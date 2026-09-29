package com.wuji.service.controller;

import com.wuji.service.model.request.FormPrivilegeGroupSaveRequest;
import com.wuji.service.service.FormPrivilegeUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-10-15
 */
@RestController
@RequestMapping("/form/privilege/user")
public class FormPrivilegeUserController {

    @Autowired
    private FormPrivilegeUserService formPrivilegeUserService;

    @ApiOperation("保存根据部门角色保存权限")
    @PostMapping("/saveByBusinessId/{businessId}/{businessType}")
    public void saveByBusinessId(@PathVariable String businessId, @PathVariable String businessType,
                                 @RequestBody List<FormPrivilegeGroupSaveRequest> formPrivilegeGroupSaveRequests) {
        formPrivilegeUserService.savePrivilegeByBusinessId(businessId, businessType, formPrivilegeGroupSaveRequests);
    }

}
