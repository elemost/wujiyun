package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormPrivilegeCreateRequest;
import com.wuji.service.model.request.FormPrivilegeSortRequest;
import com.wuji.service.model.request.FormPrivilegeUpdateRequest;
import com.wuji.service.model.vo.FormPrivilegeConfigVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.service.FormPrivilegeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/form/privilege")
public class FormPrivilegeController {

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @ApiOperation("权限创建")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormPrivilegeCreateRequest formPrivilegeCreateRequest) {
        return Response.success(formPrivilegeService.create(formPrivilegeCreateRequest));
    }

    @ApiOperation("权限修改")
    @PostMapping("/update")
    public void update(@RequestBody FormPrivilegeUpdateRequest formPrivilegeUpdateRequest) {
        formPrivilegeService.update(formPrivilegeUpdateRequest);
    }

    @ApiOperation("权限列表")
    @GetMapping("/getList/{categoryId}")
    public List<FormPrivilegeVO> getList(@PathVariable String categoryId,
                                         @RequestParam("applicationId") String applicationId) {
        return formPrivilegeService.getList(categoryId, applicationId);
    }

    @ApiOperation("权限列表")
    @GetMapping("/getSelectList")
    public List<FormPrivilegeVO> getSelectList(@RequestParam("applicationId") String applicationId) {
        return formPrivilegeService.getSelectList(applicationId);
    }

    @ApiOperation("权限删除")
    @GetMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        formPrivilegeService.delete(id);
    }


    @ApiOperation("权限详情")
    @GetMapping("/detail/{id}")
    public FormPrivilegeVO detail(@PathVariable String id) {
        return formPrivilegeService.detail(id);
    }


    @ApiOperation("权限详情")
    @GetMapping("/getOwnerPrivilege/{categoryId}")
    public List<FormPrivilegeConfigVO> getPrivilege(@PathVariable String categoryId,
                                                    @RequestParam("applicationId") String applicationId) {
        return formPrivilegeService.getPrivilege(categoryId, applicationId);
    }

    @ApiOperation("处理数据")
    @PostMapping("/dealData/{applicationId}")
    public void dealData(@PathVariable String applicationId) {
        formPrivilegeService.dealData(applicationId);
    }

    @ApiOperation("处理数据")
    @PostMapping("/dealDataAll")
    public void dealDataAll() {
        formPrivilegeService.dealDataAll();
    }

    @ApiOperation("处理数据")
    @PostMapping("/sort")
    public void sort(@RequestBody FormPrivilegeSortRequest formPrivilegeSortRequest) {
        formPrivilegeService.sort(formPrivilegeSortRequest);
    }
}
