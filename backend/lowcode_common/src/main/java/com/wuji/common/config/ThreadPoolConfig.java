package com.wuji.common.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean("dataStreamExecutor")
    @Primary
    public ThreadPoolExecutor dataStreamExecutor() {
        return new ThreadPoolExecutor(10, 50, 10, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100));
    }

    @Bean("mongoTaskExecutor")
    public ThreadPoolExecutor mongoTaskExecutor() {
        return new ThreadPoolExecutor(10, 50, 10, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100));
    }

    @Bean("messageExecutor")
    public ThreadPoolExecutor messageExecutor() {
        return new ThreadPoolExecutor(10, 50, 10, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100));
    }

    @Bean("dataStreamScheduledExecutor")
    public ScheduledExecutorService dataStreamScheduledExecutor() {
        return Executors.newScheduledThreadPool(20);
    }


    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        // 包装为TTL线程池
        return TtlExecutors.getTtlExecutorService(executor.getThreadPoolExecutor());
    }
}
