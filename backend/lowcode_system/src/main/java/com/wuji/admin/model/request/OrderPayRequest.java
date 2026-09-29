package com.wuji.admin.model.request;

import lombok.Data;

@Data
public class OrderPayRequest {
    private String itemCode;

    private int num;
}
