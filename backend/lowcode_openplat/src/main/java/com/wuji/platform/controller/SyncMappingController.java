package com.wuji.platform.controller;

import com.wuji.platform.model.request.SyncMappingSaveRequest;
import com.wuji.platform.model.vo.SyncMappingVO;
import com.wuji.platform.service.SyncMappingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/sync/mapping")
public class SyncMappingController {

    @Autowired
    private SyncMappingService syncMappingService;

    @ApiOperation("保存数据")
    @PostMapping("/save")
    public void save(@RequestBody SyncMappingSaveRequest syncMappingSaveRequest) {
        syncMappingService.save(syncMappingSaveRequest);
    }

    @ApiOperation("同步详情")
    @GetMapping("/info")
    public SyncMappingVO info(@RequestParam String applicationId, @RequestParam String formId) {
        return syncMappingService.info(applicationId, formId);
    }

}
