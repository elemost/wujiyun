package com.wuji.admin.model.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CorpCoopDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("姓名")
    @ExcelProperty(value = "姓名", index = 0)
    private String nickName;

    @ExcelProperty(value = "公司名称", index = 1)
    private String companyName;

    @ApiModelProperty("手机号码")
    @ExcelProperty(value = "手机号码", index = 2)
    private String phonenumber;

    @ApiModelProperty("用户邮箱")
    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    @ApiModelProperty("用户邮箱")
    @ExcelProperty(value = "是否为超级管理员", index = 4)
    private String adminString;

    private Integer row;

    private Boolean admin = Boolean.FALSE;

    private String uuid;

}
