package com.wuji.workflow.model.domain;

import lombok.Data;

@Data
public class AssigneeDomain {
    /**
     * 执行人Id
     */
    private Long assigneeId;
    /**
     * 执行人名称
     */
    private String assigneeName;

    private String comment;

    private String type;

    private String taskId;
}
