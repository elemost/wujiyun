package com.wuji.systemapi.client.user;

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
public class SystemFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public SystemFeignRequestInterceptor systemFeignRequestInterceptor() {
        return new SystemFeignRequestInterceptor();
    }

    @Bean
    public Decoder systemDecoder() {
        return new SystemResponseDecoder(new SpringDecoder(this.messageConverters));
    }
}