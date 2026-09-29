package com.wuji.service.controller;

import com.wuji.service.model.request.ApplicationUserSortSaveRequest;
import com.wuji.service.model.vo.ApplicationUserSortVO;
import com.wuji.service.service.ApplicationUserSortService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
 * @since 2024-12-02
 */
@RestController
@RequestMapping("/application/user/sort")
public class ApplicationUserSortController {
    @Autowired
    private ApplicationUserSortService applicationUserSortService;

    @ApiOperation("保存应用排序")
    @PostMapping("/save")
    public void save(@RequestBody ApplicationUserSortSaveRequest applicationUserSortSaveRequest) {
        applicationUserSortService.saveSort(applicationUserSortSaveRequest);
    }

    @ApiOperation("自己的排序列表")
    @GetMapping("/ownList")
    public List<ApplicationUserSortVO> ownList() {
        return applicationUserSortService.ownList();
    }
}
