package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.platform.model.vo.SyncMappingVO;
import com.wuji.platform.service.SyncMappingService;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.domain.DataStreamCreateTransDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamDemoNode;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.service.utils.OrderLoadingOptimizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FormDataStreamDemoExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private SyncMappingService syncMappingService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormService formService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String nodeType() {
        return "demo";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamDemoNode dataStreamDemoNode = (DataStreamDemoNode) dataStreamCommon;
        DataStreamCreateTransDomain dataStreamCreateTransDomain =
                MongoDbDataTransUtils.streamTransWhileCreate(nodeIdMap, dataStreamDemoNode.getFieldTrans(),
                        new ArrayList<>(), new HashMap<>());
        JSONObject commonJson = dataStreamCreateTransDomain.getReturnList().get(0);
        DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(1L);
        JSONObject orderInfo =
                sendJson(formDataStreamTrigger.getApplicationId(), dataStreamCalculateVO.getJsonValue(), "3buj5ojeo");
        JSONObject inputJson = new JSONObject();

        DataStreamCalculateVO carInfoVO = nodeIdMap.get(1756460304712L);
        JSONArray jsonArray = carInfoVO.getJsonValue().getJSONArray("key");
        if (jsonArray == null) {
            return;
        }
        List<JSONObject> carInfoList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject carInfo = sendJson(formDataStreamTrigger.getApplicationId(), jsonObject, "3bud2k474");
            carInfoList.add(carInfo);
        }
        inputJson.put("carInfo", carInfoList);
        inputJson.put("order", orderInfo);
        log.info(JSONObject.toJSONString(inputJson));
        List<Map<String, Object>> outputJsonArray = OrderLoadingOptimizer.optimizeLoading(inputJson);
        log.info(JSONObject.toJSONString(outputJsonArray));
        FormVO formVO = formService.info("3buu31erk", formDataStreamTrigger.getApplicationId());
        FormVO carInfo = formService.info("3bud2k474", formDataStreamTrigger.getApplicationId());
        for (Map<String, Object> objectMap : outputJsonArray) {
            JSONObject jsonObject = new JSONObject(objectMap);
            JSONObject instValue = insertJson(formDataStreamTrigger.getApplicationId(), jsonObject, "3buu31erk");
            double v = Double.parseDouble(objectMap.get("volume").toString()) -
                    Double.parseDouble(objectMap.get("realVolume").toString());
            instValue.put("number_mewkz4kg", v);
            instValue.put("number_mewkzm3c", Double.parseDouble(objectMap.get("volume").toString()));
            instValue.putAll(commonJson);
            String licensePlateNumber = jsonObject.getString("licensePlateNumber");
            FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
            formInsertDataRequest.setInstValue(instValue);
            formInsertDataRequest.setFormId(formVO.getId());
            formInsertDataRequest.setVersion(formVO.getVersion());
            formInsertDataRequest.setUuid(ObjectId.getGuid());
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
            formInsertDataRequest.setApplicationId(formDataStreamTrigger.getApplicationId());
            formInsertDataRequest.setDataStreamTrigger(false);
            formMongoDbService.insertData(formInsertDataRequest);
            Query query = new Query();
            MongoSearchUtils.buildCommonFilter(query, carInfo.getApplicationId(), carInfo.getId());
            query.addCriteria(Criteria.where("instValue.text_merv5rrf").is(licensePlateNumber));
            Update update = new Update().set("instValue.select_mervk2cc", "装货中").set("instValue.number_mewium4i", v);
            mongoTemplate.updateMulti(query, update, carInfo.getTableName());
        }


        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    public JSONObject sendJson(String applicationId, JSONObject inputJson, String fromId) {
        SyncMappingVO syncMappingVO = syncMappingService.info(applicationId, fromId);

        List<FormExtraFunctionSync> syncList =
                JSONObject.parseArray(syncMappingVO.getMappingConfig(), FormExtraFunctionSync.class);
        JSONObject sendJson = new JSONObject();
        for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
            if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                continue;
            }
            FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
            if (formDataService != null) {
                formDataService.dealWhileSend(formExtraFunctionSync, inputJson,  null, sendJson);
            } else {
                sendJson.put(formExtraFunctionSync.getMappingField(), inputJson.get(formExtraFunctionSync.getName()));
            }
        }
        return sendJson;
    }

    private JSONObject insertJson(String applicationId, JSONObject inputJson, String fromId) {
        SyncMappingVO info = syncMappingService.info(applicationId, fromId);

        List<FormExtraFunctionSync> syncList =
                JSONObject.parseArray(info.getMappingConfig(), FormExtraFunctionSync.class);

        JSONObject instValue = new JSONObject();
        for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
            if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                continue;
            }
            Object value = inputJson.get(formExtraFunctionSync.getMappingField());
            FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
            if (formDataService != null) {
                FormSyncCheckResultVO formSyncCheckResultVO =
                        formDataService.dealWhileSync(formExtraFunctionSync, value, new SystemAllDataNameVO());
                instValue.put(formExtraFunctionSync.getName(), formSyncCheckResultVO.getValue());
            } else {
                instValue.put(formExtraFunctionSync.getName(), value);
            }
        }
        return instValue;
    }
}
