package com.wuji.service.client;

import com.wuji.plugin.model.request.PluginUseRequest;
import com.wuji.service.client.model.PluginUseDirectRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pluginClient", url = "${plugin.server:http://127.0.0.1:9010/plugin/api/v1/open/api}",
        configuration = PluginFeignConfiguration.class)
public interface PluginClient {

    @PostMapping("/plugin/use")
    PluginResult<Object> pluginUse(@RequestBody PluginUseRequest pluginUseRequest);

    @PostMapping("/plugin/use/direct")
    PluginResult<Object> pluginUseDirect(@RequestBody PluginUseDirectRequest pluginUseDirectRequest);

}
