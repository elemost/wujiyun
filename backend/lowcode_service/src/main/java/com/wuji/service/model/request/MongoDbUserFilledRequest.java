package com.wuji.service.model.request;

import com.wuji.service.model.request.publish.PublicPublishSecretRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MongoDbUserFilledRequest extends PublicPublishSecretRequest {
    private String publicUserSign;

    private String applicationId;

    private String formId;
}
