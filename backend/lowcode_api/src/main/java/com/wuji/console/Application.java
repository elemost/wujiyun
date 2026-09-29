package com.wuji.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * application
 */
@MapperScan("com.wuji.**.mapper")
@EnableFeignClients(value = {"com.wuji.admin", "com.wuji.service", "com.wuji.systemapi"})
@ComponentScan(value = {"com.wuji.common.**", "com.wuji.admin.**", "com.wuji.framework.**", "com.wuji.console.**",
        "com.wuji.workflow.**", "com.wuji.service.**", "com.wuji.wechat.**", "com.wuji.message.**",
        "com.wuji.quartz.**", "com.wuji.open.**", "com.wuji.platform.**", "com.wuji.plugin.**", "com.wuji.factory.**",
        "com.wuji.systemapi.**"})
@SpringBootApplication
@ServletComponentScan
@EnableAsync
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

