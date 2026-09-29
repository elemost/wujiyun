package com.wuji.admin.model.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserExcelDomain {

    @ExcelProperty(value = "姓名", index = 0)
    private String nickName;

    @ExcelProperty(value = "手机号码", index = 1)
    private String phonenumber;

    @ExcelProperty(value = "部门", index = 2)
    private String deptName;

    @ExcelProperty(value = "职位", index = 3)
    private String position;

    @ExcelProperty(value = "邮箱", index = 4)
    private String email;

    private String uuid;

    @ExcelProperty(value = "职级", index = 5)
    private String rank;

    @ExcelProperty(value = "入职时间", index = 6)
    private Date entryTimeDate;

    private Long entryTime;

    @ExcelProperty(value = "性别", index = 7)
    private String sex;

    // @ExcelProperty(value = "出生年月", index = 9)
    // private String birthday;

    @ExcelProperty(value = "身份证号码", index = 8)
    private String idCard;

    @ExcelProperty(value = "学历", index = 9)
    private String ed;

    @ExcelProperty(value = "毕业院校", index = 10)
    private String graduateSchool;

    @ExcelProperty(value = "专业", index = 11)
    private String profession;


    // @ExcelProperty(value = "毕业时间", index = 14)
    // private String graduationTime;

    @ExcelProperty(value = "家庭地址", index = 12)
    private String homeAddress;


    @ExcelProperty(value = "职称", index = 13)
    private String post;


    @ExcelProperty(value = "户口", index = 14)
    private String register;


    @ExcelProperty(value = "备注", index = 15)
    private String remark;
}
