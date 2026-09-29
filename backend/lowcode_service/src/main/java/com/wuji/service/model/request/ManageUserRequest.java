package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ManageUserRequest {
    private List<Long> userIdList;

    private String groupId;
}
