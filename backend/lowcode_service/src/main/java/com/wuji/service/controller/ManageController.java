package com.wuji.service.controller;

import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.service.model.request.ManageCreateRequest;
import com.wuji.service.model.request.ManageUpdateRequest;
import com.wuji.service.model.request.MangeListRequest;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ManageService;
import com.wuji.service.valid.ManageValidator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @ApiOperation("新建管理组")
    @PostMapping("/create")
    @ClientFunction(resourceCode = "", resourceValidator = ManageValidator.class)
    public void create(@RequestBody ManageCreateRequest manageCreateRequest) {
        manageService.create(manageCreateRequest);
    }

    @ApiOperation("修改管理组")
    @PostMapping("/update")
    @ClientFunction(resourceCode = "", resourceValidator = ManageValidator.class)
    public void update(@RequestBody ManageUpdateRequest manageUpdateRequest) {
        manageService.update(manageUpdateRequest);
    }

    @ApiOperation("管理组列表")
    @PostMapping("/manageList")
    public List<ManageVO> manageList(@RequestBody MangeListRequest mangeListRequest) {
        return manageService.manageList(mangeListRequest);
    }


    @ApiOperation("管理组详情")
    @GetMapping("/info/{id}")
    public ManageVO info(@PathVariable String id) {
        List<ManageVO> info = manageService.info(Collections.singletonList(id));
        if (CollectionUtils.isEmpty(info)) {
            return null;
        }
        return info.get(0);
    }

    @ApiOperation("删除管理组")
    @DeleteMapping("/delete/{id}")
    @ClientFunction(resourceCode = "", resourceValidator = ManageValidator.class)
    public void delete(@PathVariable String id) {
        manageService.delete(id);
    }

    @ApiOperation("当前用户所在管理组")
    @GetMapping("/currentUserInfo")
    public ManageVO currentUserInfo() {
        return manageService.currentUserInfo();
    }
}
