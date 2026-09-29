package com.wuji.workflow.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowDesignerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flow/designer")
public class FlowDesignerController {
    @Autowired
    private FlowDesignerService flowDesignerService;

    @ApiOperation("模型详情")
    @GetMapping("/detail/{modelId}")
    public ModelVO detail(@PathVariable String modelId) {
        return flowDesignerService.getModelJSON(modelId);
    }

    @ApiOperation("模型详情")
    @GetMapping("/detailByDeployId/{deployId}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public ModelVO detailByDeployId(@PathVariable String deployId,
                                    @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                    String companyUuid) {
        return flowDesignerService.getModelJSONByDeployId(deployId);
    }
}
