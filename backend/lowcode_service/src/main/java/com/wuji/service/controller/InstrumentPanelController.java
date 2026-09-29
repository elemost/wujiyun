package com.wuji.service.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.MongodbAggregateCheckRequest;
import com.wuji.service.model.request.MongodbAggregateRequest;
import com.wuji.service.model.request.MongodbDetailedCheckRequest;
import com.wuji.service.model.request.MongodbDetailedListRequest;
import com.wuji.service.model.request.MongodbGanttRequest;
import com.wuji.service.model.vo.InstrumentPanelPivotTableVO;
import com.wuji.service.model.vo.MongodbAggregateVO;
import com.wuji.service.service.InstrumentPanelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/instrument/panel")
public class InstrumentPanelController {

    @Autowired
    private InstrumentPanelService instrumentPanelService;

    @ApiOperation("统计表集合查询")
    @PostMapping("/aggregate")
    public MongodbAggregateVO aggregate(@RequestBody MongodbAggregateRequest mongodbAggregateRequest) {
        return instrumentPanelService.aggregate(mongodbAggregateRequest).getMongodbAggregateVO();
    }

    @ApiOperation("统计表透视图")
    @PostMapping("/pivotTable")
    public InstrumentPanelPivotTableVO pivotTable(@RequestBody MongodbAggregateRequest mongodbAggregateRequest) {
        return instrumentPanelService.pivotTable(mongodbAggregateRequest);
    }

    @ApiOperation("统计表透视图")
    @PostMapping("/pivotTable/export")
    public void exportPivotTable(@RequestBody MongodbAggregateRequest mongodbAggregateRequest,
                                 HttpServletResponse response) {
        instrumentPanelService.export(mongodbAggregateRequest, response);
    }


    @ApiOperation("明细表集合查询")
    @PostMapping("/detailedList")
    public QueryPageVO<JSONObject> detailedList(@RequestBody MongodbDetailedListRequest mongodbDetailedListRequest) {
        return instrumentPanelService.detailedList(mongodbDetailedListRequest);
    }

    @ApiOperation("明细表集合查询")
    @PostMapping("/checkDetailedFormat")
    public void checkDetailedFormat(@RequestBody MongodbDetailedCheckRequest mongodbDetailedCheckRequest) {
        instrumentPanelService.checkDetailedFormat(mongodbDetailedCheckRequest);
    }

    @ApiOperation("统计表check")
    @PostMapping("/checkAggregateFormat")
    public void checkAggregateFormat(@RequestBody MongodbAggregateCheckRequest mongodbDetailedCheckRequest) {
        try {
            instrumentPanelService.checkAggregateFormat(mongodbDetailedCheckRequest);
        } catch (Exception e) {

        }
    }

    @ApiOperation("统计表check")
    @PostMapping("/checkAggregateFormatNew")
    public void checkAggregateFormatNew(@RequestBody MongodbAggregateCheckRequest mongodbDetailedCheckRequest) {
        instrumentPanelService.checkAggregateFormatNew(mongodbDetailedCheckRequest);
    }

    @ApiOperation("甘特图")
    @PostMapping("/gantt")
    public List<JSONObject> gantt(@RequestBody MongodbGanttRequest mongodbGanttRequest) {
        return instrumentPanelService.gantt(mongodbGanttRequest);
    }
}
