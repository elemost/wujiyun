package com.wuji.plugin.context;

import com.wuji.plugin.service.HttpResultService;
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
public class HttpResultContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, HttpResultService> payStrategyHandlerMap = new HashMap<>();


    public HttpResultService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    public Set<String> getAllType() {
        return payStrategyHandlerMap.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        Collection<HttpResultService> values = appContext.getBeansOfType(HttpResultService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.fieldType(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
