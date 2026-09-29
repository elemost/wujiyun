package com.wuji.admin.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserImportVO {

    private List<UserImportErrorVO> errorList;

}
