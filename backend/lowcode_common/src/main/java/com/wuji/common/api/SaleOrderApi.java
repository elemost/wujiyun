package com.wuji.common.api;

import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.OrdersSuccessVO;

public interface SaleOrderApi {
    void generateTemplate(String templateApplicationId, Integer day, UserDomain userDomain);

    void sendWeCom(OrdersSuccessVO ordersSuccessVO);
}
