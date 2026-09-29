package com.wuji.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring容器工具类，一定要放在该工程的第一个package里，否则会导致Flyway的Java脚本无法执行
 *
 * @author hzm
 * @since 2024-3-11
 */
@Component
public class ToolSpring implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static ApplicationContext _ctx;

    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    public static <T> T getBean(Class<T> clazz) {
        return _ctx.getBean(clazz);
    }


    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    public static <T> T getBeanByNameAndType(String name, Class<T> clazz) {
        return _ctx.getBean(name, clazz);
    }

    public static String getProperty(String key) {
        return _ctx.getEnvironment().getProperty(key);
    }

    public static Object autowire(Object target) {
        _ctx.getAutowireCapableBeanFactory().autowireBean(target);
        return target;
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return _ctx.getBeansOfType(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        _ctx = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static boolean hasApplicationContext() {
        return null != _ctx;
    }
}

