package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractOrderItemConverter;
import com.wuji.admin.mapper.OrderItemMapper;
import com.wuji.admin.model.entity.OrderItemEntity;
import com.wuji.admin.model.request.OrdersCreateRequest;
import com.wuji.admin.model.vo.ItemVO;
import com.wuji.admin.model.vo.OrderItemVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.OrderItemService;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单商品 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@Service
@DS("slave")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItemEntity> implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CompanyAppService companyAppService;

    @Override
    public void saveOrderItem(ItemVO item, OrdersCreateRequest ordersCreateRequest, Long orderId) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setCompanyId(UserUtils.getUser().getCompanyId());
        orderItem.setItemId(item.getId());
        orderItem.setItemName(item.getItemName());
        orderItem.setItemCode(item.getItemCode());
        orderItem.setItemConfig(item.getItemConfig());
        orderItem.setItemPrice(item.getPrice());
        orderItem.setItemNum(ordersCreateRequest.getUserCount());
        orderItem.setAmount(ordersCreateRequest.getTotalPrice());
        orderItem.setCreateBy(ordersCreateRequest.getCreator());
        orderItem.setUpdateBy(ordersCreateRequest.getCreator());
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        orderItem.setOrderId(orderId);
        orderItem.setUserId(ordersCreateRequest.getUserId());
        orderItemMapper.insert(orderItem);
        companyAppService.updateVersion(item.getItemConfig(),ordersCreateRequest.getOrderPeriod());
    }

    @Override
    public List<OrderItemVO> getOrderItemList(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrderItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderItemEntity::getOrderId,orderIds);
        List<OrderItemEntity> orderItemList = this.list(queryWrapper);
        return orderItemList.stream().map(AbstractOrderItemConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderItemVO> selectByItemAndCompany(Long itemId) {
        LambdaQueryWrapper<OrderItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItemEntity::getItemId,itemId);
        queryWrapper.eq(OrderItemEntity::getCompanyId,UserUtils.getUser().getCompanyId());
        List<OrderItemEntity> orderItemList = this.list(queryWrapper);
        return orderItemList.stream().map(AbstractOrderItemConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
