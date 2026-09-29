package com.wuji.common.context;

import com.wuji.common.service.UploadFileService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UploadFileContext implements InitializingBean, ApplicationContextAware {

    private ApplicationContext appContext;
    private final Map<String, UploadFileService> payStrategyHandlerMap = new HashMap<>();


    public UploadFileService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    @Override
    public void afterPropertiesSet() {
        Collection<UploadFileService> values = appContext.getBeansOfType(UploadFileService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.type(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}

