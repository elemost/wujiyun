package com.wuji.workflow.controller;

import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.service.FlowableActivityConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
@RestController
@RequestMapping("/flowable/activity/config")
public class FlowableActivityConfigController {

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @ApiOperation("流程节点配置")
    @GetMapping("/detail/{modelId}")
    public List<FlowableActivityConfigDomain> detail(@PathVariable String modelId) {
        return flowableActivityConfigService.detail(modelId, null);
    }

    @ApiOperation("流程节点配置")
    @GetMapping("/detail/{modelId}/{activityId}")
    public List<FlowableActivityConfigDomain> detailByActivity(@PathVariable String modelId,
                                                               @PathVariable String activityId) {
        return flowableActivityConfigService.detail(modelId, activityId);
    }
}
