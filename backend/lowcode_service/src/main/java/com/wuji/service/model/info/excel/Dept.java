package com.wuji.service.model.info.excel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dept {
    private Long deptId;
    private String deptName;
}
