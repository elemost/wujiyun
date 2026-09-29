package com.wuji.admin.converter;

import com.wuji.admin.model.entity.OrdersEntity;
import com.wuji.admin.model.vo.OrdersVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract  class AbstractOrdersConverter {
    public static final AbstractOrdersConverter INSTANCE = Mappers.getMapper(AbstractOrdersConverter.class);

    public abstract OrdersVO toVO(OrdersEntity ordersEntity);
}
