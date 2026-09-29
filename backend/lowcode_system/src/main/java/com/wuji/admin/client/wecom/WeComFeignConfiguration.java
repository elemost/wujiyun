package com.wuji.admin.client.wecom;

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
public class WeComFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public WeComFeignRequestInterceptor weComFeignRequestInterceptor() {
        return new WeComFeignRequestInterceptor();
    }

    @Bean
    public Decoder weComDecoder() {
        return new WeComResponseDecoder(new SpringDecoder(this.messageConverters));
    }
}