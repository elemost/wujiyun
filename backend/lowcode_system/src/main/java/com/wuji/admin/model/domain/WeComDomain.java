package com.wuji.admin.model.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class WeComDomain {
    @ExcelProperty(value = "账号", index = 0)
    private String thirdId;

    @ExcelProperty(value = "手机", index = 0)
    private String phone;
}
