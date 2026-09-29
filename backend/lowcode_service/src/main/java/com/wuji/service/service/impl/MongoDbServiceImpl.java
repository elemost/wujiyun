package com.wuji.service.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.wuji.common.model.domain.MongoDbUpdateBaseDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.constant.Constants;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.request.MongodbSearchRequest;
import com.wuji.service.service.MongoDbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MongoDbServiceImpl implements MongoDbService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void createCollection(String collectionName) {

        // 创建固定大小集合
        CollectionOptions collectionOptions = CollectionOptions.empty();
        // 创建固定集合。固定集合是指有着固定大小的集合，当达到最大值时，它会自动覆盖最早的文档。
        // 固定集合指定一个最大值，以千字节计(KB),如果 capped 为 true，也需要指定该字段。
        // 指定固定集合中包含文档的最大数量。

        // 执行创建集合
        mongoTemplate.createCollection(collectionName, collectionOptions);
    }

    @Override
    public void insertData(LowcodeDataDomain data, String collectionName) {
        if (StringUtils.isEmpty(data.getUuid())) {
            data.setUuid(ObjectId.getGuid());
        }
        mongoTemplate.insert(data, collectionName);
    }

    @Override
    public void updateData(MongoDbUpdateBaseDomain mongoDbUpdateBaseDomain) {
        Class<? extends MongoDbUpdateBaseDomain> clazz = mongoDbUpdateBaseDomain.getClass();
        Criteria criteria = Criteria.where(Constants.UUID).is(mongoDbUpdateBaseDomain.getUuid());
        Query query = new Query(criteria);
        Update update = new Update();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                try {
                    String name = field.getName();
                    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = clazz.getMethod(methodName);
                    Object invoke = method.invoke(mongoDbUpdateBaseDomain);
                    if (invoke != null) {
                        update.set(name, invoke);
                    }
                } catch (Exception e) {
                    log.error("获取字段值失败", e);
                }
            }
        } catch (Exception e) {
            log.error("处理class失败", e);
        }
        mongoTemplate.upsert(query, update, mongoDbUpdateBaseDomain.getCollection());
    }

    @Override
    public void deleteData(String uuid, String collectionName) {
        // 创建条件对象
        Criteria criteria = Criteria.where(Constants.UUID).is(uuid);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        // 执行删除查找到的匹配的全部文档信息
        DeleteResult result = mongoTemplate.remove(query, collectionName);
    }

    @Override
    public LowcodeDataDomain getByInstanceId(String processInstanceId, String collectionName) {
        // 创建条件对象
        Criteria criteria = Criteria.where(Constants.PROCESS_INSTANCE_ID).is(processInstanceId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, LowcodeDataDomain.class, collectionName);
    }

    @Override
    public LowcodeDataDomain getByUuid(String uuid, String collectionName) {
        // 创建条件对象
        Criteria criteria = Criteria.where(Constants.UUID).is(uuid);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, LowcodeDataDomain.class, collectionName);
    }

    @Override
    public List<LowcodeDataDomain> getByUuidList(List<String> uuidList, String collectionName) {
        if(CollectionUtils.isEmpty(uuidList)) {
            return new ArrayList<>();
        }
        Criteria criteria = Criteria.where(Constants.UUID).in(uuidList);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        return mongoTemplate.find(query, LowcodeDataDomain.class, collectionName);
    }

    @Override
    public QueryPageVO<LowcodeDataDomain> search(MongodbSearchRequest mongodbSearchRequest) {
        Query query = new Query();
        // 关键字搜索
        buildKeywordSearch(mongodbSearchRequest.getKeyword(), query);
        query.limit(mongodbSearchRequest.getPageSize());
        query.skip(mongodbSearchRequest.getOffSet());
        long count = mongoTemplate.count(query, LowcodeDataDomain.class);
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, mongodbSearchRequest.getCollection());
        return new QueryPageVO<>((int) count, lowcodeInsertDataDomains);
    }

    /**
     * 关键字搜索
     *
     * @param keyword 关键字
     * @param query
     */
    private static void buildKeywordSearch(String keyword, Query query) {
        if (StringUtils.isNotEmpty(keyword)) {
            Criteria criteria = new Criteria();
            Criteria search = Criteria.where("fieldData.value").regex(keyword);
            criteria.elemMatch(search);
            query.addCriteria(criteria);
        }
    }
}
