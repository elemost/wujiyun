package com.wuji.service.model.request;

import com.wuji.service.model.info.FormFataFactorySyncConfig;
import lombok.Data;

@Data
public class FormDataFactorySyncConfigRequest {
    private String id;

    private String applicationId;

    private FormFataFactorySyncConfig syncConfig;
}
