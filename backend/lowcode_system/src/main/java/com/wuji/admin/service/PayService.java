package com.wuji.admin.service;

import com.wuji.admin.model.request.OrderPayRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PayService {
    Map<String,Object> orderSingleItem(OrderPayRequest orderPayRequest, HttpServletRequest  request);
}
