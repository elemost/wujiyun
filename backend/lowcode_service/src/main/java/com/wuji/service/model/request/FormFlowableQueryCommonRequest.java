package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormFlowableQueryCommonRequest extends BasePageRequest {

    private String status;

    private Date startTime;

    private Date endTime;

    private String formId;

    private List<String> processInstanceIds;
}
