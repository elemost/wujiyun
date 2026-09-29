package com.wuji.admin.client.lark.model;

import lombok.Data;

@Data
public class LarkDataVO<T> {
    private Boolean has_more;

    private String page_token;

    private T items;
}
