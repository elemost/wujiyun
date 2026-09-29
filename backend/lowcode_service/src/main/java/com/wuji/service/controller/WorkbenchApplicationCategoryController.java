package com.wuji.service.controller;

import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.service.ApplicationCategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workbench/application/category")
public class WorkbenchApplicationCategoryController {

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @ApiOperation("应用目录列表")
    @GetMapping("/selectList/{applicationId}")
    public List<ApplicationCategoryVO> selectTreePrivilege(@PathVariable String applicationId) {
        return applicationCategoryService.selectTreePrivilege(applicationId, Boolean.TRUE);
    }
}
