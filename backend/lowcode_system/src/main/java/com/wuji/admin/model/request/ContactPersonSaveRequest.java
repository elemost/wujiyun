package com.wuji.admin.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ContactPersonSaveRequest {
    private Long companyId;

    private List<Long> userIdList;
}
