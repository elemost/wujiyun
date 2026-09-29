package com.wuji.service.model.info.stream;

import com.wuji.service.utils.UserDeptUtils;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamFieldTrans {
    private String fieldId;

    private String subForm;

    private String fieldType;


    private String quoteType;

    /**
     * 自定义value
     */
    private Object customValue;

    private DataStreamQuoteField quoteField;

    private DataStreamCalculate calculate;

    public Object dealCustomValue() {
        List<Object> objectList = UserDeptUtils.dealUserDept(this.fieldType, this.getFieldId(), customValue);
        if (objectList == null) {
            return customValue;
        }
        return objectList;
    }

}
