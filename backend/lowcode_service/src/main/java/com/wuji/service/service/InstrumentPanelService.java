package com.wuji.service.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.MongodbAggregateCheckRequest;
import com.wuji.service.model.request.MongodbAggregateRequest;
import com.wuji.service.model.request.MongodbDetailedCheckRequest;
import com.wuji.service.model.request.MongodbDetailedListRequest;
import com.wuji.service.model.request.MongodbGanttRequest;
import com.wuji.service.model.vo.InstrumentPanelPivotTableVO;
import com.wuji.service.model.vo.MongodbAggregateAllVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface InstrumentPanelService {
    /**
     * 统计表
     *
     * @param mongodbAggregateRequest
     * @return
     */
    MongodbAggregateAllVO aggregate(MongodbAggregateRequest mongodbAggregateRequest);

    void growthRate(MongodbAggregateRequest mongodbAggregateRequest);

    MongodbAggregateAllVO getAggregateData(MongodbAggregateRequest mongodbAggregateRequest);

    InstrumentPanelPivotTableVO pivotTable(MongodbAggregateRequest mongodbAggregateRequest);

    void export(MongodbAggregateRequest mongodbAggregateRequest, HttpServletResponse response);

    QueryPageVO<JSONObject> detailedList(MongodbDetailedListRequest mongodbDetailedListRequest);

    void checkDetailedFormat(MongodbDetailedCheckRequest mongodbDetailedCheckRequest);

    void checkAggregateFormat(MongodbAggregateCheckRequest mongodbAggregateCheckRequest);

    void checkAggregateFormatNew(MongodbAggregateCheckRequest mongodbAggregateCheckRequest);

    List<JSONObject> gantt(MongodbGanttRequest mongodbGanttRequest);
}
