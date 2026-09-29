package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class MangeListRequest {
    private List<Long> userIdList;

    private List<String> applicationIdList;

    private List<String> groupIdList;
}
