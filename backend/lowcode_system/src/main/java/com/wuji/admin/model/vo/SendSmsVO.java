package com.wuji.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendSmsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private Long createTime;


}
