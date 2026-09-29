package com.wuji.service.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.model.request.ManageDeptSystemRequest;
import com.wuji.service.model.request.ManagePostSystemRequest;
import com.wuji.service.model.request.ManageUserSystemRequest;
import com.wuji.service.service.ManageSystemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/system")
public class ManageSystemController {

    @Autowired
    private ManageSystemService manageSystemService;

    @ApiOperation("用户管理组权限")
    @PostMapping("/user")
    public QueryPageVO<UserVO> getUserByGroupId(@RequestBody ManageUserSystemRequest manageUserSystemRequest) {
        return manageSystemService.getUserByGroupId(manageUserSystemRequest);
    }

    @ApiOperation("用户管理组权限")
    @PostMapping("/manage/user")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<UserReturnVO> getUserManageList(@RequestBody UserRequest userRequest) {
        return manageSystemService.getUserManageList(userRequest);
    }

    @ApiOperation("用户管理组权限")
    @PostMapping("/use/user")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<UserVO> getUserUseList(@RequestBody UserSelectRequest userRequest) {
        return manageSystemService.getUseList(userRequest);
    }

    @ApiOperation("部门管理组权限")
    @GetMapping("/manage/dept")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public List<DepartmentVO> getDeptManageList(@RequestParam String deptType,
                                                @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                                String companyUuid) {
        return manageSystemService.getDeptManageList(deptType);
    }

    @ApiOperation("部门管理组权限")
    @PostMapping("/dept")
    public List<DepartmentVO> getDeptSelectList(@RequestBody ManageDeptSystemRequest manageDeptSystemRequest) {
        return manageSystemService.getDeptSelectList(manageDeptSystemRequest);
    }

    @ApiOperation("职位管理组权限")
    @PostMapping("/post")
    public QueryPageVO<PostVO> getPostSelectList(@RequestBody ManagePostSystemRequest managePostSystemRequest) {
        return manageSystemService.getPostSelectList(managePostSystemRequest);
    }
}
