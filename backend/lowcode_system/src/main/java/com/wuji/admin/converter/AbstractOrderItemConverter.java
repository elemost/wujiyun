package com.wuji.admin.converter;

import com.wuji.admin.model.entity.OrderItemEntity;
import com.wuji.admin.model.vo.OrderItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractOrderItemConverter {

    public static final AbstractOrderItemConverter INSTANCE = Mappers.getMapper(AbstractOrderItemConverter.class);

    public abstract OrderItemVO toVO(OrderItemEntity orderItemEntity);
}
