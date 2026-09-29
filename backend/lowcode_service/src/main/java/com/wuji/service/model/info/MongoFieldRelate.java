package com.wuji.service.model.info;

import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.utils.UserDeptUtils;
import lombok.Data;

import java.util.List;

@Data
public class MongoFieldRelate {

    private Long nodeId;

    private String fieldId;

    private String fieldType;

    private String subForm;

    private String method;

    private String relMethod;

    /**
     * EMPTY, NODE_FIELD , CUSTOM
     */
    private String mode;

    private String quoteType;

    private String searchType;

    private List<Object> value;

    // private MongoFieldRelateVariable variable;

    private DataStreamQuoteField quoteField;

    public List<Object> dealValue() {
        List<Object> objectList = UserDeptUtils.dealUserDept(this.fieldType, this.getFieldId(), value);
        if (objectList == null) {
            return value;
        } else {
            return objectList;
        }
    }

    @Data
    public static class MongoFieldRelateVariable {
        private Long nodeId;

        private String sourceType;

        private String fieldId;

        private String subFieldId;

        private String fieldType;

        public Long getNodeId() {
            if (nodeId == null) {
                return null;
            }
            if (nodeId == 2) {
                return 1L;
            } else {
                return nodeId;
            }
        }
    }

    public Long getNodeId() {
        if (nodeId == null) {
            return null;
        }
        if (nodeId == 2) {
            return 1L;
        } else {
            return nodeId;
        }
    }
}
