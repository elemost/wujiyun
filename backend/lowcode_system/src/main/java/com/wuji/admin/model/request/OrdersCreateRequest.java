package com.wuji.admin.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersCreateRequest {
    private String orderId;

    private Integer userCount;

    private String creator;

    private String itemCode;

    private BigDecimal totalPrice;

    private Long userId;

    private BigDecimal itemPrice;

    private Integer orderPeriod;
}
