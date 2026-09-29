package com.wuji.message.context;

import com.wuji.message.service.MessageSendService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, MessageSendService> payStrategyHandlerMap = new HashMap<>();


    public MessageSendService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    @Override
    public void afterPropertiesSet() {
        Collection<MessageSendService> values = appContext.getBeansOfType(MessageSendService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.sendPlatform(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
