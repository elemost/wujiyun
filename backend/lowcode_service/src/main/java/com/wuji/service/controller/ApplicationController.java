package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.annotation.ApplicationId;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.privilege.annotation.Resource;
import com.wuji.common.privilege.annotation.ResourceId;
import com.wuji.common.privilege.annotation.Secure;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.model.request.ApplicationCreateRequest;
import com.wuji.service.model.request.ApplicationOwnerRequest;
import com.wuji.service.model.request.ApplicationQueryRequest;
import com.wuji.service.model.request.ApplicationUpdateRequest;
import com.wuji.service.model.vo.ApplicationFlowableVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.CategoryPrivilegeVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.valid.ApplicationCountValidator;
import com.wuji.service.valid.ApplicationDeleteValidator;
import com.wuji.service.valid.ApplicationValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 应用 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @ApiOperation("创建应用")
    @PostMapping("/create")
    @ClientFunction(resourceCode = ResourceCodeConstants.APPLICATION_COUNT,
            resourceValidator = ApplicationCountValidator.class)
    public Response<String> create(@RequestBody ApplicationCreateRequest applicationCreateRequest) {
        return Response.success(applicationService.create(applicationCreateRequest));
    }

    @ApiOperation("修改应用")
    @PutMapping("/update")
    public void update(@RequestBody ApplicationUpdateRequest applicationUpdateRequest) {
        applicationService.update(applicationUpdateRequest);
    }

    @ApiOperation("应用详情")
    @GetMapping("/detail/{id}")
    @Secure(@Resource(identifierLocation = IdentifierLocationEnum.PATH_VAR,
            resourceValidator = ApplicationValidator.class))
    public ApplicationVO detail(@PathVariable @ResourceId String id) {
        return applicationService.detail(id);
    }

    @ApiOperation("应用列表")
    @PostMapping("/queryList")
    public QueryPageVO<ApplicationVO> queryList(@RequestBody ApplicationQueryRequest applicationQueryRequest) {
        return applicationService.queryList(applicationQueryRequest);
    }

    @ApiOperation("删除应用")
    @PutMapping("/delete/{id}")
    @Secure(@Resource(applicationLocation = IdentifierLocationEnum.PATH_VAR,
            resourceValidator = ApplicationDeleteValidator.class))
    public void delete(@PathVariable @ApplicationId String id) {
        applicationService.delete(id);
    }

    @ApiOperation("应用上下架")
    @PutMapping("/updateState/{id}")
    public void updateState(@PathVariable String id, @RequestParam("state") String state) {
        applicationService.updateState(id, state);
    }

    @ApiOperation("最近使用")
    @GetMapping("/latestUseApplication")
    public List<ApplicationVO> latestUseApplication(@RequestParam Integer limitCount) {
        return applicationService.latestUseApplication(limitCount);
    }

    @ApiOperation("我的应用")
    @GetMapping("/myApplication")
    public List<ApplicationVO> getMyApplication(ApplicationOwnerRequest applicationOwnerRequest) {
        return applicationService.getMyApplication(applicationOwnerRequest);
    }

    @ApiOperation("我的应用")
    @GetMapping("/myAllPrivilege")
    public List<CategoryPrivilegeVO> getMyApplication() {
        return applicationService.getMyPrivilege();
    }

    @ApiOperation("获取公司所有应用下流程")
    @GetMapping("/getAllApplicationFlowable")
    public List<ApplicationFlowableVO> getAllApplicationFlowable() {
        return applicationService.getAllApplicationFlowable();
    }

    @ApiOperation("获取应用下目录")
    @GetMapping("/getAllForm")
    public List<ApplicationFlowableVO> getAllForm() {
        return applicationService.getAllApplicationForm();
    }

    @ApiOperation("根据模版id获取应用")
    @GetMapping("/getByTemplateId/{templateId}")
    public ApplicationVO getByTemplateId(@PathVariable String templateId) {
        return applicationService.getByTemplateId(templateId);
    }

}

