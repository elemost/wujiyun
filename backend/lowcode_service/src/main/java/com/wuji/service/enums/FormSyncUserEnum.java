package com.wuji.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FormSyncUserEnum {
    PHONE("phone", "手机号码"),
    NICK_NAME("nickName", "昵称"),
    DEPT("dept", "归属部门"),
    EMAIL("email","邮箱"),
    SEX("sex","性别"),
    ID_CARD("idCard", "身份证号码"),
    ED("ed", "学历"),
    GRADUATE_SCHOOL("graduateSchool", "毕业院校"),
    PROFESSION("profession", "专业"),
    GRADUATION_TIME("graduationTime","毕业时间"),
    HOME_ADDRESS("homeAddress", "家庭地址"),
    POST("post", "职称"),
    RANK("rank", "职级"),
    REGISTER("register", "户口"),
    ENTRY_TIME("entryTime", "入职时间")
    ;

    private final String key;

    private final String msg;
}
