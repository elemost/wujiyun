package com.wuji.service.client;

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
public class PluginFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public PluginFeignRequestInterceptor pluginFeignRequestInterceptor() {
        return new PluginFeignRequestInterceptor();
    }

    @Bean
    public Decoder pluginDecoder() {
        return new PluginResponseDecoder(new SpringDecoder(this.messageConverters));
    }
}