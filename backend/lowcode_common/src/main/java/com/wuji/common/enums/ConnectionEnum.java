package com.wuji.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConnectionEnum {
    POSTGRESQL("jdbc:postgresql://%s:%s/%s%s", "org.postgresql.Driver"),
    MYSQL("jdbc:mysql://%s:%s/%s%s", "com.mysql.cj.jdbc.Driver"),
    DM("jdbc:dm://%s:%s/%s%s", "dm.jdbc.driver.DmDriver"),
    SQLSERVER("jdbc:sqlserver://%s:%s;databaseName=%s%s", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    ORACLESID("jdbc:oracle:thin:@%s:%s:%s%s ", "oracle.jdbc.driver.OracleDriver"),
    ORACLESERVICE("jdbc:oracle:thin:@%s:%s/%s%s", "oracle.jdbc.driver.OracleDriver"),
    API("","");

    private String url;

    private String dbDriver;
}
