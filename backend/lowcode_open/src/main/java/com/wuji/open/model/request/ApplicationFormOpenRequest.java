package com.wuji.open.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationFormOpenRequest extends FormOpenCommonRequest {
    private String applicationId;
}
