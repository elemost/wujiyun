package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MongodbAggregateAllVO {
    private MongodbAggregateVO mongodbAggregateVO;

    private List<JSONObject> mappedResults;

    private Map<String, Object> functionMap;

    private List<InstrumentPanelFunctionVO> functions = new ArrayList<>();

    private Map<String, Object> functionDocumentMap;
}
