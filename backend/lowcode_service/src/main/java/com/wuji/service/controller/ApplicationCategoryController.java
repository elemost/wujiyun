package com.wuji.service.controller;


import com.wuji.common.model.Response;
import com.wuji.service.model.request.ApplicationCategoryCreateRequest;
import com.wuji.service.model.request.ApplicationCategorySortRequest;
import com.wuji.service.model.request.ApplicationCategoryTypeTransRequest;
import com.wuji.service.model.request.ApplicationCategoryUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.service.ApplicationCategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 应用目录 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@RestController
@RequestMapping("/application/category")
public class ApplicationCategoryController {

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @ApiOperation("创建应用目录")
    @PostMapping("/create")
    public Response<String> create(@RequestBody ApplicationCategoryCreateRequest applicationCategoryCreateRequest) {
        return Response.success(applicationCategoryService.create(applicationCategoryCreateRequest, true));
    }

    @ApiOperation("修改应用目录")
    @PutMapping("/update")
    public void update(@RequestBody ApplicationCategoryUpdateRequest applicationCategoryUpdateRequest) {
        applicationCategoryService.updateCategory(applicationCategoryUpdateRequest);
    }

    @ApiOperation("显示隐藏")
    @GetMapping("/updateShowType/{id}")
    public void updateShowType(@PathVariable String id, @RequestParam String showType,
                               @RequestParam("applicationId") String applicationId) {
        applicationCategoryService.updateShowType(id, showType, applicationId);
    }

    @ApiOperation("删除目录")
    @DeleteMapping("/deleteCategory/{id}")
    public void deleteCategory(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        applicationCategoryService.deleteCategory(id, applicationId);
    }

    @ApiOperation("应用目录列表")
    @GetMapping("/selectList/{applicationId}")
    public List<ApplicationCategoryVO> selectList(@PathVariable String applicationId) {
        return applicationCategoryService.selectTree(Collections.singletonList(applicationId), Boolean.FALSE);
    }

    @ApiOperation("应用目录列表")
    @GetMapping("/quoteList/{applicationId}")
    public List<ApplicationCategoryVO> quoteList(@PathVariable String applicationId) {
        return applicationCategoryService.quoteList(Collections.singletonList(applicationId), Boolean.FALSE);
    }

    @ApiOperation("应用目录发布列表")
    @GetMapping("/selectPublishList/{applicationId}")
    public List<ApplicationCategoryVO> selectPublishList(@PathVariable String applicationId) {
        return applicationCategoryService.selectTree(Collections.singletonList(applicationId), Boolean.TRUE);
    }


    @ApiOperation("发布表单")
    @PutMapping("/publish/{id}")
    public void publish(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        applicationCategoryService.publish(id, applicationId);
    }

    @ApiOperation("流程表单列表")
    @GetMapping("/flowable/form/{applicationId}")
    public List<ApplicationCategoryVO> getCategoryFlowableForm(@PathVariable String applicationId) {
        return applicationCategoryService.getCategoryFlowableForm(Collections.singletonList(applicationId), null,
                false);
    }

    @ApiOperation("最近使用的表单")
    @GetMapping("/latestUseForm")
    public List<ApplicationCategoryVO> getCategoryFlowableForm(@RequestParam("limitCount") Integer limitCount) {
        return applicationCategoryService.latestUseForm(limitCount);
    }

    @ApiOperation("表单排序")
    @PostMapping("/sort")
    public void sortCategory(@RequestBody ApplicationCategorySortRequest applicationCategorySortRequest) {
        applicationCategoryService.sortCategory(applicationCategorySortRequest);
    }

    @ApiOperation("表单转化成流程表单")
    @PostMapping("/transToFlowable")
    public void transToFlowable(@RequestBody ApplicationCategoryTypeTransRequest applicationCategoryTypeTransRequest) {
        applicationCategoryService.transToFlowable(applicationCategoryTypeTransRequest);
    }


    @ApiOperation("表单复制")
    @GetMapping("/copy/{categoryId}")
    public Response<String> copy(@PathVariable String categoryId, @RequestParam("applicationId") String applicationId) {
        return Response.success(applicationCategoryService.copy(categoryId, applicationId));
    }


}
