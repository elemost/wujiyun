package com.wuji.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RosterEnum {

    RANK("rank", "职级"),
    SEX("sex", "性别"),
    BIRTHDAY("birthday", "出生年月"),
    ID_CARD("idCard", "身份证号码"),
    ED("ed", "学历"),
    GRADUATE_SCHOOL("graduateSchool", "毕业院校"),
    PROFESSION("profession", "专业"),
    GRADUATION_TIME("graduationTime","毕业时间"),
    HOME_ADDRESS("homeAddress", "家庭地址"),
    POST("post", "职称"),
    REGISTER("register", "户口"),
    ENTRY_TIME("entryTime", "入职时间")

    ;

    private final String key;

    private final String name;
}
