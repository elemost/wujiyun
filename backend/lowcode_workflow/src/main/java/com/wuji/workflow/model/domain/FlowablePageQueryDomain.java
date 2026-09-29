package com.wuji.workflow.model.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FlowablePageQueryDomain {
    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程分类
     */
    private String category;

    /**
     * 状态
     */
    private String state;

    private String taskId;

    private String processInstanceId;

    private List<String> createUsers;

    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
