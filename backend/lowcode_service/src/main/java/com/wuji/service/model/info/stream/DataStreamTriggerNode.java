package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamTriggerNode extends DataStreamCommon {
    private String triggerFormId;

    private DataStreamConditionRel condition;

    private String triggerType;

    private List<DataStreamTriggerAction> actions;


    @Data
    public static class DataStreamTriggerAction {
        // create delete update process_terminated activity_finish
        private String action;

        private List<String> triggerFields;
        // AT_WILL SPEC_FIELD SPEC_FIELD_VALUE
        private String mode;

        private String activityId;

        private List<String> activityActions;

        private String rel;

        private List<DataStreamTriggerFieldValue> triggerFieldValues;
    }

    @Data
    public static class DataStreamTriggerFieldValue {
        private String fieldId;

        private Object preValue;

        private Object currentValue;
    }
}
