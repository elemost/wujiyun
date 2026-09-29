package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.AggregateIterable;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserPostService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormRole;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.context.FormFunctionDataContext;
import com.wuji.service.converter.AbstractFormMongoDbConverter;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.FormSystemReturnFieldEnum;
import com.wuji.service.enums.MongodbSearchConditionQuoteTypeEnum;
import com.wuji.service.enums.SearchMethodEnum;
import com.wuji.service.model.domain.FormPrivilegeDataScopeDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchConditionQuote;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.request.FormMongodbLinkSelectRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.MongoGroupLookUpRequest;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.SearchFilterVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormExtraFunctionTitleService;
import com.wuji.service.service.FormFunctionDataService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.FormConfigUtils;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.MongoDataUtils;
import com.wuji.service.utils.MongoFunctionUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.workflow.service.WorkFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FormMongoDbCommonServiceImpl {

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FormFunctionDataContext formFunctionDataContext;

    @Autowired
    private AdminCommonService adminCommonService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserPostService userPostService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private FormModelService formModelService;


    public void buildSearchFilter(FormSearchDataRequest formSearchDataRequest,
                                  List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList, Query query) {
        List<Criteria> criteriaList = getCriteria(formSearchDataRequest);
        if (StringUtils.isNotEmpty(formSearchDataRequest.getStatus())) {
            criteriaList.add(Criteria.where("status").is(formSearchDataRequest.getStatus()));
        }
        // 权限搜索
        if (CollectionUtils.isNotEmpty(formPrivilegeDataScopeDomainList)) {
            buildMongodbDataScope(criteriaList, formPrivilegeDataScopeDomainList, new ArrayList<>(),
                    formSearchDataRequest.getApplicationId(), formSearchDataRequest.getFormId());
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria search = new Criteria();
            search.andOperator(criteriaList);
            query.addCriteria(search);
        }
    }

    public List<Criteria> getCriteria(FormSearchDataRequest formSearchDataRequest) {
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, formSearchDataRequest.getApplicationId(),
                formSearchDataRequest.getFormId());

        Criteria viewCriteria =
                MongoSearchUtils.buildCriteriaByFilter(formSearchDataRequest.getViewFilter(), new ArrayList<>());
        if (viewCriteria != null) {
            criteriaList.add(viewCriteria);
        }
        Criteria treeCriteria = MongoSearchUtils.buildCriteriaByFilter(formSearchDataRequest.getTreeFilter(), new ArrayList<>());
        if (treeCriteria != null) {
            criteriaList.add(treeCriteria);
        }
        if (CollectionUtils.isNotEmpty(formSearchDataRequest.getUuidList())) {
            criteriaList.add(new Criteria("uuid").in(formSearchDataRequest.getUuidList()));
        }
        // 高级搜索
        Criteria criteria =
                MongoSearchUtils.buildCriteriaByFilter(formSearchDataRequest.getFilter(), new ArrayList<>());
        if (criteria != null) {
            criteriaList.add(criteria);
        }
        // 关键字搜索
        keywordSearch(formSearchDataRequest, criteriaList);
        return criteriaList;
    }

    // 构建范围数据搜索
    public void buildMongodbDataScope(List<Criteria> allList,
                                      List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList,
                                      List<MongoGroupLookUpRequest> mongoGroupLookUpRequestList, String applicationId,
                                      String formId) {
        List<Criteria> criteriaList =
                getPrivilegeCriteria(formPrivilegeDataScopeDomainList, mongoGroupLookUpRequestList, applicationId,
                        formId);
        if (CollectionUtils.isEmpty(criteriaList)) {
            return;
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria criteria = new Criteria();
            criteria.orOperator(criteriaList);
            allList.add(criteria);
        }
    }

    public void keywordSearch(FormSearchDataRequest formSearchDataRequest, List<Criteria> allList) {
        if (StringUtils.isNotEmpty(formSearchDataRequest.getKeyword()) &&
                CollectionUtils.isNotEmpty(formSearchDataRequest.getKeyList())) {
            Criteria criteria = new Criteria();
            Pattern compile =
                    Pattern.compile("^.*" + formSearchDataRequest.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
            List<Criteria> criteriaList = new ArrayList<>();
            for (String key : formSearchDataRequest.getKeyList()) {
                criteriaList.add(new Criteria("instValue." + key).is(compile));
            }
            criteria.orOperator(criteriaList);
            allList.add(criteria);
        }
        if (StringUtils.isNotEmpty(formSearchDataRequest.getParentDataUuid())) {
            Criteria criteria = new Criteria("parentInfo.parentDataUuid").is(formSearchDataRequest.getParentDataUuid());
            allList.add(criteria);
        }
    }

    public List<Criteria> getPrivilegeCriteria(List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList,
                                               List<MongoGroupLookUpRequest> mongoGroupLookUpRequestList,
                                               String applicationId, String formId) {
        boolean all = Boolean.FALSE;
        List<Criteria> criteriaList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        List<MongodbSearchFilter> mongodbSearchFilterList = new ArrayList<>();
        for (FormPrivilegeDataScopeDomain formPrivilegeDataScopeDomain : formPrivilegeDataScopeDomainList) {
            userIdList.addAll(formPrivilegeDataScopeDomain.getUserIdList());
            deptIdList.addAll(formPrivilegeDataScopeDomain.getDeptIdList());
            if (formPrivilegeDataScopeDomain.getAll()) {
                all = true;
                break;
            }
            if (formPrivilegeDataScopeDomain.getFilter() != null) {
                mongodbSearchFilterList.add(formPrivilegeDataScopeDomain.getFilter());
            }
        }
        if (all) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isNotEmpty(deptIdList)) {
            criteriaList.add(new Criteria("deptList").elemMatch(Criteria.where("value").in(deptIdList)));
        }

        if (CollectionUtils.isNotEmpty(userIdList)) {
            criteriaList.add(new Criteria("creator.assigneeId").in(userIdList));
        }

        if (CollectionUtils.isNotEmpty(mongodbSearchFilterList)) {
            for (MongodbSearchFilter mongodbSearchFilter : mongodbSearchFilterList) {
                filterCheckAndSearchValue(mongodbSearchFilter, applicationId, formId);
                Criteria custom =
                        MongoSearchUtils.buildCriteriaByFilter(mongodbSearchFilter, mongoGroupLookUpRequestList);
                if (custom == null) {
                    continue;
                }
                criteriaList.add(custom);
            }
        }
        return criteriaList;
    }

    public void filterCheckAndSearchValue(MongodbSearchFilter filter, String applicationId, String formId) {
        if (filter == null || CollectionUtils.isEmpty(filter.getConditionList())) {
            return;
        }
        List<String> groupIdList = new ArrayList<>();
        List<MongodbSearchCondition> conditionList = filter.getConditionList();
        FormModelVO formModelVO = null;
        for (MongodbSearchCondition mongodbSearchCondition : conditionList) {
            List<Object> formUserList = new ArrayList<>();
            if (MongodbSearchConditionQuoteTypeEnum.PRIVILEGE.name().equals(mongodbSearchCondition.getQuoteType())) {
                groupIdList.add(mongodbSearchCondition.getQuote().getGroupId());
            } else if (SearchMethodEnum.USER_DEPT.name().equals(mongodbSearchCondition.getMethod())) {
                List<Long> userIds = userDeptService.getByDeptIdList(UserUtils.getUser().getDeptIdList());
                if (CollectionUtils.isEmpty(userIds)) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(-1L);
                    formUserList.add(formUser);
                } else {
                    for (Long userId : userIds) {
                        FormUser formUser = new FormUser();
                        formUser.setAssigneeId(userId);
                        formUserList.add(formUser);
                    }
                }
                mongodbSearchCondition.setValue(formUserList);
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                mongodbSearchCondition.setMethod(SearchMethodEnum.IN.name());
            } else if (SearchMethodEnum.USER_DEPT_CHILD.name().equals(mongodbSearchCondition.getMethod())) {
                List<Long> deptIds = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(),
                        UserUtils.getUser().getDeptIdList());
                List<Long> userIds = userDeptService.getByDeptIdList(deptIds);
                if (CollectionUtils.isEmpty(userIds)) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(-1L);
                    formUserList.add(formUser);
                } else {
                    for (Long userId : userIds) {
                        FormUser formUser = new FormUser();
                        formUser.setAssigneeId(userId);
                        formUserList.add(formUser);
                    }
                }
                mongodbSearchCondition.setValue(formUserList);
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                mongodbSearchCondition.setMethod(SearchMethodEnum.IN.name());
            } else if (SearchMethodEnum.USER_POST.name().equals(mongodbSearchCondition.getMethod())) {
                List<Long> userIds = userPostService.getCurrentUserPostIdList();
                if (CollectionUtils.isEmpty(userIds)) {
                    FormUser formUser = new FormUser();
                    formUser.setAssigneeId(-1L);
                    formUserList.add(formUser);
                } else {
                    for (Long userId : userIds) {
                        FormUser formUser = new FormUser();
                        formUser.setAssigneeId(userId);
                        formUserList.add(formUser);
                    }
                }
                mongodbSearchCondition.setValue(formUserList);
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                mongodbSearchCondition.setMethod(SearchMethodEnum.IN.name());
            } else if (FormSystemFieldEnum.CURRENT_NODE.getName().equals(mongodbSearchCondition.getFieldId())) {
                if (formModelVO == null) {
                    formModelVO = formModelService.getByFormId(formId, applicationId);
                }
                List<Object> processInstanceIds = new ArrayList<>();
                if (CollectionUtils.isEmpty(mongodbSearchCondition.getValue())) {
                    processInstanceIds.add("-1");
                } else {
                    if (formModelVO == null) {
                        processInstanceIds.add("-1");
                    } else {
                        List<String> values = mongodbSearchCondition.getValue().stream().map(Object::toString)
                                .collect(Collectors.toList());
                        processInstanceIds =
                                workFlowService.getProcessInstanceIdListByTaskId(values, formModelVO.getBusinessType())
                                        .stream().map(Object::toString).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(processInstanceIds)) {
                            processInstanceIds.add("-1");
                        }
                    }
                }
                mongodbSearchCondition.setValue(processInstanceIds);
                mongodbSearchCondition.setFieldId("processInstanceId");
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                mongodbSearchCondition.setMethod(SearchMethodEnum.IN.name());
            } else if (FormSystemFieldEnum.ASSIGNEE_NAME.getName().equals(mongodbSearchCondition.getFieldId())) {
                if (formModelVO == null) {
                    formModelVO = formModelService.getByFormId(formId, applicationId);
                }
                List<Object> processInstanceIds = new ArrayList<>();
                if (CollectionUtils.isEmpty(mongodbSearchCondition.getValue())) {
                    processInstanceIds.add("-1");
                } else {
                    if (formModelVO == null) {
                        processInstanceIds.add("-1");
                    } else {
                        List<String> values = mongodbSearchCondition.getValue().stream().map(c -> {
                            FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(c), FormUser.class);
                            if (Objects.equals(UserDefaultEnum.CURRENT_USER.getId(), formUser.getAssigneeId())) {
                                return UserUtils.getUser().getUserId();
                            } else {
                                return formUser.getAssigneeId().toString();
                            }
                        }).collect(Collectors.toList());
                        processInstanceIds = workFlowService.getProcessInstanceIdListByAssigneeId(values,
                                        formModelVO.getBusinessType()).stream().map(Object::toString)
                                .collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(processInstanceIds)) {
                            processInstanceIds.add("-1");
                        }
                    }
                }
                mongodbSearchCondition.setValue(processInstanceIds);
                mongodbSearchCondition.setFieldId("processInstanceId");
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                mongodbSearchCondition.setMethod(SearchMethodEnum.IN.name());
            }
        }
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            List<FormPrivilegeVO> formPrivilegeVOS = formPrivilegeService.getByIdList(groupIdList, applicationId);
            Map<String, FormPrivilegeVO> formPrivilegeMap =
                    formPrivilegeVOS.stream().collect(Collectors.toMap(FormPrivilegeVO::getId, c -> c));
            List<String> formIdList =
                    formPrivilegeVOS.stream().map(FormPrivilegeVO::getCategoryId).collect(Collectors.toList());
            List<FormVO> formVOList = formService.getByIdList(formIdList, applicationId);
            Map<String, FormVO> formIdMap = formVOList.stream().collect(Collectors.toMap(FormVO::getId, c -> c));
            for (MongodbSearchCondition mongodbSearchCondition : conditionList) {
                if (!MongodbSearchConditionQuoteTypeEnum.PRIVILEGE.name()
                        .equals(mongodbSearchCondition.getQuoteType())) {
                    continue;
                }
                MongodbSearchConditionQuote quote = mongodbSearchCondition.getQuote();
                FormPrivilegeVO formPrivilegeVO = formPrivilegeMap.get(quote.getGroupId());
                List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                        FormPrivilegeUtils.getDataScope(Collections.singletonList(formPrivilegeVO));
                if (CollectionUtils.isEmpty(formPrivilegeDataScopeDomainList)) {
                    mongodbSearchCondition.setValue(new ArrayList<>());
                    mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                    continue;
                }
                List<AggregationOperation> aggregationList = new ArrayList<>();
                List<Criteria> criteriaList = new ArrayList<>();
                MongoSearchUtils.buildCommonFilter(criteriaList, applicationId, quote.getFormId());
                buildMongodbDataScope(criteriaList, formPrivilegeDataScopeDomainList, new ArrayList<>(), applicationId,
                        formId);
                MongoSearchUtils.addMatch(criteriaList, aggregationList);
                FormVO formVO = formIdMap.get(quote.getFormId());
                if (formVO == null) {
                    mongodbSearchCondition.setValue(new ArrayList<>());
                    mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
                    continue;
                }
                List<LowcodeDataDomain> lowcodeDataDomainList =
                        mongoTemplate.aggregate(Aggregation.newAggregation(aggregationList), formVO.getTableName(),
                                LowcodeDataDomain.class).getMappedResults();
                FormMongodbLinkSelectRequest formMongodbLinkSelectRequest = new FormMongodbLinkSelectRequest();
                formMongodbLinkSelectRequest.setFormId(quote.getFormId());
                formMongodbLinkSelectRequest.setApplicationId(applicationId);
                if (StringUtils.isNotEmpty(quote.getSubForm())) {
                    formMongodbLinkSelectRequest.setFieldId(quote.getSubForm());
                    formMongodbLinkSelectRequest.setChildFieldId(quote.getFieldId());
                } else {
                    formMongodbLinkSelectRequest.setFieldId(quote.getFieldId());
                }
                List<Object> linkSelectData = getLinkSelectData(formMongodbLinkSelectRequest, lowcodeDataDomainList);
                mongodbSearchCondition.setValue(linkSelectData);
                mongodbSearchCondition.setQuoteType(MongodbSearchConditionQuoteTypeEnum.CUSTOM.name());
            }
        }

    }

    public List<Object> getLinkSelectData(FormMongodbLinkSelectRequest formMongodbLinkSelectRequest,
                                          List<LowcodeDataDomain> lowcodeDataDomainList) {
        List<Object> objectList = new ArrayList<>();
        if (CollectionUtils.isEmpty(lowcodeDataDomainList)) {
            return objectList;
        }
        Map<Long, UserCompanyVO> allUserWithDelete = userCompanyService.getAllUserWithDelete();
        Map<Long, String> deptWithDelete = departmentService.allDeptWithDelete(UserUtils.getUser().getCompanyId());
        List<PostVO> allPost = postService.getAllPost();
        Map<Long, String> postIdToNameMap =
                allPost.stream().collect(Collectors.toMap(PostVO::getPostId, PostVO::getPostName));
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            if ("system".equals(formMongodbLinkSelectRequest.getFieldType())) {
                if (FormSystemFieldEnum.UPDATE_TIME.getName().equals(formMongodbLinkSelectRequest.getFieldId())) {
                    if (lowcodeDataDomain.getModifyTime() != null) {
                        objectList.add(lowcodeDataDomain.getModifyTime());
                        continue;
                    }
                } else if (FormSystemFieldEnum.CREATE_TIME.getName()
                        .equals(formMongodbLinkSelectRequest.getFieldId())) {
                    if (lowcodeDataDomain.getCreateTime() != null) {
                        objectList.add(lowcodeDataDomain.getCreateTime());
                        continue;
                    }
                } else if (FormSystemFieldEnum.CREATE_NAME.getName()
                        .equals(formMongodbLinkSelectRequest.getFieldId())) {
                    if (lowcodeDataDomain.getCreator() != null) {
                        objectList.add(lowcodeDataDomain.getCreator());
                        continue;
                    }
                }
            }
            JSONObject instValue = lowcodeDataDomain.getInstValue();
            if (instValue == null) {
                continue;
            }
            if (StringUtils.isEmpty(formMongodbLinkSelectRequest.getChildFieldId())) {
                addValue(formMongodbLinkSelectRequest.getFieldId(), formMongodbLinkSelectRequest.getFieldType(),
                        instValue, objectList, allUserWithDelete, deptWithDelete, postIdToNameMap);
            } else {
                JSONArray jsonArray =
                        JsonObjectUtils.getJsonArray(instValue, formMongodbLinkSelectRequest.getFieldId());
                for (int i = 0; i < jsonArray.size(); i++) {
                    addValue(formMongodbLinkSelectRequest.getChildFieldId(),
                            formMongodbLinkSelectRequest.getFieldType(), jsonArray.getJSONObject(i), objectList,
                            allUserWithDelete, deptWithDelete, postIdToNameMap);
                }
            }
        }
        String jsonString = JSONObject.toJSONString(objectList);
        if (FormFieldTypeEnum.getUserFieldType().contains(formMongodbLinkSelectRequest.getFieldType())) {
            return JSONArray.parseArray(jsonString, FormUser.class).stream().distinct().collect(Collectors.toList());
        } else if (FormFieldTypeEnum.getDeptFieldType().contains(formMongodbLinkSelectRequest.getFieldType())) {
            return JSONArray.parseArray(jsonString, FormDept.class).stream().distinct().collect(Collectors.toList());
        } else {
            return JSONArray.parseArray(jsonString, Object.class).stream().distinct().collect(Collectors.toList());
        }
    }

    private void addValue(String fieldId, String fieldType, JSONObject instValue, List<Object> objectList,
                          Map<Long, UserCompanyVO> allUserWithDelete, Map<Long, String> deptWithDelete,
                          Map<Long, String> postIdToNameMap) {
        if (FormFieldTypeEnum.getDeptFieldType().contains(fieldType)) {
            Object object = instValue.get(fieldId);
            if (object != null && StringUtils.isNotEmpty(object.toString())) {
                List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(object), FormDept.class);
                for (FormDept formDept : formDeptList) {
                    String deptName = deptWithDelete.get(formDept.getValue());
                    if (deptName == null) {
                        continue;
                    }
                    formDept.setLabel(deptName);
                    objectList.add(formDept);
                }
            }
        } else if (FormFieldTypeEnum.CHECKBOXES.getFieldType().equals(fieldType) ||
                FormFieldTypeEnum.TREE_SELECT.getFieldType().equals(fieldType)) {
            Object object = instValue.get(fieldId);
            if (object != null && StringUtils.isNotEmpty(object.toString())) {
                List<Object> formCheckboxesList = JSONArray.parseArray(JSONObject.toJSONString(object), Object.class);
                objectList.addAll(formCheckboxesList);
            }
        } else if (FormFieldTypeEnum.getUserFieldType().contains(fieldType)) {
            Object object = instValue.get(fieldId);
            if (object != null && StringUtils.isNotEmpty(object.toString())) {
                List<FormUser> formUserList = JSONArray.parseArray(JSONObject.toJSONString(object), FormUser.class);
                for (FormUser formUser : formUserList) {
                    UserCompanyVO userCompanyVO = allUserWithDelete.get(formUser.getAssigneeId());
                    if (userCompanyVO == null) {
                        continue;
                    }
                    formUser.setAssigneeName(userCompanyVO.getNickName());
                    objectList.add(formUser);
                }
            }
        } else if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType().equals(fieldType)) {
            Object object = instValue.get(fieldId);
            List<Long> userIdList = new ArrayList<>();
            if (object != null) {
                userIdList.add(Long.valueOf(object.toString()));
            }

            for (Long userId : userIdList) {
                UserCompanyVO userCompanyVO = allUserWithDelete.get(userId);
                if (userCompanyVO == null) {
                    continue;
                }
                FormUser formUser = new FormUser();
                formUser.setAssigneeId(userId);
                formUser.setAssigneeName(userCompanyVO.getNickName());
                objectList.add(formUser);
            }
        } else if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType().equals(fieldType)) {
            Object object = instValue.get(fieldId);
            List<Long> deptIdList = new ArrayList<>();
            if (object != null) {
                deptIdList.add(Long.valueOf(object.toString()));
            }
            for (Long deptId : deptIdList) {
                String deptName = deptWithDelete.get(deptId);
                if (deptName == null) {
                    continue;
                }
                FormDept formDept = new FormDept();
                formDept.setValue(deptId);
                formDept.setLabel(deptName);
                objectList.add(formDept);
            }
        } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_SINGLE.getFieldType().equals(fieldType)) {
            Object object = instValue.get(fieldId);
            if (object != null) {
                FormRole formRole = new FormRole();
                formRole.setRoleId(Long.valueOf(object.toString()));
                formRole.setRoleName(postIdToNameMap.get(formRole.getRoleId()));
                objectList.add(formRole);
            }
        } else if (FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType().equals(fieldType)) {
            Object object = instValue.get(fieldId);
            if (object != null) {
                List<Long> roleIds = JSONArray.parseArray(JSONArray.toJSONString(object), Long.class);
                for (Long roleId : roleIds) {
                    FormRole formRole = new FormRole();
                    formRole.setRoleId(roleId);
                    formRole.setRoleName(postIdToNameMap.get(roleId));
                    objectList.add(formRole);
                }
            }
        } else {
            Object object = instValue.get(fieldId);
            if (object != null && StringUtils.isNotEmpty(object.toString())) {
                Object value = MongoDataUtils.decryptAndEncryptReturn(object);
                objectList.add(value);
            }
        }
    }

    public void queryField(FormVO info, Query query, List<FormPrivilegeFieldConfig> formPrivilegeFieldConfigs) {
        // List<FormConfigCommon> configList =
        //         formService.getAllFormConfigCommonList(info.getId(), Boolean.TRUE, info.getApplicationId(),
        //                 Boolean.FALSE).getFields();
        // query.fields().include(MongoSearchUtils.buildQueryField(formPrivilegeFieldConfigs, configList));
    }

    public List<LowcodeDataVO> getLowcodeDataVOS(FormVO info, List<LowcodeDataDomain> lowcodeInsertDataDomains,
                                                 List<FormPrivilegeVO> formPrivilegeList, Boolean extraButton,
                                                 Boolean filterAdminPrivilege) {
        String viewId = info.getId();
        if (StringUtils.isNotEmpty(info.getSourceId())) {
            info = formService.info(info.getSourceId(), info.getApplicationId());
        }
        formExtraFunctionTitleService.buildTitle(lowcodeInsertDataDomains, info.getConfig(), info.getId(),
                info.getApplicationId());
        List<FormConfigCommon> configList =
                FormConfigUtils.getConfigList(info.getConfig(), info.getFormType(), Boolean.FALSE);
        List<LowcodeDataVO> lowcodeDataVOList =
                lowcodeInsertDataDomains.stream().map(AbstractFormMongoDbConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        FormDataExtraParameterRequest formDataExtraParameterRequest = new FormDataExtraParameterRequest();
        formDataExtraParameterRequest.setExtraButton(extraButton);
        formDataExtraParameterRequest.setFormPrivilegeVO(formPrivilegeList);
        formDataExtraParameterRequest.setViewId(viewId);
        formDataExtraParameterRequest.setFilterAdminPrivilege(filterAdminPrivilege);
        for (FormSystemReturnFieldEnum formSystemReturnFieldEnum : FormSystemReturnFieldEnum.values()) {
            FormFunctionDataService formFunctionDataService =
                    formFunctionDataContext.getHandler(formSystemReturnFieldEnum.getName());
            if (formFunctionDataService != null) {
                formFunctionDataService.dealFunctionReturn(lowcodeDataVOList, formDataExtraParameterRequest, info);
            }
        }
        for (FormConfigCommon formConfigCommon : configList) {
            String type = formConfigCommon.getType();
            FormDataService formDataService = formDataContext.getHandler(type);
            if (formDataService != null) {
                formDataService.dealWhileReturn(lowcodeDataVOList, formConfigCommon, info, systemAllData);
            }
        }
        return lowcodeDataVOList;
    }

    public SearchFilterVO buildSearchFilter(FormSearchDataRequest formSearchDataRequest) {
        SearchFilterVO searchFilterVO = new SearchFilterVO();
        List<Criteria> criteriaList = getCriteria(formSearchDataRequest);
        List<FormPrivilegeVO> formPrivilegeList = null;
        // 获取当前用户权限组
        if ("ALL".equals(formSearchDataRequest.getGroupId())) {
            formPrivilegeList = formPrivilegeService.getUserPrivilegeByCategory(
                    Collections.singletonList(formSearchDataRequest.getFormId()),
                    formSearchDataRequest.getApplicationId());
        } else {
            if (StringUtils.isNotEmpty(formSearchDataRequest.getGroupId())) {
                FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(formSearchDataRequest.getGroupId());
                formPrivilegeList = Collections.singletonList(formPrivilegeVO);
            }
        }
        if (CollectionUtils.isNotEmpty(formPrivilegeList)) {
            // 获取数据范围
            List<FormPrivilegeDataScopeDomain> formPrivilegeDataScopeDomainList =
                    FormPrivilegeUtils.getDataScope(formPrivilegeList);
            // 权限搜索
            if (CollectionUtils.isNotEmpty(formPrivilegeDataScopeDomainList)) {
                buildMongodbDataScope(criteriaList, formPrivilegeDataScopeDomainList, new ArrayList<>(),
                        formSearchDataRequest.getApplicationId(), formSearchDataRequest.getFormId());
            }
        }
        searchFilterVO.setFormPrivilegeList(formPrivilegeList);
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria search = new Criteria();
            search.andOperator(criteriaList);
            searchFilterVO.setSearch(search);
        }
        return searchFilterVO;
    }

    public int getAggregateCountSize(List<AggregationOperation> aggregationList, String tableName) {
        List<AggregationOperation> count = new ArrayList<>(aggregationList);
        CountOperation countOperation = Aggregation.count().as("count");
        count.add(countOperation);
        List<Document> documentList = MongoFunctionUtils.toDocument(count);
        AggregateIterable<JSONObject> aggregate =
                mongoTemplate.getCollection(tableName).aggregate(documentList, JSONObject.class);
        List<JSONObject> mappedResults = new ArrayList<>();
        Iterator<JSONObject> iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            mappedResults.add(new JSONObject(iterator.next()));
        }
        int countSize = 0;
        if (CollectionUtils.isNotEmpty(mappedResults)) {
            countSize = (int) mappedResults.get(0).get("count");
        }
        return countSize;
    }
}
