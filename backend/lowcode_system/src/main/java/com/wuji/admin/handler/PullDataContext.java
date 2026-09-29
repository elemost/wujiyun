package com.wuji.admin.handler;

import com.wuji.admin.service.OrganizePullDataService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录上下文
 * 定义上下文
 *
 * @author LPiu
 * @date 2023/12/20
 */
@Component
public class PullDataContext implements InitializingBean, ApplicationContextAware {

    private ApplicationContext appContext;
    private final Map<String, OrganizePullDataService> payStrategyHandlerMap = new HashMap<>();


    public OrganizePullDataService getHandler(String type) {
        return payStrategyHandlerMap.get(type);
    }

    @Override
    public void afterPropertiesSet() {
        Collection<OrganizePullDataService> values = appContext.getBeansOfType(OrganizePullDataService.class).values();
        values.forEach(loginStrategy -> payStrategyHandlerMap.put(loginStrategy.dataSource(), loginStrategy));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
