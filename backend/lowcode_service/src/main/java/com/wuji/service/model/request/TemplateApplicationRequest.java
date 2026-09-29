package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateApplicationRequest extends BasePageRequest {
    private String applicationName;

    private String scene;

    private String industry;

    private String sortType;

    private String tag;
}
