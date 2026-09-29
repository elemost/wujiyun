package com.wuji.workflow.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessViewerVO {
    /**
     * 获取流程实例的历史节点（去重）
     */
    private Set<String> finishedTaskSet;

    /**
     * 已完成
     */
    private Set<String> finishedSequenceFlowSet;

    /**
     * 获取流程实例当前正在待办的节点（去重）
     */
    private Set<String> unfinishedTaskSet;

    /**
     * 已拒绝
     */
    private Set<String> rejectedTaskSet;


    private Map<String, String> activityIdToNameMap;
}
