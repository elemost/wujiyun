package com.wuji.open.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.open.service.DataApiConfigOpenService;
import com.wuji.platform.model.entity.DataApiConfigEntity;
import com.wuji.platform.service.DataApiConfigService;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbWidget;
import com.wuji.service.model.request.MongodbAggregateRequest;
import com.wuji.service.model.request.MongodbDetailedListRequest;
import com.wuji.service.service.InstrumentPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataApiConfigOpenServiceImpl implements DataApiConfigOpenService {

    @Autowired
    private DataApiConfigService dataApiConfigService;

    @Autowired
    private InstrumentPanelService instrumentPanelService;

    @Override
    public Object getData(String id, String applicationId) {
        DataApiConfigEntity dataApiConfigEntity = dataApiConfigService.getById(id);
        MongodbAggregateRequest mongodbAggregateRequest =
                JSONObject.parseObject(dataApiConfigEntity.getConfig(), MongodbAggregateRequest.class);
        List<JSONObject> aggregateData = instrumentPanelService.getAggregateData(mongodbAggregateRequest).getMappedResults();
        MongodbWidget widget = mongodbAggregateRequest.getWidget();
        List<MongodbSearchField> fieldxList = widget.getFieldxList();
        List<MongodbSearchField> metricList = widget.getMetricList();
        List<MongodbSearchField> fieldyList = widget.getFieldyList();
        List<MongodbSearchField> allFieldList = new ArrayList<>();
        allFieldList.addAll(metricList);
        allFieldList.addAll(fieldxList);
        allFieldList.addAll(fieldyList);
        List<JSONObject> returnList = new ArrayList<>();
        for (JSONObject jsonObject : aggregateData) {
            JSONObject returnJson = new JSONObject();
            for (MongodbSearchField mongodbSearchField : allFieldList) {
                returnJson.put(mongodbSearchField.getLabel(), jsonObject.get(mongodbSearchField.getTag()));
            }
            returnList.add(returnJson);
        }
        return returnList;
    }

    @Override
    public Object getDetailedData(String id, String applicationId) {
        DataApiConfigEntity dataApiConfigEntity = dataApiConfigService.getById(id);
        MongodbDetailedListRequest mongodbDetailedListRequest =
                JSONObject.parseObject(dataApiConfigEntity.getConfig(), MongodbDetailedListRequest.class);
        QueryPageVO<JSONObject> jsonObjectQueryPageVO = instrumentPanelService.detailedList(mongodbDetailedListRequest);
        List<JSONObject> list = jsonObjectQueryPageVO.getList();
        List<JSONObject> returnList = new ArrayList<>();
        for (JSONObject jsonObject : list) {
            JSONObject returnJson = new JSONObject();
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            for (MongodbSearchField mongodbSearchField : mongodbDetailedListRequest.getWidget().getFields()) {
                returnJson.put(mongodbSearchField.getLabel(), instValue.get(mongodbSearchField.getName()));
            }
            returnList.add(returnJson);
        }
        return returnList;
    }
}
