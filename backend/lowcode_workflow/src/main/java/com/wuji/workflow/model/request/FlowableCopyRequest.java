package com.wuji.workflow.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FlowableCopyRequest extends BasePageRequest {
    private String applicationId;

    private Boolean userView;

    private Long id;
}
