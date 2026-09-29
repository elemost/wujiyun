package com.wuji.service.context;

import com.wuji.service.service.FormFunctionDataService;
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
public class FormFunctionDataContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, FormFunctionDataService> payStrategyHandlerMap = new HashMap<>();


    public FormFunctionDataService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    public Set<String> getAllType() {
        return payStrategyHandlerMap.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        Collection<FormFunctionDataService> values = appContext.getBeansOfType(FormFunctionDataService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.fieldType(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
