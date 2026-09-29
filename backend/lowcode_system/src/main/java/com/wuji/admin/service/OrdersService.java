package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.OrdersEntity;
import com.wuji.admin.model.request.OrderPayRequest;
import com.wuji.admin.model.request.OrdersCreateRequest;
import com.wuji.admin.model.vo.OrdersVO;

/**
 * <p>
 * 支付订单表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
public interface OrdersService extends IService<OrdersEntity> {

    Long generateOrder(OrdersCreateRequest ordersCreateRequest);

    void sendPayMessage();

    OrdersVO  createSingleItemOrder(OrderPayRequest orderPayRequest);

    void payNotify(String orderNo,String transactionId);

}
