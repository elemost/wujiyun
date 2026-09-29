package com.wuji.admin.client.lark;

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
public class LarkFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public LarkFeignRequestInterceptor larkFeignRequestInterceptor() {
        return new LarkFeignRequestInterceptor();
    }

    @Bean
    public Decoder larkDecoder() {
        return new LarkResponseDecoder(new SpringDecoder(this.messageConverters));
    }
}