package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationUserSortSaveRequest {
    private List<String> applicationIdList;
}
