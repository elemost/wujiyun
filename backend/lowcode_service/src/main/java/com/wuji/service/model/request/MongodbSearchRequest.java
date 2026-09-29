package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

import java.util.List;

@Data
public class MongodbSearchRequest extends BasePageRequest {

    private String formId;

    private List<MongodbSearchKeyRequest> mongodbSearchKeyRequestList;

    private String collection;

    private String keyword;
}
