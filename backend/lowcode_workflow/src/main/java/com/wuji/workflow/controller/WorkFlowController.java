package com.wuji.workflow.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.workflow.model.vo.FlowableDetailVO;
import com.wuji.workflow.service.WorkFlowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;

    @ApiOperation("审批详情")
    @GetMapping("/processInstanceDetail/{processInstanceId}")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FlowableDetailVO getProcessInstanceDetail(@PathVariable String processInstanceId,
                                                     @RequestParam(value = "companyUuid", required = false) @CorpCoop
                                                     String companyUuid) {
        return workFlowService.getProcessInstanceDetail(processInstanceId);
    }

}
