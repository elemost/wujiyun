package com.wuji.common.model.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormRole implements Serializable {

    private static final long serialVersionUID = 3670079982654483072L;

    private String roleName;

    private Long roleId;

}
