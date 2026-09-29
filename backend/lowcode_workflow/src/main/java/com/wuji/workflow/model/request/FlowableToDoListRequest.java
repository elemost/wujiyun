package com.wuji.workflow.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FlowableToDoListRequest extends BasePageRequest {
    private String taskName;

    private String processName;

    private String category;

    private String taskId;

    private List<String> createUsers;

    private List<String> processInstanceIds;

    private HashMap<String, Object> params;
}
