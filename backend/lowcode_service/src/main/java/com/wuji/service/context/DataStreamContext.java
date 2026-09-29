package com.wuji.service.context;

import com.wuji.service.service.FormDataStreamExecuteService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataStreamContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, FormDataStreamExecuteService> payStrategyHandlerMap = new HashMap<>();


    public FormDataStreamExecuteService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    @Override
    public void afterPropertiesSet() {
        Collection<FormDataStreamExecuteService> values = appContext.getBeansOfType(FormDataStreamExecuteService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.nodeType(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
