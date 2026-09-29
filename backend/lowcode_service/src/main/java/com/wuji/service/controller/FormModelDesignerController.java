package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormModelDesignerRequest;
import com.wuji.service.service.FormModelDesignerService;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/form/model/designer")
public class FormModelDesignerController {
    @Autowired
    private FormModelDesignerService formModelDesignerService;

    @ApiOperation("设计模型")
    @PostMapping("/designModel")
    public Response<String> designMode(@RequestBody FormModelDesignerRequest formModelDesignerRequest) {
        return Response.success(formModelDesignerService.designModel(formModelDesignerRequest));
    }

    @ApiOperation("流程配置")
    @GetMapping("/activity/config/{formId}")
    public List<FlowableActivityConfigDomain> activityConfig(@PathVariable String formId,
                                                             @RequestParam("applicationId") String applicationId,
                                                             @RequestParam("dataUuid") String dataUuid) {
        return formModelDesignerService.getByFormId(formId, dataUuid, applicationId).stream()
                .filter(c -> "subFlowTask".equals(c.getActivityType())).collect(Collectors.toList());
    }

    @ApiOperation("流程配置")
    @GetMapping("/getPublishModelConfig/{formId}")
    public List<FlowableActivityConfigDomain> publishModelConfig(@PathVariable String formId,
                                                                 @RequestParam("applicationId") String applicationId) {
        return formModelDesignerService.getPublishModelConfig(formId, applicationId);
    }
}
