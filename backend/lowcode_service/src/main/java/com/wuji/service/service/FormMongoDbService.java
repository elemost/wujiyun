package com.wuji.service.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.FormWorkflowDataDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormMongoDbBatchRequest;
import com.wuji.service.model.request.FormMongoDbDeleteRequest;
import com.wuji.service.model.request.FormMongoDbLinkRequest;
import com.wuji.service.model.request.FormMongoDbSummaryRequest;
import com.wuji.service.model.request.FormMongodbLinkSelectRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.request.MongoDbUserFilledRequest;
import com.wuji.service.model.vo.FormMongoDbLinkVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;

import java.util.List;

public interface FormMongoDbService {
    String insertData(FormInsertDataRequest formInsertDataRequest);

    String insertMongoDbData(FormInsertDataRequest formInsertDataRequest, FormVO info);

    void insertDataTrigger(FormInsertDataRequest formInsertDataRequest);

    void updateData(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate);

    void updateDataTrigger(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate);

    void update(FormUpdateDataRequest formUpdateDataRequest, Boolean onlyUpdate);

    String updateBatch(FormMongoDbBatchRequest formMongoDbBatchRequest);

    void deleteData(String uuid, String formId, String applicationId, List<String> triggerParentList);

    void batchDelete(FormMongoDbDeleteRequest formMongoDbDeleteRequest);

    QueryPageVO<LowcodeDataVO> queryList(FormSearchDataRequest formSearchDataRequest);

    QueryPageVO<LowcodeDataDomain> queryListDomain(FormSearchDataRequest formSearchDataRequest);

    LowcodeDataVO infoAll(FormSearchDataRequest formSearchDataRequest);

    QueryPageVO<LowcodeDataVO> queryListLink(FormSearchDataRequest formSearchDataRequest);

    LowcodeDataDomain info(String uuid, String formId, String applicationId);

    LowcodeDataDomain infoByProcessInstanceId(String formId, String processInstanceId, String applicationId);

    void dataStreamTriggerAgain(DataStreamTriggerLogDomain dataStreamTriggerLogDomain, String uuid);

    QueryPageVO<LowcodeDataVO> queryListPrivilege(FormSearchDataRequest formSearchDataRequest);

    List<FormExtraFunctionButton> infoButton(String application, String formId, String id, String groupId);

    FormMongoDbLinkVO link(FormMongoDbLinkRequest formMongoDbLinkRequest);

    FormMongoDbLinkVO linkList(FormMongoDbLinkRequest formMongoDbLinkRequest);

    List<Object> linkSelect(FormMongodbLinkSelectRequest formMongodbLinkSelectRequest);

    Boolean checkDataExist(String uuid, MongodbSearchFilter mongodbSearchFilter, String formId, String applicationId);

    void copyData(String formConfig, String tableName, String toTableName, Boolean needTrans, Boolean exist,
                  String applicationId, String formId, String generaTemplateId);

    Boolean checkUserFilled(MongoDbUserFilledRequest mongoDbUserFilledRequest);

    JSONObject summary(FormMongoDbSummaryRequest formMongoDbSummaryRequest);

    List<LowcodeDataVO> getWorkflowData(List<FormWorkflowDataDomain> formWorkflowDataDomains);
}
