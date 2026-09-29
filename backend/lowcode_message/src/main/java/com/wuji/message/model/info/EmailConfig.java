package com.wuji.message.model.info;

import lombok.Data;

@Data
public class EmailConfig {
    private String userName;

    private String password;

    private String mailHost;

    private String mailPort;
}
