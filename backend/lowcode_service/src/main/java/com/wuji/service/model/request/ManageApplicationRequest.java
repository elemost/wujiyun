package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ManageApplicationRequest {

    private List<String> applicationIdList;

    private String groupId;

}
