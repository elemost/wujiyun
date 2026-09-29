package com.wuji.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * feign发送请求前要做的操作。比如生成认证、签名、请求头等信息
 * @author hzm
 * @since 2024-03-04
 */
@Slf4j
public abstract class AbstractFeignRequestInterceptor implements RequestInterceptor {
    public AbstractFeignRequestInterceptor() {}

    @Override
    public void apply(RequestTemplate template) {
        if (StringUtils.startsWith(template.feignTarget().url(), server())) {
            doApply(template);
        }
    }

    /**
     * 真实请求路径，打印日志时会用到。
     * 默认为Client上定义的url+方法url，但某些Client请求时会根据系统配置来决定真实请求路径，此种情况需要自己重写该方法生成真实请求路径
     * @param template 请求模板
     * @return
     */
    protected String realUrl(RequestTemplate template) {
        return template.feignTarget().url() + template.url();
    }

    /**
     * 发送请求前要做的操作
     * @param template
     */
    abstract protected void doApply(RequestTemplate template);

    /**
     * 外部系统名称。如CSP、DXP等
     * @return
     */
    abstract protected String serverName();

    /**
     * 外部系统服务地址。该方法返回的地址分两种情况
     * 1、如果服务地址为配置文件中的固定值，则该方法返回的地址到端口即可。如http://core.dbconnection-prod:8888
     * 2、如果服务地址为动态地址，例如从数据库中读取，则该方法返回的地址格式应为"http://{feignClientName}"，其中feignClientName为Client上@FeignClient
     * 注解的name属性的值，且@FeignClient注解的url属性不能配置
     * @return
     */
    abstract protected String server();
}
