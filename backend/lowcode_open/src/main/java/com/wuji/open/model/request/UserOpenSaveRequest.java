package com.wuji.open.model.request;

import lombok.Data;

import java.util.List;

@Data
public class UserOpenSaveRequest {

    private String phonenumber;

    private String nickName;

    private List<Long> deptIdList;
}
