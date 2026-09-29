package com.wuji.service.controller;

import com.wuji.service.model.request.AcrossAppSaveRequest;
import com.wuji.service.model.vo.AcrossAppVO;
import com.wuji.service.service.AcrossAppService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-22
 */
@RestController
@RequestMapping("/across/app")
public class AcrossAppController {

    @Autowired
    private AcrossAppService acrossAppService;

    @ApiOperation("保存跨应用配置")
    @PostMapping("/save/{configAppId}")
    public void aggregateTable(@PathVariable String configAppId,
                               @RequestBody List<AcrossAppSaveRequest> acrossAppSaveList) {
        acrossAppService.save(acrossAppSaveList, configAppId);
    }

    @ApiOperation("获取应用配置")
    @GetMapping("/getByAppId/{configAppId}")
    public List<AcrossAppVO> aggregateTable(@PathVariable String configAppId) {
        return acrossAppService.getByAppId(configAppId);
    }

}
