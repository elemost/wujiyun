package com.wuji.admin.client.lark.model;

import lombok.Data;

@Data
public class LarkUserStatus {
    private Boolean is_activated;

    private Boolean is_exited;

    private Boolean is_frozen;

    private Boolean is_resigned;

    private Boolean is_unjoin;
}
