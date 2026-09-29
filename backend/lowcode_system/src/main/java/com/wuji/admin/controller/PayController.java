package com.wuji.admin.controller;

import com.wuji.admin.model.request.OrderPayRequest;
import com.wuji.admin.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/order")
    public Map<String,Object> orderSingleItem(OrderPayRequest orderPayRequest, HttpServletRequest request) {
        return payService.orderSingleItem(orderPayRequest, request);
    }

}
