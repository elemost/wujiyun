package com.wuji.admin.client.qcc;

import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class QccFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public QccFeignRequestInterceptor qccFeignRequestInterceptor() {
        return new QccFeignRequestInterceptor();
    }

    @Bean
    public Decoder qccDecoder() {
        return new QccResponseDecoder(new SpringDecoder(this.messageConverters));
    }
}