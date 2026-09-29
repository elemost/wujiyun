package com.wuji.workflow.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.entity.FlowableActivityConfigEntity;
import com.wuji.workflow.model.info.FlowableAssigneeConfig;
import com.wuji.workflow.model.info.FlowableRemindConfig;
import com.wuji.workflow.model.info.FlowableSubFlowConfig;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
public interface FlowableActivityConfigService extends IService<FlowableActivityConfigEntity> {
    void save(String modelId, List<FlowableActivityConfigDomain> flowableActivityConfigDomainList);

    List<FlowableActivityConfigDomain> detail(String modelId, String activityId);

    List<FlowableActivityConfigDomain> getByModelIdList(List<String> modelIdList, String type);


    /**
     * 获取抄送userid
     *
     * @param activityId
     * @param modelId
     * @param processVariables
     * @param instValue
     * @return
     */
    List<Long> getCopyUser(String activityId, String modelId, Map<String, Object> processVariables, JSONObject instValue);

    FlowableSubFlowConfig getFlowableSubConfig(String activityId, String modelId);

    FlowableRemindConfig getRemindConfig(String activityId, String modelId);

    /**
     * 获取抄送userid
     *
     * @param activityId
     * @param modelId
     * @return
     */
    List<String> getAssigneeUserList(String activityId, String modelId, Map<String, Object> variables);

    List<String> getCandidateList(String activityId, String modelId, Map<String, Object> variables);

    List<String> getAssigneeUserList(Map<String, Object> variables,
                                     List<FlowableAssigneeConfig> flowableAssigneeConfigList);

    List<FlowableActivityConfigDomain> getConditionConfig(String modelId, String pid);

    void delete(List<String> modelIdList);
}
