package com.wuji.plugin.controller;

import com.wuji.common.model.Response;
import com.wuji.plugin.model.request.PluginUseRequest;
import com.wuji.plugin.service.CompanyPluginService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugin/use")
public class PluginUseController {

    @Autowired
    private CompanyPluginService companyPluginService;

    @ApiOperation("插件使用")
    @PostMapping("")
    public Response<Object> create(@RequestBody PluginUseRequest pluginUseRequest) {
        return Response.success(
                companyPluginService.usePlugin(pluginUseRequest.getPluginId(), pluginUseRequest.getPluginMapping()));
    }
}
