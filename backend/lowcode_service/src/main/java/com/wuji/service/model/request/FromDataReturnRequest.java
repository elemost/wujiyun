package com.wuji.service.model.request;

import lombok.Data;

import java.util.Map;

@Data
public class FromDataReturnRequest {
    private Map<Long, String> userIdToNameMap;

    private Map<Long, String> deptIdToNameMap;

    private Map<Long, String> roleIdToNameMap;
}
