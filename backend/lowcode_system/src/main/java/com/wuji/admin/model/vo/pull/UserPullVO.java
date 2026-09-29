package com.wuji.admin.model.vo.pull;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserPullVO {
    private String companyName;

    private String unionid;

    private List<String> deptIdList;

    private String thirdId;

    private String thirdType;

    @ExcelProperty(value = "员工ID", index = 0)
    private String dingThirdId;

    @ExcelProperty(value = "姓名", index = 1)
    private String nickName;

    @ExcelProperty(value = "手机号码", index = 2)
    private String phonenumber;

    @ExcelProperty(value = "职位", index = 4)
    private String position;

    @ExcelProperty(value = "邮箱", index = 5)
    private String email;

    private String uuid;

    private String larkUnionId;

    private String larkUserId;

    private String larkOpenId;

    private String weComUserId;

    private String userName;

    private Long sourceCompanyId;

    private Boolean admin;
}
