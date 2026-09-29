package com.wuji.workflow.utils;


import com.wuji.common.utils.TimeUtils;
import com.wuji.workflow.constant.FlowableConstant;
import com.wuji.workflow.enums.ProcessStateEnum;
import com.wuji.workflow.model.domain.FlowablePageQueryDomain;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.query.Query;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;

import java.util.Collections;
import java.util.Map;

/**
 * 流程工具类
 *
 * @author konbai
 * @since 2022/12/11 03:35
 */
public class ProcessUtils {

    public static void buildProcessSearch(Query<?, ?> query, FlowablePageQueryDomain process) {
        if (query instanceof ProcessDefinitionQuery) {
            // buildProcessDefinitionSearch((ProcessDefinitionQuery) query, process);
        } else if (query instanceof TaskQuery) {
            buildTaskSearch((TaskQuery) query, process);
        } else if (query instanceof HistoricTaskInstanceQuery) {
            buildHistoricTaskInstanceSearch((HistoricTaskInstanceQuery) query, process);
        } else if (query instanceof HistoricProcessInstanceQuery) {
            buildHistoricProcessInstanceSearch((HistoricProcessInstanceQuery) query, process);
        }
    }

    /**
     * 构建流程定义搜索
     */
    public static void buildProcessDefinitionSearch(ProcessDefinitionQuery query, FlowablePageQueryDomain process) {
        // 流程标识
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        // 流程名称
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        // 流程分类
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processDefinitionCategory(process.getCategory());
        }
        // 流程状态
        if (StringUtils.isNotBlank(process.getState())) {
            if (SuspensionState.ACTIVE.toString().equals(process.getState())) {
                query.active();
            } else if (SuspensionState.SUSPENDED.toString().equals(process.getState())) {
                query.suspended();
            }
        }
    }

    /**
     * 构建任务搜索
     */
    public static void buildTaskSearch(TaskQuery query, FlowablePageQueryDomain process) {
        Map<String, Object> params = process.getParams();
        if (StringUtils.isNotEmpty(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        if (StringUtils.isNotEmpty(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        if (StringUtils.isNotEmpty(process.getCategory())) {
            query.processCategoryIn(Collections.singletonList(process.getCategory()));
        }
        if (StringUtils.isNotEmpty(process.getTaskId())) {
            query.taskId(process.getTaskId());
        }
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.taskCreatedAfter(TimeUtils.convertDate(params.get("beginTime").toString()));
            query.taskCreatedBefore(TimeUtils.convertDate(params.get("endTime").toString()));
            params.remove("beginTime");
            params.remove("endTime");
        }
        if (CollectionUtils.isNotEmpty(process.getCreateUsers())) {
            query.or();
            for (String createUser : process.getCreateUsers()) {
                query.processVariableValueEquals(FlowableConstant.PROCESS_INITIATOR, createUser);
            }
            query.endOr();
        }
        process.getParams().forEach(query::processVariableValueEquals);
    }

    private static void buildHistoricTaskInstanceSearch(HistoricTaskInstanceQuery query,
                                                        FlowablePageQueryDomain process) {
        Map<String, Object> params = process.getParams();
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        if (StringUtils.isNotEmpty(process.getTaskId())) {
            query.taskId(process.getTaskId());
        }
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processCategoryIn(Collections.singletonList(process.getCategory()));
        }
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.taskCompletedAfter(TimeUtils.convertDate(params.get("beginTime").toString()));
            query.taskCompletedBefore(TimeUtils.convertDate(params.get("endTime").toString()));
        }

        process.getParams().forEach(query::processVariableValueEquals);
    }

    /**
     * 构建历史流程实例搜索
     */
    public static void buildHistoricProcessInstanceSearch(HistoricProcessInstanceQuery query,
                                                          FlowablePageQueryDomain process) {
        Map<String, Object> params = process.getParams();
        // 流程标识
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKey(process.getProcessKey());
        }
        // 流程名称
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionName(process.getProcessName());
        }
        // 流程名称
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processDefinitionCategory(process.getCategory());
        }
        if (StringUtils.isNotBlank(process.getProcessInstanceId())) {
            query.processInstanceId(process.getProcessInstanceId());
        }
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.startedAfter(TimeUtils.convertDate(params.get("beginTime").toString()));
            query.startedBefore(TimeUtils.convertDate(params.get("endTime").toString()));
        }
        Object object = process.getParams().get(FlowableConstant.PROCESS_STATUS_SING_KEY);
        if (object != null) {
            query.or();
            if (object.toString().equals("running")) {
                for (ProcessStateEnum processStateEnum : ProcessStateEnum.running()) {
                    query.variableValueEquals(FlowableConstant.PROCESS_STATUS_SING_KEY, processStateEnum.getStatus());
                }
            } else if (object.toString().equals("completed")) {
                for (ProcessStateEnum processStateEnum : ProcessStateEnum.finish()) {
                    query.variableValueEquals(FlowableConstant.PROCESS_STATUS_SING_KEY, processStateEnum.getStatus());
                }
            }
            query.endOr();
        }

        process.getParams().remove(FlowableConstant.PROCESS_STATUS_SING_KEY);
        process.getParams().forEach(query::variableValueEquals);
    }

}
