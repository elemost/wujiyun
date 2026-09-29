package com.wuji.service.service;

import com.wuji.common.model.domain.MongoDbUpdateBaseDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.request.MongodbSearchRequest;

import java.util.List;

public interface MongoDbService {

    void createCollection(String collectionName);

    void insertData(LowcodeDataDomain data, String collectionName);

    void updateData(MongoDbUpdateBaseDomain mongoDbUpdateBaseDomain);

    void deleteData(String uuid, String collectionName);

    LowcodeDataDomain getByInstanceId(String processInstanceId, String collectionName);

    LowcodeDataDomain getByUuid(String uuid, String collectionName);

    List<LowcodeDataDomain> getByUuidList(List<String> uuidList, String collectionName);

    QueryPageVO<LowcodeDataDomain> search(MongodbSearchRequest mongodbSearchRequest);

}
