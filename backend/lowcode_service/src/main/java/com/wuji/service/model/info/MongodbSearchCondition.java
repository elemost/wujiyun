package com.wuji.service.model.info;

import com.wuji.service.utils.UserDeptUtils;
import lombok.Data;

import java.util.List;

@Data
public class MongodbSearchCondition {

    private String id;

    private String fieldId;

    private String childFieldId;

    private String method;

    private String type;

    private String searchType;

    // CURRENT_USER CURRENT_DEPT CUSTOM_DATA
    private String valueType;

    private List<Object> value;

    private String formId;
    // PRIVILEGE CUSTOM
    private String quoteType = "CUSTOM";

    private MongodbSearchConditionQuote quote;

    public List<Object> dealValue() {
        List<Object> objectList = UserDeptUtils.dealUserDept(this.type, this.getFieldId(), value);
        if (objectList == null) {
            return value;
        } else {
            return objectList;
        }
    }
}
