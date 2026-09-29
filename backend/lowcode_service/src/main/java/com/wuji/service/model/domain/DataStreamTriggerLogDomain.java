package com.wuji.service.model.domain;

import com.wuji.service.model.info.FormDataTitle;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "data_stream_trigger_log")
@Data
public class DataStreamTriggerLogDomain {

    private String uuid;

    private String dataStreamId;

    private Integer dataStreamVersion;

    private String creator;

    private String createName;

    private String action;

    private String applicationId;

    private String formId;

    private FormDataTitle title;

    private Long triggerStartTime;

    private Long triggerEndTime;

    private String result;

    private String exceptionError;

    private List<String> parentList;

    private String lastParentId;
}
