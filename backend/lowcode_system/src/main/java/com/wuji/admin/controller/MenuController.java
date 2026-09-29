package com.wuji.admin.controller;

import com.wuji.admin.model.request.MenuCreateRequest;
import com.wuji.admin.model.request.MenuUpdateRequest;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.vo.MenuVO;
import com.wuji.admin.model.vo.RouterVO;
import com.wuji.admin.service.MenuService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
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
 * 菜单权限表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-04-20
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation("创建menu")
    @PostMapping("/create")
    public void create(@RequestBody MenuCreateRequest menuCreateRequest) {
        menuService.create(menuCreateRequest);
    }

    @ApiOperation("修改menu")
    @PostMapping("/update")
    public void update(@RequestBody MenuUpdateRequest menuUpdateRequest) {
        menuService.update(menuUpdateRequest);
    }

    @ApiOperation("菜单配置列表")
    @PostMapping("/queryAllMenu")
    public List<MenuVO> queryAllMenuByClientId() {
        return menuService.queryAllMenuByClientId();
    }

    @ApiOperation("获取当前菜单")
    @PostMapping("/queryCurrentMenu")
    public List<RouterVO> queryCurrentMenu() {
        return menuService.queryCurrentMenu();
    }
}
