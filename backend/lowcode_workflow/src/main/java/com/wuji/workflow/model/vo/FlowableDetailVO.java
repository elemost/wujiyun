package com.wuji.workflow.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FlowableDetailVO {

    /**
     * 历史流程节点信息
     */
    private List<FlowableOperateLogVO> historyProcNodeList;

    private ProcessViewerVO flowViewer;

    private String bpmnXml;

}
