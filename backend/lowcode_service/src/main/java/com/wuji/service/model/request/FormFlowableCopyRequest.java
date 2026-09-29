package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

@Data
public class FormFlowableCopyRequest extends BasePageRequest {
    private String applicationId;

    private Boolean userView;

    private Long id;

    @CorpCoop
    private String companyUuid;
}
