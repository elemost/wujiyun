package com.wuji.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserPostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormPost;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.converter.AbstractFlowableActivityConfigConverter;
import com.wuji.workflow.mapper.FlowableActivityConfigMapper;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.entity.FlowableActivityConfigEntity;
import com.wuji.workflow.model.flowable.enums.AssigneeTypeEnum;
import com.wuji.workflow.model.info.FlowableAssigneeConfig;
import com.wuji.workflow.model.info.FlowableCopyConfig;
import com.wuji.workflow.model.info.FlowableFormFieldConfig;
import com.wuji.workflow.model.info.FlowableMongodbSearchFilter;
import com.wuji.workflow.model.info.FlowableRemindConfig;
import com.wuji.workflow.model.info.FlowableSubFlowConfig;
import com.wuji.workflow.service.FlowableActivityConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
@Service
public class FlowableActivityConfigServiceImpl
        extends ServiceImpl<FlowableActivityConfigMapper, FlowableActivityConfigEntity>
        implements FlowableActivityConfigService {

    @Autowired
    private FlowableActivityConfigMapper flowableActivityConfigMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserPostService userPostService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String modelId, List<FlowableActivityConfigDomain> flowableActivityConfigDomainList) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> delete = new LambdaQueryWrapper<>();
        delete.eq(FlowableActivityConfigEntity::getModelId, modelId);
        flowableActivityConfigMapper.delete(delete);

        UserDomain user = UserUtils.getUser();
        List<FlowableActivityConfigEntity> flowableActivityConfigEntityList = new ArrayList<>();
        for (FlowableActivityConfigDomain flowableActivityConfigDomain : flowableActivityConfigDomainList) {
            FlowableActivityConfigEntity flowableActivityConfigEntity =
                    AbstractFlowableActivityConfigConverter.INSTANCE.toEntity(flowableActivityConfigDomain);
            flowableActivityConfigEntity.setModelId(modelId);
            if (flowableActivityConfigDomain.getConditionConfigJson() != null) {
                flowableActivityConfigEntity.setConditionConfig(
                        JSONObject.toJSONString(flowableActivityConfigDomain.getConditionConfigJson()));
            }
            if (flowableActivityConfigDomain.getSubFlowConfigJson() != null) {
                flowableActivityConfigEntity.setSubFlowConfig(
                        JSONObject.toJSONString(flowableActivityConfigDomain.getSubFlowConfigJson()));
            }
            if (CollectionUtils.isNotEmpty(flowableActivityConfigDomain.getFieldConfigList())) {
                flowableActivityConfigEntity.setFieldConfig(
                        JSONObject.toJSONString(flowableActivityConfigDomain.getFieldConfigList()));
            }
            if (CollectionUtils.isNotEmpty(flowableActivityConfigDomain.getCopyConfigList())) {
                flowableActivityConfigEntity.setCopyConfig(
                        JSONObject.toJSONString(flowableActivityConfigDomain.getCopyConfigList()));
            }
            if (CollectionUtils.isNotEmpty(flowableActivityConfigDomain.getAssigneeConfigList())) {
                flowableActivityConfigEntity.setAssigneeConfig(
                        JSONObject.toJSONString(flowableActivityConfigDomain.getAssigneeConfigList()));
            }
            flowableActivityConfigEntity.setCreatorName(user.getNickName());
            flowableActivityConfigEntity.setModifierName(user.getNickName());
            flowableActivityConfigEntityList.add(flowableActivityConfigEntity);

        }
        if (CollectionUtils.isNotEmpty(flowableActivityConfigEntityList)) {
            saveBatch(flowableActivityConfigEntityList);
        }
    }

    @Override
    public List<FlowableActivityConfigDomain> detail(String modelId, String activityId) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(StringUtils.isNotEmpty(activityId), FlowableActivityConfigEntity::getActivityId, activityId);
        List<FlowableActivityConfigEntity> flowableActivityConfigEntityList =
                flowableActivityConfigMapper.selectList(queryWrapper);
        List<Long> userIdList = new ArrayList<>();
        List<Long> deptList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        List<FlowableActivityConfigDomain> flowableActivityConfigDomainList =
                getFlowableConfig(flowableActivityConfigEntityList, userIdList, deptList, postIdList);
        Map<Long, String> userNameMap = userService.getIdToNameMap(userIdList);

        Map<Long, String> deptNameMap = departmentService.deptIdToMap(deptList);

        Map<Long, String> postNameMap = postService.getListByIdList(postIdList);
        for (FlowableActivityConfigDomain flowableActivityConfigDomain : flowableActivityConfigDomainList) {
            for (FlowableAssigneeConfig assigneeConfig : flowableActivityConfigDomain.getAssigneeConfigList()) {
                if (AssigneeTypeEnum.USER.name().equals(assigneeConfig.getAssigneeType())) {
                    assigneeConfig.setAssigneeName(userNameMap.get(Long.valueOf(assigneeConfig.getAssigneeId())));
                } else if (AssigneeTypeEnum.DEPT.name().equals(assigneeConfig.getAssigneeType())) {
                    assigneeConfig.setAssigneeName(deptNameMap.get(Long.valueOf(assigneeConfig.getAssigneeId())));
                } else if (AssigneeTypeEnum.POST.name().equals(assigneeConfig.getAssigneeType())) {
                    assigneeConfig.setAssigneeName(postNameMap.get(Long.valueOf(assigneeConfig.getAssigneeId())));
                }
            }

            for (FlowableCopyConfig flowableCopyConfig : flowableActivityConfigDomain.getCopyConfigList()) {
                if (AssigneeTypeEnum.USER.name().equalsIgnoreCase(flowableCopyConfig.getBusinessType())) {
                    flowableCopyConfig.setBusinessName(
                            userNameMap.get(Long.valueOf(flowableCopyConfig.getBusinessId())));
                } else if (AssigneeTypeEnum.DEPT.name().equalsIgnoreCase(flowableCopyConfig.getBusinessType())) {
                    flowableCopyConfig.setBusinessName(
                            deptNameMap.get(Long.valueOf(flowableCopyConfig.getBusinessId())));
                } else if (AssigneeTypeEnum.POST.name().equalsIgnoreCase(flowableCopyConfig.getBusinessType())) {
                    flowableCopyConfig.setBusinessName(
                            postNameMap.get(Long.valueOf(flowableCopyConfig.getBusinessId())));
                }
            }

        }

        return flowableActivityConfigDomainList;
    }

    @Override
    public List<FlowableActivityConfigDomain> getByModelIdList(List<String> modelIdList, String type) {
        if (CollectionUtils.isEmpty(modelIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowableActivityConfigEntity::getModelId, modelIdList);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(StringUtils.isNotEmpty(type), FlowableActivityConfigEntity::getActivityType, type);
        List<FlowableActivityConfigEntity> flowableActivityConfigEntityList =
                flowableActivityConfigMapper.selectList(queryWrapper);

        return getFlowableConfig(flowableActivityConfigEntityList, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());
    }

    @Override
    public List<Long> getCopyUser(String activityId, String modelId, Map<String, Object> processVariables,
                                  JSONObject instValue) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getActivityId, activityId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        FlowableActivityConfigEntity flowableActivityConfigEntity =
                flowableActivityConfigMapper.selectOne(queryWrapper);
        if (flowableActivityConfigEntity == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(flowableActivityConfigEntity.getCopyConfig())) {
            return new ArrayList<>();
        }
        List<FlowableCopyConfig> flowableCopyConfigList =
                JSONObject.parseArray(flowableActivityConfigEntity.getCopyConfig(), FlowableCopyConfig.class);
        List<Long> userIdList = new ArrayList<>();

        List<Long> deptIdList = new ArrayList<>();

        List<Long> postIdList = new ArrayList<>();
        for (FlowableCopyConfig flowableCopyConfig : flowableCopyConfigList) {
            if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.USER.name())) {
                userIdList.add(Long.valueOf(flowableCopyConfig.getBusinessId()));
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.DEPT.getType())) {
                deptIdList.add(Long.valueOf(flowableCopyConfig.getBusinessId()));
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.SELF.getType())) {
                String initiator = processVariables.get(FlowableConstant.PROCESS_INITIATOR).toString();
                if (StringUtils.isNotEmpty(initiator)) {
                    userIdList.add(Long.valueOf(initiator));
                }
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.POST.getType())) {
                postIdList.add(Long.valueOf(flowableCopyConfig.getBusinessId()));
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.FORM_DEPT.getType())) {
                Object deptObject = instValue.get(flowableCopyConfig.getBusinessId());
                if (deptObject != null) {
                    List<FormDept> flowableFormDeptList =
                            JSONArray.parseArray(JSONObject.toJSONString(deptObject), FormDept.class);
                    deptIdList.addAll(
                            flowableFormDeptList.stream().map(FormDept::getValue).collect(Collectors.toList()));
                }
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.FORM_USER.getType())) {
                Object userObject = instValue.get(flowableCopyConfig.getBusinessId());
                if (userObject != null) {
                    List<FormUser> flowableFormDeptList =
                            JSONArray.parseArray(JSONObject.toJSONString(userObject), FormUser.class);
                    userIdList.addAll(
                            flowableFormDeptList.stream().map(FormUser::getAssigneeId).collect(Collectors.toList()));
                }
            } else if (flowableCopyConfig.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.FORM_POST.getType())) {
                Object postObject = instValue.get(flowableCopyConfig.getBusinessId());
                if (postObject != null) {
                    List<FormPost> flowableFormPostList =
                            JSONArray.parseArray(JSONObject.toJSONString(postObject), FormPost.class);
                    postIdList.addAll(flowableFormPostList.stream().map(c -> Long.valueOf(c.getValue()))
                            .collect(Collectors.toList()));
                }
            }
        }
        List<Long> allChildren = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(), deptIdList);
        userIdList.addAll(userDeptService.getByDeptIdList(allChildren));
        userIdList.addAll(userPostService.getUserIdByPostIdList(postIdList));
        return userIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public FlowableSubFlowConfig getFlowableSubConfig(String activityId, String modelId) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getActivityId, activityId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        FlowableActivityConfigEntity flowableActivityConfigEntity =
                flowableActivityConfigMapper.selectOne(queryWrapper);
        if (flowableActivityConfigEntity == null) {
            return null;
        }
        if (flowableActivityConfigEntity.getSubFlowConfig() == null) {
            return null;
        }
        return JSONObject.parseObject(flowableActivityConfigEntity.getSubFlowConfig(), FlowableSubFlowConfig.class);
    }

    @Override
    public FlowableRemindConfig getRemindConfig(String activityId, String modelId) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getActivityId, activityId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        FlowableActivityConfigEntity flowableActivityConfigEntity =
                flowableActivityConfigMapper.selectOne(queryWrapper);
        if (flowableActivityConfigEntity == null) {
            return null;
        }
        if (flowableActivityConfigEntity.getRemindConfig() == null) {
            return null;
        }
        FlowableRemindConfig flowableRemindConfig =
                JSONObject.parseObject(flowableActivityConfigEntity.getRemindConfig(), FlowableRemindConfig.class);
        if (CollectionUtils.isNotEmpty(flowableRemindConfig.getAssigneeUserList())) {
            List<String> assigneeUserList =
                    getAssigneeUserList(new HashMap<>(), flowableRemindConfig.getAssigneeList());
            flowableRemindConfig.setAssigneeUserList(assigneeUserList);
        }
        return flowableRemindConfig;
    }

    @Override
    public List<String> getAssigneeUserList(String activityId, String modelId, Map<String, Object> variables) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getActivityId, activityId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        FlowableActivityConfigEntity flowableActivityConfigEntity =
                flowableActivityConfigMapper.selectOne(queryWrapper);
        if (flowableActivityConfigEntity == null) {
            return new ArrayList<>();
        }

        List<FlowableAssigneeConfig> flowableAssigneeConfigList =
                JSONObject.parseArray(flowableActivityConfigEntity.getAssigneeConfig(), FlowableAssigneeConfig.class);
        boolean existNoBody = flowableAssigneeConfigList.stream()
                .anyMatch(c -> AssigneeTypeEnum.NO_BODY.getType().equals(c.getAssigneeType()));
        if (existNoBody) {
            return Collections.singletonList(UserDefaultEnum.NO_BODY.getId().toString());
        }
        return getAssigneeUserList(variables, flowableAssigneeConfigList);
    }

    @Override
    public List<String> getCandidateList(String activityId, String modelId, Map<String, Object> variables) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getActivityId, activityId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        FlowableActivityConfigEntity flowableActivityConfigEntity =
                flowableActivityConfigMapper.selectOne(queryWrapper);
        if (flowableActivityConfigEntity == null) {
            return new ArrayList<>();
        }
        List<FlowableAssigneeConfig> flowableAssigneeConfigList =
                JSONObject.parseArray(flowableActivityConfigEntity.getAssigneeConfig(), FlowableAssigneeConfig.class);

        return getAssigneeUserList(variables, flowableAssigneeConfigList);
    }

    @Override
    public List<String> getAssigneeUserList(Map<String, Object> variables,
                                            List<FlowableAssigneeConfig> flowableAssigneeConfigList) {

        List<String> userIdList = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();

        buildAssignee(variables, flowableAssigneeConfigList, userIdList, deptIdList, postIdList);
        List<Long> allChildren = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(), deptIdList);
        userIdList.addAll(userDeptService.getByDeptIdList(allChildren).stream().map(String::valueOf)
                .collect(Collectors.toList()));
        userIdList.addAll(userPostService.getUserIdByPostIdList(postIdList).stream().map(String::valueOf)
                .collect(Collectors.toList()));
        UserSelectRequest userSelectRequest = new UserSelectRequest();
        userSelectRequest.setDepartmentIdList(deptIdList);
        userSelectRequest.setPostIdList(postIdList);
        userSelectRequest.setIdList(userIdList);
        userSelectRequest.setPageSize(500);
        if (CollectionUtils.isEmpty(userIdList) && CollectionUtils.isEmpty(postIdList) &&
                CollectionUtils.isEmpty(deptIdList)) {
            return new ArrayList<>();
        }
        QueryPageVO<UserVO> userVOQueryPageVO = userService.querySelectList(userSelectRequest);
        return userVOQueryPageVO.getList().stream().map(c -> c.getUserId().toString()).collect(Collectors.toList());
    }

    @Override
    public List<FlowableActivityConfigDomain> getConditionConfig(String modelId, String pid) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableActivityConfigEntity::getModelId, modelId);
        queryWrapper.eq(FlowableActivityConfigEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(StringUtils.isNotEmpty(pid), FlowableActivityConfigEntity::getActivityPid, pid);
        List<FlowableActivityConfigEntity> flowableActivityConfigEntityList =
                flowableActivityConfigMapper.selectList(queryWrapper);
        List<FlowableActivityConfigDomain> flowableActivityConfigDomainList = new ArrayList<>();
        for (FlowableActivityConfigEntity flowableActivityConfigEntity : flowableActivityConfigEntityList) {
            FlowableActivityConfigDomain flowableActivityConfigVO =
                    AbstractFlowableActivityConfigConverter.INSTANCE.toDomain(flowableActivityConfigEntity);
            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getConditionConfig())) {
                flowableActivityConfigVO.setConditionConfigJson(
                        JSONObject.parseObject(flowableActivityConfigEntity.getConditionConfig(),
                                FlowableMongodbSearchFilter.class));
            }
            flowableActivityConfigDomainList.add(flowableActivityConfigVO);
        }
        return flowableActivityConfigDomainList;
    }

    @Override
    public void delete(List<String> modelIdList) {
        LambdaQueryWrapper<FlowableActivityConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowableActivityConfigEntity::getModelId, modelIdList);
        flowableActivityConfigMapper.delete(queryWrapper);
    }

    private static void buildAssignee(Map<String, Object> variables,
                                      List<FlowableAssigneeConfig> flowableAssigneeConfigList, List<String> userIdList,
                                      List<Long> deptIdList, List<Long> postIdList) {
        for (FlowableAssigneeConfig flowableAssigneeConfig : flowableAssigneeConfigList) {
            if (flowableAssigneeConfig.getAssigneeType().equalsIgnoreCase(AssigneeTypeEnum.USER.getType())) {
                userIdList.add(flowableAssigneeConfig.getAssigneeId());
            } else if (flowableAssigneeConfig.getAssigneeType().equalsIgnoreCase(AssigneeTypeEnum.DEPT.getType())) {
                deptIdList.add(Long.valueOf(flowableAssigneeConfig.getAssigneeId()));
            } else if (flowableAssigneeConfig.getAssigneeType().equalsIgnoreCase(AssigneeTypeEnum.SELF.getType())) {
                // 发起人
                String initiator = variables.getOrDefault(FlowableConstant.PROCESS_INITIATOR, "").toString();
                if (StringUtils.isEmpty(initiator)) {
                    initiator = variables.getOrDefault(FlowableConstant.INITIATOR, "").toString();
                }
                if (!UserDefaultEnum.ANONYMOUS_USER.getId().toString().equals(initiator)) {
                    userIdList.add(initiator);
                }
            } else if (flowableAssigneeConfig.getAssigneeType().equalsIgnoreCase(AssigneeTypeEnum.POST.getType())) {
                postIdList.add(Long.valueOf(flowableAssigneeConfig.getAssigneeId()));
            } else if (flowableAssigneeConfig.getAssigneeType()
                    .equalsIgnoreCase(AssigneeTypeEnum.FORM_DEPT.getType())) {
                Object deptObject = variables.get(flowableAssigneeConfig.getAssigneeId());
                if (deptObject != null && StringUtils.isNotEmpty(deptObject.toString())) {
                    List<FormDept> flowableFormDeptList =
                            JSONArray.parseArray(JSONObject.toJSONString(deptObject), FormDept.class);
                    deptIdList.addAll(
                            flowableFormDeptList.stream().map(FormDept::getValue).collect(Collectors.toList()));
                }
            } else if (flowableAssigneeConfig.getAssigneeType()
                    .equalsIgnoreCase(AssigneeTypeEnum.FORM_USER.getType())) {
                Object userObject = variables.get(flowableAssigneeConfig.getAssigneeId());
                if (userObject != null && StringUtils.isNotEmpty(userObject.toString())) {
                    List<FormUser> flowableFormDeptList =
                            JSONArray.parseArray(JSONObject.toJSONString(userObject), FormUser.class);
                    userIdList.addAll(flowableFormDeptList.stream().map(c -> String.valueOf(c.getAssigneeId()))
                            .collect(Collectors.toList()));
                }
            } else if (flowableAssigneeConfig.getAssigneeId().equalsIgnoreCase(AssigneeTypeEnum.FORM_POST.getType())) {
                Object postObject = variables.get(flowableAssigneeConfig.getAssigneeType());
                if (postObject != null && StringUtils.isNotEmpty(postObject.toString())) {
                    List<FormPost> flowableFormPostList =
                            JSONArray.parseArray(JSONObject.toJSONString(postObject), FormPost.class);
                    postIdList.addAll(flowableFormPostList.stream().map(c -> Long.valueOf(c.getValue()))
                            .collect(Collectors.toList()));
                }
            }

        }
    }

    /**
     * 获取流程配置
     *
     * @param flowableActivityConfigEntityList
     * @param userIdList
     * @param deptList
     * @return
     */
    private static List<FlowableActivityConfigDomain> getFlowableConfig(
            List<FlowableActivityConfigEntity> flowableActivityConfigEntityList, List<Long> userIdList,
            List<Long> deptList, List<Long> postIdList) {
        List<FlowableActivityConfigDomain> flowableActivityConfigVOS = new ArrayList<>();
        for (FlowableActivityConfigEntity flowableActivityConfigEntity : flowableActivityConfigEntityList) {
            FlowableActivityConfigDomain flowableActivityConfigVO =
                    AbstractFlowableActivityConfigConverter.INSTANCE.toDomain(flowableActivityConfigEntity);

            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getConditionConfig())) {
                flowableActivityConfigVO.setConditionConfigJson(
                        JSONObject.parseObject(flowableActivityConfigEntity.getConditionConfig(),
                                FlowableMongodbSearchFilter.class));
            }

            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getSubFlowConfig())) {
                flowableActivityConfigVO.setSubFlowConfigJson(
                        JSONObject.parseObject(flowableActivityConfigEntity.getSubFlowConfig(),
                                FlowableSubFlowConfig.class));
            }
            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getFieldConfig())) {
                flowableActivityConfigVO.setFieldConfigList(
                        JSONObject.parseArray(flowableActivityConfigEntity.getFieldConfig(),
                                FlowableFormFieldConfig.class));
            } else {
                flowableActivityConfigVO.setFieldConfigList(new ArrayList<>());
            }
            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getCopyConfig())) {
                flowableActivityConfigVO.setCopyConfigList(
                        JSONObject.parseArray(flowableActivityConfigEntity.getCopyConfig(), FlowableCopyConfig.class));
            } else {
                flowableActivityConfigVO.setCopyConfigList(new ArrayList<>());
            }
            if (StringUtils.isNotEmpty(flowableActivityConfigEntity.getAssigneeConfig())) {
                flowableActivityConfigVO.setAssigneeConfigList(
                        JSONObject.parseArray(flowableActivityConfigEntity.getAssigneeConfig(),
                                FlowableAssigneeConfig.class));
            } else {
                flowableActivityConfigVO.setAssigneeConfigList(new ArrayList<>());
            }
            flowableActivityConfigVOS.add(flowableActivityConfigVO);

            userIdList.addAll(flowableActivityConfigVO.getCopyConfigList().stream()
                    .filter(d -> d.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.USER.name()))
                    .map(d -> Long.valueOf(d.getBusinessId())).collect(Collectors.toList()));

            deptList.addAll(flowableActivityConfigVO.getCopyConfigList().stream()
                    .filter(d -> d.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.DEPT.name()))
                    .map(d -> Long.valueOf(d.getBusinessId())).collect(Collectors.toList()));

            postIdList.addAll(flowableActivityConfigVO.getCopyConfigList().stream()
                    .filter(d -> d.getBusinessType().equalsIgnoreCase(AssigneeTypeEnum.POST.name()))
                    .map(d -> Long.valueOf(d.getBusinessId())).collect(Collectors.toList()));

            userIdList.addAll(flowableActivityConfigVO.getAssigneeConfigList().stream()
                    .filter(d -> d.getAssigneeType().equals(AssigneeTypeEnum.USER.name()))
                    .map(d -> Long.valueOf(d.getAssigneeType())).collect(Collectors.toList()));

            deptList.addAll(flowableActivityConfigVO.getAssigneeConfigList().stream()
                    .filter(d -> d.getAssigneeType().equals(AssigneeTypeEnum.DEPT.name()))
                    .map(d -> Long.valueOf(d.getAssigneeType())).collect(Collectors.toList()));

            postIdList.addAll(flowableActivityConfigVO.getAssigneeConfigList().stream()
                    .filter(d -> d.getAssigneeType().equals(AssigneeTypeEnum.POST.name()))
                    .map(d -> Long.valueOf(d.getAssigneeType())).collect(Collectors.toList()));
        }
        return flowableActivityConfigVOS;
    }
}
