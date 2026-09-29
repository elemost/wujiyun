package com.wuji.service.controller;

import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.model.vo.DataFactoryStageAllFieldVO;
import com.wuji.common.privilege.annotation.Resource;
import com.wuji.common.privilege.annotation.ResourceId;
import com.wuji.common.privilege.annotation.Secure;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.converter.AbstractFormConfigCommonConverter;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.request.FormFieldMoreRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormFieldMappingVO;
import com.wuji.service.model.vo.FormFieldVO;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormFieldService;
import com.wuji.service.service.FormService;
import com.wuji.service.valid.ApplicationValidator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/form")
public class FormFieldController {

    @Autowired
    private FormFieldService formFieldService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @ApiOperation("field")
    @GetMapping("/field/mapping")
    public List<FormFieldMappingVO> getFieldMapping() {
        return formFieldService.getFieldMapping();
    }

    @ApiOperation("表单字段")
    @GetMapping("/field/{id}")
    @Secure(@Resource(identifierLocation = IdentifierLocationEnum.REQ_PRAM,
            resourceValidator = ApplicationValidator.class))
    public List<FormConfigCommon> fieldList(@PathVariable String id,
                                            @RequestParam("containSystem") Boolean containSystem,
                                            @RequestParam("applicationId") @ResourceId String applicationId) {
        return formService.getFormConfigCommonList(id, containSystem, applicationId).getFields();
    }

    @ApiOperation("表单字段")
    @GetMapping("/fieldExistName/{id}")
    public FieldExistNameVO fieldExistName(@PathVariable String id,
                                           @RequestParam("containSystem") Boolean containSystem,
                                           @RequestParam("applicationId") String applicationId) {
        return formService.getFormConfigCommonList(id, containSystem, applicationId);
    }

    @ApiOperation("获取所有表单字段")
    @GetMapping("/getAllFormField/{applicationId}")
    public List<FormFieldVO> getAllFormField(@PathVariable String applicationId,
                                             @RequestParam(required = false) Boolean needSystemField) {
        Integer defaultFieldType = needSystemField != null && needSystemField ? 1 : 0;
        return formService.getAllFormFieldVO(applicationId, null, defaultFieldType);
    }

    @ApiOperation("获取所有表单字段")
    @PostMapping("/getByIds")
    public Map<String, FieldExistNameVO> getAllFormField(
            @RequestBody List<FormFieldMoreRequest> formFieldMoreRequests) {
        if (CollectionUtils.isEmpty(formFieldMoreRequests)) {
            return Collections.emptyMap();
        }
        List<String> formIds =
                formFieldMoreRequests.stream().map(FormFieldMoreRequest::getFormId).collect(Collectors.toList());
        List<String> applicationIds =
                formFieldMoreRequests.stream().map(FormFieldMoreRequest::getApplicationId).collect(Collectors.toList());
        List<FieldExistNameVO> allFormFieldVO = formService.getAllFormFieldVO(applicationIds, formIds, false, false);
        return allFormFieldVO.stream()
                .collect(Collectors.toMap(c -> c.getApplicationId() + "_" + c.getFormId(), c -> c));
    }

    @ApiOperation("获取所有表单字段")
    @GetMapping("/getFlowableField/{applicationId}")
    public FormFieldVO getAllFlowableField(@PathVariable String applicationId, @RequestParam("formId") String formId) {
        return formService.getAllFormFieldVO(applicationId, Collections.singletonList(formId), 2).get(0);
    }


    @ApiOperation("获取所有表单字段")
    @GetMapping("/getAll/{applicationId}")
    public Map<String, List<FormFieldVO>> getAll(@PathVariable String applicationId) {
        List<FormFieldVO> allFormFieldVO = formService.getAllFormFieldVO(applicationId, null, 0);
        Map<String, List<FormFieldVO>> allFromFieldMap = new HashMap<>();
        allFromFieldMap.put("form", allFormFieldVO);
        List<FormFieldVO> fieldByApplicationId = formAggregateService.getFieldByApplicationId(applicationId);
        allFromFieldMap.put("agg", fieldByApplicationId);
        List<DataFactoryStageAllFieldVO> allFinalFields = formDataFactoryExecuteApi.getAllFinalField(applicationId);
        List<FormFieldVO> factory = new ArrayList<>();
        for (DataFactoryStageAllFieldVO dataFactoryStageAllFieldVO : allFinalFields) {
            List<FormConfigCommon> formConfigCommonList = dataFactoryStageAllFieldVO.getFields().stream()
                    .map(AbstractFormConfigCommonConverter.INSTANCE::toConfig).collect(Collectors.toList());
            FormFieldVO formFieldVO = new FormFieldVO();
            formFieldVO.setFields(formConfigCommonList);
            formFieldVO.setId(dataFactoryStageAllFieldVO.getId());
            formFieldVO.setCategoryName(dataFactoryStageAllFieldVO.getName());
            factory.add(formFieldVO);
        }
        allFromFieldMap.put("fac", factory);
        return allFromFieldMap;
    }

    @ApiOperation("获取所有表单字段")
    @GetMapping("/getFormFieldById/{applicationId}")
    public List<FormFieldVO> getAllFormField(@PathVariable String applicationId,
                                             @RequestParam("idList") List<String> idList) {
        return formService.getAllFormFieldVO(applicationId, idList, 0);
    }
}
