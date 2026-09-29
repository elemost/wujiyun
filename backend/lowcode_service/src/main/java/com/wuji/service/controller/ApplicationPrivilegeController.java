package com.wuji.service.controller;

import com.wuji.service.enums.ApplicationPrivilegeTypeEnum;
import com.wuji.service.model.request.ApplicationPrivilegeSaveRequest;
import com.wuji.service.model.vo.ApplicationPrivilegeVO;
import com.wuji.service.service.ApplicationPrivilegeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
 * @since 2024-10-14
 */
@RestController
@RequestMapping("/application/privilege")
public class ApplicationPrivilegeController {

    @Autowired
    private ApplicationPrivilegeService applicationPrivilegeService;

    @ApiOperation("保存应用权限")
    @PostMapping("/save/{applicationId}")
    public void save(@RequestBody List<ApplicationPrivilegeSaveRequest> applicationPrivilegeSaveList,
                     @PathVariable String applicationId) {
        applicationPrivilegeService.save(applicationId, ApplicationPrivilegeTypeEnum.VIEW.name(),
                applicationPrivilegeSaveList);
    }

    @ApiOperation("获取应用权限详情")
    @GetMapping("/getPrivilegeList/{applicationId}")
    public List<ApplicationPrivilegeVO> getPriviegeList(@PathVariable String applicationId) {
        return applicationPrivilegeService.getPriviegeList(applicationId);
    }


}
