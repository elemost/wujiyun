package com.wuji.plugin.context;

import com.wuji.plugin.service.PluginUseService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class PluginContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, PluginUseService> payStrategyHandlerMap = new HashMap<>();


    public PluginUseService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    public Set<String> getAllType() {
        return payStrategyHandlerMap.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        Collection<PluginUseService> values = appContext.getBeansOfType(PluginUseService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.pluginType(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
