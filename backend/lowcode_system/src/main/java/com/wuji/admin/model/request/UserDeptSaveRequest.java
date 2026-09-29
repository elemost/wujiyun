package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class UserDeptSaveRequest {
    private Long userId;

    private Long deptId;
}
