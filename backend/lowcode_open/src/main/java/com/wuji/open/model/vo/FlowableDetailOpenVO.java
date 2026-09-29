package com.wuji.open.model.vo;

import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import com.wuji.workflow.model.vo.ProcessViewerVO;
import lombok.Data;

import java.util.List;

@Data
public class FlowableDetailOpenVO {
    /**
     * 历史流程节点信息
     */
    private List<FlowableOperateLogVO> historyProcNodeList;

    private ProcessViewerVO flowViewer;
}
