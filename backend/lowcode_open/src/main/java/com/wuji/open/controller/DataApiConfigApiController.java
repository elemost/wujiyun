package com.wuji.open.controller;

import com.wuji.open.service.DataApiConfigOpenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/develop/document/data/api/config")
public class DataApiConfigApiController {

    @Autowired
    private DataApiConfigOpenService dataApiConfigOpenService;

    @ApiOperation("统计表数据")
    @GetMapping("/getStatisticsData")
    public Object getData(@RequestParam("id") String id, @RequestParam("applicationId") String applicationId) {
        return dataApiConfigOpenService.getData(id, applicationId);
    }

    @ApiOperation("明细表数据")
    @GetMapping("/getDetailedData")
    public Object getDetailedData(@RequestParam("id") String id, @RequestParam("applicationId") String applicationId) {
        return dataApiConfigOpenService.getDetailedData(id, applicationId);
    }

}
