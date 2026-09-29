package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbBatchRequest {
    private String formId;

    private String applicationId;

    private String groupId;

    // UUID
    private String updateType;

    private List<String> uuidList;

    private MongodbSearchFilter filter;

    private List<UpdateField>  fields;

    @Data
    public static class UpdateField {
        private String subForm;

        private String fieldId;

        private Object value;

        private String fieldType;

    }

}
