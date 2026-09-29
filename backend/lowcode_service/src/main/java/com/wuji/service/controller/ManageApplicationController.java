package com.wuji.service.controller;

import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.service.model.request.ManageApplicationRequest;
import com.wuji.service.service.ManageApplicationService;
import com.wuji.service.valid.ManageValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
@RestController
@RequestMapping("/manage/application")
public class ManageApplicationController {

    @Autowired
    private ManageApplicationService manageApplicationService;

    @ApiOperation("修改应用")
    @PostMapping("/update")
    @ClientFunction(resourceCode = "", resourceValidator = ManageValidator.class)
    public void update(@RequestBody ManageApplicationRequest manageApplicationRequest) {
        manageApplicationService.update(manageApplicationRequest.getGroupId(),
                manageApplicationRequest.getApplicationIdList());
    }
}
