package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.OrderItemEntity;
import com.wuji.admin.model.request.OrdersCreateRequest;
import com.wuji.admin.model.vo.ItemVO;
import com.wuji.admin.model.vo.OrderItemVO;

import java.util.List;

/**
 * <p>
 * 订单商品 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
public interface OrderItemService extends IService<OrderItemEntity> {
    void saveOrderItem(ItemVO item, OrdersCreateRequest ordersCreateRequest, Long orderId);

    List<OrderItemVO> getOrderItemList(List<Long> orderIds);

    List<OrderItemVO> selectByItemAndCompany(Long itemId);
}
