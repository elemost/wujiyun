package com.wuji.open.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptOpenTreeRequest extends FormOpenCommonRequest {
    private String deptName;
}
