package com.wuji.platform.controller;

import com.wuji.common.model.Response;

import com.wuji.platform.model.request.DataApiConfigCreateRequest;
import com.wuji.platform.model.request.DataApiConfigRequest;
import com.wuji.platform.model.request.DataApiConfigUpdateRequest;
import com.wuji.platform.model.vo.DataApiConfigVO;
import com.wuji.platform.service.DataApiConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * @since 2025-09-09
 */
@RestController
@RequestMapping("/data/api/config")
public class DataApiConfigController {

    @Autowired
    private DataApiConfigService dataApiConfigService;

    @ApiOperation("api数据管理新增")
    @PostMapping("/create")
    public Response<String> create(@RequestBody DataApiConfigCreateRequest dataApiConfigCreateRequest) {
        return Response.success(dataApiConfigService.create(dataApiConfigCreateRequest));
    }

    @ApiOperation("api数据管理修改")
    @PostMapping("/update")
    public void update(@RequestBody DataApiConfigUpdateRequest dataApiConfigUpdateRequest) {
        dataApiConfigService.update(dataApiConfigUpdateRequest);
    }

    @ApiOperation("api数据管理详情")
    @GetMapping("/info/{id}")
    public DataApiConfigVO info(@PathVariable String id) {
        return dataApiConfigService.info(id);
    }

    @ApiOperation("api数据管理列表")
    @PostMapping("/queryList")
    public List<DataApiConfigVO> queryList(@RequestBody DataApiConfigRequest dataApiConfigRequest) {
        return dataApiConfigService.queryList(dataApiConfigRequest);
    }

    @ApiOperation("api数据管理删除")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        dataApiConfigService.delete(id);
    }

}
