package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ManageDeptSystemRequest {
    private List<Long> departmentIdList;

    private String deptType;
}
