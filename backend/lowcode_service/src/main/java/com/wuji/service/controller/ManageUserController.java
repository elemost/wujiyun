package com.wuji.service.controller;

import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.model.Response;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.model.request.ManageUserRequest;
import com.wuji.service.model.vo.ManageUserVO;
import com.wuji.service.service.ManageUserService;
import com.wuji.service.valid.ManageValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
@RestController
@RequestMapping("/manage/user")
public class ManageUserController {

    @Autowired
    private ManageUserService manageUserService;

    @Autowired
    private UserCompanyService userCompanyService;

    @ApiOperation("修改管理员")
    @PostMapping("/update")
    @ClientFunction(resourceCode = "", resourceValidator = ManageValidator.class)
    public Response update(@RequestBody ManageUserRequest manageUserRequest) {
        Map<String, Object> returnMap = new HashMap<>();
        Long admin = userCompanyService.getAdmin(UserUtils.getUser().getCompanyId());
        if (manageUserRequest.getUserIdList().contains(admin)) {
            manageUserRequest.getUserIdList().remove(admin);
            UserCompanyVO userCompanyVO =
                    userCompanyService.getByUserIdAndCompanyId(admin, UserUtils.getUser().getCompanyId());
            if (userCompanyVO != null) {
                returnMap.put("adminUserId", admin);
                returnMap.put("adminUser",
                        userCompanyVO.getNickName() + "是企业创建者, 拥有所有权限, 无需添加系统管理员!");
            }
        }
        List<Long> update = manageUserService.update(manageUserRequest.getGroupId(), manageUserRequest.getUserIdList());
        Response success = Response.success();
        returnMap.put("sameUserId", update);
        success.setOtherData(returnMap);
        return success;
    }

    @ApiOperation("获取对应管理组用户")
    @GetMapping("/getByGroupId/{groupId}")
    public List<ManageUserVO> getByGroupIds(@PathVariable String groupId) {
        return manageUserService.getByGroupIds(Collections.singletonList(groupId));
    }

    @ApiOperation("超级管理员数据修复")
    @PostMapping("/dealData")
    public void dealData() {
        manageUserService.dealData();
    }

}
