package com.wuji.service.model.request.publish;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublicPublishSecretRequest extends BasePageRequest {
    private String accessToken;

    private String publishType;

    private String publishFormId;

    private String applicationId;
}
