package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FormDataStreamLogRequest extends BasePageRequest {
    private String dataStreamId;

    private String formId;

    private String applicationId;

    private Date startTime;

    private Date endTime;

    private String result;

    private List<String> creators;
}
