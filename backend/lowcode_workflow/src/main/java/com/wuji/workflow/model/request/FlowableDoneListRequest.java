package com.wuji.workflow.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FlowableDoneListRequest extends BasePageRequest {
    /**
     * 流程标识
     */
    private String processKey;

    /**
     * 流程名称
     */
    private String processName;


    private String category;

    private String taskId;

    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
