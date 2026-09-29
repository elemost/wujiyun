package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamTimeTriggerNode extends DataStreamCommon {
    // custom formField
    private String triggerTimeType;

    private Date customTime;

    private String repeatTrigger;

    private Date endTime;

    private String customCron;

    private String triggerFormId;

    private String dateFieldId;

    private String daytime;

    private Integer offset;

    private String offsetUnit;

    private DataStreamConditionRel condition;
}
