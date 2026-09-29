package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormViewFieldGroupRequest {
    private String formId;

    private String applicationId;

    private String groupId;

    private MongodbSearchFilter filter;

    private List<MongodbSearchField> groupFields;

    private List<MongodbSearchField> metricList = new ArrayList<>();

    private List<String> keyList;

    private String keyword;

}
