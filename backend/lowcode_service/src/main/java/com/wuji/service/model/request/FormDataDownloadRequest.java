package com.wuji.service.model.request;

import com.wuji.common.privilege.annotation.ApplicationId;
import lombok.Data;

@Data
public class FormDataDownloadRequest {
    private String uuid;

    @ApplicationId
    private String applicationId;

    private String formId;

    private String templateId;
}
