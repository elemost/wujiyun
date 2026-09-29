package com.wuji.service.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.model.vo.FormFlowableStatisticVO;
import com.wuji.service.service.FormWorkflowService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workbench")
public class WorkbenchController {

    @Autowired
    private FormWorkflowService formWorkflowService;

    @ApiOperation("flowable 工作台统计")
    @GetMapping("/flowableStatistic")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public FormFlowableStatisticVO flowableStatistic(@RequestParam(required = false) @CorpCoop String companyUuid) {
        return formWorkflowService.flowableStatistic();
    }
}
