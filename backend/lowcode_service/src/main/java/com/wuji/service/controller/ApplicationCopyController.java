package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.model.request.ApplicationCopyRequest;
import com.wuji.service.service.ApplicationCopyService;
import com.wuji.service.valid.ApplicationCountValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/application")
public class ApplicationCopyController {

    @Autowired
    private ApplicationCopyService applicationCopyService;

    @ApiOperation("复制应用")
    @PostMapping("/copy")
    @ClientFunction(resourceCode = ResourceCodeConstants.APPLICATION_COUNT,
            resourceValidator = ApplicationCountValidator.class)
    public Response<String> copy(@RequestBody ApplicationCopyRequest applicationCopyRequest) {
        return Response.success(applicationCopyService.copy(applicationCopyRequest));
    }

    @ApiOperation("分享应用")
    @PostMapping("/share")
    @ClientFunction(resourceCode = ResourceCodeConstants.APPLICATION_COUNT,
            resourceValidator = ApplicationCountValidator.class)
    public Response<String> share(@RequestBody ApplicationCopyRequest applicationCopyRequest) {
        return Response.success(applicationCopyService.share(applicationCopyRequest));
    }
}
