package com.wuji.factory.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.client.AggregateIterable;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormRole;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.factory.model.info.DataFactoryConfig;
import com.wuji.factory.model.info.DataFactoryInputStage;
import com.wuji.factory.model.info.DataFactoryStage;
import com.wuji.factory.model.request.DataFactoryRequest;
import com.wuji.factory.model.vo.DataFactoryDistinctFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageVO;
import com.wuji.factory.model.vo.FormDataFactoryBuildVO;
import com.wuji.factory.service.FormDataFactoryExecuteService;
import com.wuji.quartz.enums.JobGroupEnum;
import com.wuji.quartz.service.JobService;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.express.MongoFormulaRunner;
import com.wuji.service.model.info.FormFataFactorySyncConfig;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import com.wuji.service.model.request.factory.DataFactoryStageDataSourceRequest;
import com.wuji.service.model.request.factory.DataFactoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormDataFactoryPublishService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormDataFactoryExecuteServiceImpl implements FormDataFactoryExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private JobService jobService;

    @Autowired
    private PostService postService;


    @Override
    public DataFactoryVO lookUpAndAggregate(DataFactoryRequest dataFactoryRequest) {
        FormDataFactoryBuildVO factoryBuild = getParam(dataFactoryRequest);
        List<JSONObject> jsonObjectList = getJsonObjects(factoryBuild);
        DataFactoryStage lastStage = factoryBuild.getStageIdToMap().get(dataFactoryRequest.getStageId());
        return getReturnData(lastStage, jsonObjectList, Boolean.FALSE);
    }

    private List<JSONObject> getJsonObjects(FormDataFactoryBuildVO factoryBuild) {
        List<AggregationOperation> aggregationOperations = factoryBuild.getAggregationOperations();
        String tableName = factoryBuild.getTableName();
        List<Document> documentList = new ArrayList<>();
        for (AggregationOperation aggregationOperation : aggregationOperations) {
            documentList.addAll(aggregationOperation.toPipelineStages(Aggregation.DEFAULT_CONTEXT));
        }
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(documentList, JSONObject.class);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            jsonObjectList.add(new JSONObject(iterator.next()));
        }
        return jsonObjectList;
    }

    @Override
    public DataFactoryDistinctFieldVO distinctField(DataFactoryRequest dataFactoryRequest) {
        FormDataFactoryBuildVO factoryBuild = getParam(dataFactoryRequest);
        List<JSONObject> jsonObjectList = getJsonObjects(factoryBuild);
        List<Object> values = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjectList) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Object object = instValue.get(dataFactoryRequest.getDistinctField());
            if (object == null || StringUtils.isEmpty(object.toString())) {
                continue;
            }
            values.add(object);
        }
        DataFactoryDistinctFieldVO dataFactoryDistinctFieldVO = new DataFactoryDistinctFieldVO();
        dataFactoryDistinctFieldVO.setValues(values.stream().distinct().collect(Collectors.toList()));
        return dataFactoryDistinctFieldVO;
    }

    @Override
    public List<DataFactoryStageFieldVO> stageField(DataFactoryRequest dataFactoryRequest) {
        FormDataFactoryBuildVO factoryBuild = getParam(dataFactoryRequest);
        Map<String, DataFactoryStage> stageIdToMap = factoryBuild.getStageIdToMap();
        DataFactoryStage dataFactoryStage = stageIdToMap.get(dataFactoryRequest.getStageId());
        if (dataFactoryStage == null) {
            return new ArrayList<>();
        }
        List<DataFactoryStageFieldVO> dataFactoryStageFieldVOS = new ArrayList<>();
        List<String> inputs = dataFactoryStage.getInput();
        if ("input".equals(dataFactoryStage.getType())) {
            DataFactoryStageFieldVO dataFactoryStageFieldVO = new DataFactoryStageFieldVO();
            dataFactoryStageFieldVO.setFields(dataFactoryStage.getReturnFields());
            dataFactoryStageFieldVO.setTitle(dataFactoryStage.getTitle());
            dataFactoryStageFieldVO.setId(dataFactoryStage.getId());
            dataFactoryStageFieldVOS.add(dataFactoryStageFieldVO);
        } else {
            for (String input : inputs) {
                DataFactoryStageFieldVO dataFactoryStageFieldVO = new DataFactoryStageFieldVO();
                DataFactoryStage inputStage = stageIdToMap.get(input);
                dataFactoryStageFieldVO.setFields(inputStage.getReturnFields());
                dataFactoryStageFieldVO.setTitle(inputStage.getTitle());
                dataFactoryStageFieldVO.setId(inputStage.getId());
                dataFactoryStageFieldVOS.add(dataFactoryStageFieldVO);
            }
        }
        return dataFactoryStageFieldVOS;
    }

    @Override
    public List<DataFactoryStageVO> getInfoByIds(List<String> ids, String applicationId) {
        List<FormDataFactoryPublishVO> formDataFactoryPublishVOS =
                formDataFactoryPublishService.queryPublishList(applicationId, ids);
        List<DataFactoryStageVO> dataFactoryStageVOList = new ArrayList<>();
        List<FieldExistNameVO> allFormConfigCommonList =
                formService.getAllFormConfigCommonList(true, applicationId, true);
        for (FormDataFactoryPublishVO formDataFactoryPublishVO : formDataFactoryPublishVOS) {
            if (StringUtils.isEmpty(formDataFactoryPublishVO.getFactoryConfig())) {
                continue;
            }
            DataFactoryRequest dataFactoryRequest = new DataFactoryRequest();
            dataFactoryRequest.setApplicationId(applicationId);
            dataFactoryRequest.setStageId("end");
            DataFactoryConfig dataFactoryConfig;
            try {
                dataFactoryConfig =
                        objectMapper.readValue(formDataFactoryPublishVO.getFactoryConfig(), DataFactoryConfig.class);
            } catch (Exception e) {
                log.error("转化数智助手失败", e);
                continue;
            }
            dataFactoryRequest.setDataFactoryStageList(dataFactoryConfig.getDataFactoryStageList());
            dataFactoryRequest.setAllFormConfigCommonList(allFormConfigCommonList);
            List<DataFactoryStageFieldVO> dataFactoryStageFieldVOS = stageField(dataFactoryRequest);
            DataFactoryStageVO dataFactoryStageVO = new DataFactoryStageVO();
            if (!dataFactoryStageFieldVOS.isEmpty()) {
                dataFactoryStageVO.setFields(dataFactoryStageFieldVOS.get(0).getFields());
            }
            dataFactoryStageVO.setId(formDataFactoryPublishVO.getId());
            dataFactoryStageVO.setName(formDataFactoryPublishVO.getFactoryName());
            dataFactoryStageVOList.add(dataFactoryStageVO);
        }
        return dataFactoryStageVOList;
    }

    @Override
    public FormDataFactoryBuildVO getParam(DataFactoryRequest dataFactoryRequest) {
        List<DataFactoryStage> dataFactoryStageList = dataFactoryRequest.getDataFactoryStageList();
        String stageId = dataFactoryRequest.getStageId();
        Map<String, DataFactoryStage> stageIdToMap =
                dataFactoryStageList.stream().collect(Collectors.toMap(DataFactoryStage::getId, c -> c));
        DataFactoryStage lastStage = stageIdToMap.get(stageId);
        List<DataFactoryStage> sortList = new ArrayList<>();
        lastStage.sort(sortList, stageIdToMap);
        DataFactoryInputStage mainStage = (DataFactoryInputStage) sortList.get(0);
        DataFactoryStageDataSourceRequest mainDataSource = mainStage.getDataSource();
        Map<String, List<AggregationOperation>> stageIdToAggMap = new HashMap<>();
        List<String> formIdList = new ArrayList<>();
        List<String> applicationIdList = new ArrayList<>();
        for (DataFactoryStage dataFactoryStage : sortList) {
            if ("input".equals(dataFactoryStage.getType())) {
                DataFactoryInputStage dataFactoryInputStage = (DataFactoryInputStage) dataFactoryStage;
                DataFactoryStageDataSourceRequest dataSource = dataFactoryInputStage.getDataSource();
                if (StringUtils.isEmpty(dataSource.getApplicationId())) {
                    dataSource.setApplicationId(dataFactoryRequest.getApplicationId());
                }
                formIdList.add(dataSource.getFormId());
                applicationIdList.add(dataSource.getApplicationId());
            }
        }
        applicationIdList = applicationIdList.stream().distinct().collect(Collectors.toList());
        List<FieldExistNameVO> allFormConfigCommonList = dataFactoryRequest.getAllFormConfigCommonList();
        if (CollectionUtils.isEmpty(dataFactoryRequest.getAllFormConfigCommonList()) || applicationIdList.size() > 1 ||
                !applicationIdList.get(0).equals(dataFactoryRequest.getApplicationId())) {
            allFormConfigCommonList = formService.getAllFormFieldVO(applicationIdList, formIdList, Boolean.TRUE, true);
        }
        Map<String, FieldExistNameVO> idToMap =
                allFormConfigCommonList.stream().collect(Collectors.toMap(FieldExistNameVO::getFormId, c -> c));
        for (DataFactoryStage dataFactoryStage : sortList) {
            if ("input".equals(dataFactoryStage.getType())) {
                DataFactoryInputStage dataFactoryInputStage = (DataFactoryInputStage) dataFactoryStage;
                DataFactoryStageDataSourceRequest dataSource = dataFactoryInputStage.getDataSource();
                FieldExistNameVO fieldExistNameVO = idToMap.get(dataSource.getFormId());
                if (fieldExistNameVO == null) {
                    return new FormDataFactoryBuildVO();
                }
            }
            dataFactoryStage.convert(stageIdToMap, mainStage, stageIdToAggMap, idToMap);
        }
        List<AggregationOperation> aggregationOperations = stageIdToAggMap.get(stageId);
        FormDataFactoryBuildVO formDataFactoryBuild = new FormDataFactoryBuildVO();
        String tableName = idToMap.get(mainDataSource.getFormId()).getTableName();
        formDataFactoryBuild.setTableName(tableName);
        formDataFactoryBuild.setAggregationOperations(aggregationOperations);
        formDataFactoryBuild.setStageIdToMap(stageIdToMap);
        return formDataFactoryBuild;
    }

    @Override
    public void syncDataExecute(String id, String applicationId) {
        ApplicationVO applicationVO = applicationService.detail(applicationId);
        if (UserUtils.getUser() == null) {
            UserDomain userDomain = new UserDomain();
            userDomain.setUserId(UserDefaultEnum.SYSTEM_USER.getId().toString());
            userDomain.setCompanyId(applicationVO.getCompanyId());
            UserUtils.setUser(userDomain);
        }
        FormDataFactoryVO formDataFactoryVO = formDataFactoryService.info(id, applicationId);
        DataFactoryConfig dataFactoryConfig;
        try {
            dataFactoryConfig = objectMapper.readValue(formDataFactoryVO.getFactoryConfig(), DataFactoryConfig.class);
        } catch (Exception e) {
            log.error("转化数智助手失败", e);
            return;
        }
        FormFataFactorySyncConfig formFataFactorySyncConfig =
                JSONObject.parseObject(formDataFactoryVO.getSyncConfig(), FormFataFactorySyncConfig.class);
        DataFactoryVO dataFactoryVO = getDataFactoryVO(applicationId, dataFactoryConfig);
        List<FormFataFactorySyncConfig.MappingField> mappingFields = formFataFactorySyncConfig.getMappingFields();
        List<JSONObject> dataList = dataFactoryVO.getData();
        FormVO info = formService.info(formFataFactorySyncConfig.getFormId(), applicationId);
        for (JSONObject jsonObject : dataList) {
            JSONObject sourceInstValue = jsonObject.getJSONObject("instValue");
            JSONObject instValue = getInstValue(sourceInstValue, mappingFields);
            FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
            formInsertDataRequest.setFormId(info.getId());
            formInsertDataRequest.setVersion(info.getVersion());
            formInsertDataRequest.setUuid(ObjectId.getGuid());
            formInsertDataRequest.setInstValue(instValue);
            formInsertDataRequest.setApplicationId(info.getApplicationId());
            formMongoDbService.insertMongoDbData(formInsertDataRequest, info);
        }
        UserUtils.clearUser();
    }

    @Override
    public void checkFormula() {
        MongoFormulaRunner mongoFormulaRunner = new MongoFormulaRunner();
        // try {
        //     DefaultContext<String, Object> context = new DefaultContext<>();
        //     if (CollectionUtils.isNotEmpty(mainField.getQuoteFields())) {
        //         for (DataStreamQuoteField quoteField : mainField.getQuoteFields()) {
        //             context.put(quoteField.getId(), MongoSearchUtils.getField(quoteField.getQuoteFieldId(), quoteField.getQuoteSubForm(),
        //                     quoteField.getQuoteFieldType()));
        //         }
        //     }
        //     Object function = mongoFormulaRunner.execute(mainField.getFunction(), context, null, true, false);
        //
        // }
    }

    @Override
    public void trigger(String id, String applicationId) {
        String businessId = id + "_" + applicationId;
        jobService.run(businessId, JobGroupEnum.DATA_FACTORY.name());
    }

    private static JSONObject getInstValue(JSONObject jsonObject,
                                           List<FormFataFactorySyncConfig.MappingField> mappingFields) {
        JSONObject instValue = new JSONObject();
        for (FormFataFactorySyncConfig.MappingField mappingField : mappingFields) {
            Object object = jsonObject.get(mappingField.getFieldId());
            if (object == null) {
                continue;
            }
            if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType().equals(mappingField.getTargetFieldType())) {
                instValue.put(mappingField.getTargetFieldId(), Lists.newArrayList(object));
            } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType()
                    .equals(mappingField.getTargetFieldType())) {
                instValue.put(mappingField.getTargetFieldId(), Lists.newArrayList(object));
            } else {
                instValue.put(mappingField.getTargetFieldId(), object);
            }
        }
        return instValue;
    }

    private DataFactoryVO getDataFactoryVO(String applicationId, DataFactoryConfig dataFactoryConfig) {
        List<DataFactoryStage> dataFactoryStageList = dataFactoryConfig.getDataFactoryStageList();
        DataFactoryRequest dataFactoryRequest = new DataFactoryRequest();
        dataFactoryRequest.setApplicationId(applicationId);
        dataFactoryRequest.setStageId("end");
        dataFactoryRequest.setDataFactoryStageList(dataFactoryStageList);
        return lookUpAndAggregate(dataFactoryRequest);
    }

    private DataFactoryVO getReturnData(DataFactoryStage lastStage, List<JSONObject> mappedResults, Boolean needSave) {
        Map<Long, String> deptIdToMapMap = departmentService.allDeptWithDelete(UserUtils.getUser().getCompanyId());
        Map<Long, UserCompanyVO> userIdMap = userCompanyService.getAllUserWithDelete();
        List<PostVO> allPost = postService.getAllPost();
        Map<Long, String> postMap = allPost.stream().collect(Collectors.toMap(PostVO::getPostId, PostVO::getPostName));
        List<DataFactoryReturnFieldVO> returnFields = lastStage.getReturnFields();
        for (JSONObject jsonObject : mappedResults) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            for (DataFactoryReturnFieldVO returnField : returnFields) {
                Object object = instValue.get(returnField.getAliasName());
                if (object == null) {
                    continue;
                }
                if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(returnField.getFieldType())) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(Long.valueOf(object.toString()));
                    UserCompanyVO userCompanyVO = userIdMap.get(formUser.getAssigneeId());
                    if (userCompanyVO != null) {
                        formUser.setAssigneeName(userCompanyVO.getNickName());
                    }
                    if (needSave) {
                        instValue.put(returnField.getAliasName(), Collections.singletonList(formUser));
                    } else {
                        instValue.put(returnField.getAliasName(), formUser);
                    }
                } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType()
                        .equals(returnField.getFieldType())) {
                    FormDept formDept = new FormDept();
                    formDept.setValue(Long.valueOf(object.toString()));
                    String deptName = deptIdToMapMap.get(formDept.getValue());
                    formDept.setLabel(deptName);
                    if (needSave) {
                        instValue.put(returnField.getAliasName(), Collections.singletonList(formDept));
                    } else {
                        instValue.put(returnField.getAliasName(), formDept);
                    }
                } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_SINGLE.getFieldType().equals(returnField.getFieldType())) {
                    Long roleId = Long.valueOf(object.toString());
                    FormRole formRole = new FormRole();
                    formRole.setRoleId(roleId);
                    formRole.setRoleName(postMap.get(roleId));
                    instValue.put(returnField.getAliasName(), formRole);
                } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType()
                        .equals(returnField.getFieldType())) {
                    JSONArray jsonArray = JsonObjectUtils.getJsonArray(instValue, returnField.getAliasName());
                    List<Long> roleIdList = jsonArray.toJavaList(Long.class);
                    List<FormRole> formRoles = new ArrayList<>();
                    if (CollectionUtils.isEmpty(roleIdList)) {
                        continue;
                    }
                    for (Long roleId : roleIdList) {
                        FormRole formRole = new FormRole();
                        formRole.setRoleId(roleId);
                        formRole.setRoleName(postMap.get(roleId));
                        formRoles.add(formRole);
                    }
                    instValue.put(returnField.getAliasName(), formRoles);
                }
            }
            jsonObject.put("instValue", instValue);
        }
        DataFactoryVO dataFactoryVO = new DataFactoryVO();
        dataFactoryVO.setData(mappedResults);
        dataFactoryVO.setHeader(returnFields);
        return dataFactoryVO;
    }
}
