package com.wuji.admin.controller;

import com.wuji.admin.model.request.RoleCreateRequest;
import com.wuji.admin.model.request.RoleListRequest;
import com.wuji.admin.model.request.RoleUpdateRequest;
import com.wuji.admin.model.vo.RoleDetailVO;
import com.wuji.admin.model.vo.RoleVO;
import com.wuji.admin.service.RoleService;
import com.wuji.common.model.vo.QueryPageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @since 2024-04-22
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation("创建role")
    @PostMapping("/create")
    public void create(@RequestBody RoleCreateRequest roleCreateRequest) {
        roleService.create(roleCreateRequest);
    }

    @ApiOperation("修改role")
    @PostMapping("/update")
    public void update(@RequestBody RoleUpdateRequest roleUpdateRequest) {
        roleService.update(roleUpdateRequest);
    }

    @ApiOperation("角色详情")
    @GetMapping("/info/{id}")
    public RoleDetailVO info(@PathVariable Long id) {
        return roleService.queryById(id);
    }

    @ApiOperation("role列表")
    @PostMapping("/queryList")
    public QueryPageVO<RoleVO> queryList(@RequestBody RoleListRequest roleListRequest) {
        return roleService.queryList(roleListRequest);
    }

    @ApiOperation("删除角色")
    @PostMapping("/delete/{id}")
    public void queryList(@PathVariable Long id) {
        roleService.deleted(id);
    }

}
