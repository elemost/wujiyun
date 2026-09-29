package com.wuji.plugin.context;

import com.wuji.plugin.service.AuthService;
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
public class AuthContext implements InitializingBean, ApplicationContextAware {
    private ApplicationContext appContext;
    private final Map<String, AuthService> payStrategyHandlerMap = new HashMap<>();


    public AuthService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    public Set<String> getAllType() {
        return payStrategyHandlerMap.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        Collection<AuthService> values = appContext.getBeansOfType(AuthService.class).values();
        values.forEach(dataStream -> payStrategyHandlerMap.put(dataStream.authType(), dataStream));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
