package com.wuji.workflow.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

@Data
public class ModelListRequest extends BasePageRequest {
    private String key;

    private String name;

    private String applicationId;
}
