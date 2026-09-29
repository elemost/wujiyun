package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.model.vo.FormExtraFunctionInfoPrivilegeVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.service.FormExtraFunctionInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form/extra/function/info")
public class FormExtraFunctionInfoController {

    @Autowired
    private FormExtraFunctionInfoService formExtraFunctionInfoService;

    @ApiOperation("创建")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        formExtraFunctionCreateRequest.setFunctionType(FormExtraFunctionTypeEnum.INFO.name());
        String id = formExtraFunctionInfoService.create(formExtraFunctionCreateRequest);
        return Response.success(id);
    }

    @ApiOperation("编辑")
    @PostMapping("/update")
    public void update(@RequestBody FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest) {
        formExtraFunctionUpdateRequest.setFunctionType(FormExtraFunctionTypeEnum.INFO.name());
        formExtraFunctionInfoService.update(formExtraFunctionUpdateRequest);
    }

    @ApiOperation("通过表单id获取数据")
    @GetMapping("/getByFormId/{applicationId}/{formId}")
    public List<FormExtraFunctionVO> getByFormId(@PathVariable String formId, @PathVariable String applicationId) {
        return formExtraFunctionInfoService.getByFormId(formId, applicationId, FormExtraFunctionTypeEnum.INFO.name());
    }

    @ApiOperation("通过表单id获取数据")
    @GetMapping("/privilegeList/{id}")
    public List<FormExtraFunctionInfoPrivilegeVO> privilegeList(@PathVariable String id) {
        return formExtraFunctionInfoService.privilegeList(id);
    }

}
