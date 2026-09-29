package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractOrdersConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.OrdersMapper;
import com.wuji.admin.model.domain.OrderItemConfigDomain;
import com.wuji.admin.model.entity.CompanyAppEntity;
import com.wuji.admin.model.entity.ItemEntity;
import com.wuji.admin.model.entity.OrderItemEntity;
import com.wuji.admin.model.entity.OrdersEntity;
import com.wuji.admin.model.request.OrderPayRequest;
import com.wuji.admin.model.request.OrdersCreateRequest;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.ItemVO;
import com.wuji.admin.model.vo.OrderItemVO;
import com.wuji.admin.model.vo.OrdersVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.ItemService;
import com.wuji.admin.service.OrderItemService;
import com.wuji.admin.service.OrdersService;
import com.wuji.common.api.SaleOrderApi;
import com.wuji.common.enums.DictDataTypeEnum;
import com.wuji.common.enums.OrderStatusEnum;
import com.wuji.common.model.vo.OrdersSuccessVO;
import com.wuji.common.utils.Seq;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 支付订单表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@Service
@DS("slave")
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, OrdersEntity> implements OrdersService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SaleOrderApi saleOrderApi;

    @Autowired
    private CompanyAppService companyAppService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateOrder(OrdersCreateRequest ordersCreateRequest) {
        ItemVO item = itemService.info(ordersCreateRequest.getItemCode());
        OrdersEntity orders = new OrdersEntity();
        orders.setOrderNo(ordersCreateRequest.getOrderId());
        orders.setUserId(ordersCreateRequest.getUserId());
        orders.setCompanyId(UserUtils.getUser().getCompanyId());
        orders.setScreenName(item.getItemName());
        orders.setScreenUuid(item.getItemCode());
        orders.setIndexImage("");
        orders.setDownloadUrl("");
        orders.setAmount(ordersCreateRequest.getTotalPrice());
        orders.setOriginalPrice(ordersCreateRequest.getTotalPrice());
        orders.setStatus((short) 2);
        orders.setCreateBy(ordersCreateRequest.getCreator());
        orders.setUpdateBy(ordersCreateRequest.getCreator());
        orders.setCreateTime(new Date());
        orders.setUpdateTime(new Date());
        orders.setOrderPeriod(ordersCreateRequest.getOrderPeriod());
        ordersCreateRequest.setItemPrice(item.getPrice());
        ordersMapper.insert(orders);
        orderItemService.saveOrderItem(item, ordersCreateRequest, orders.getId());
        return orders.getId();
    }

    @Override
    public void sendPayMessage() {
        LambdaQueryWrapper<OrdersEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrdersEntity::getSync, Boolean.FALSE);
        List<OrdersEntity> ordersEntities = ordersMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(ordersEntities)) {
            return;
        }
        List<Long> orderIds = ordersEntities.stream().map(OrdersEntity::getId).collect(Collectors.toList());
        List<OrderItemVO> orderItemList = orderItemService.getOrderItemList(orderIds);
        Map<Long, OrderItemVO> orderIdToMap =
                orderItemList.stream().collect(Collectors.toMap(OrderItemVO::getOrderId, c -> c, (v1, v2) -> v1));
        List<CompanyVO> companyVOS = companyService.getByIds(
                ordersEntities.stream().map(OrdersEntity::getCompanyId).collect(Collectors.toList()));
        Map<Long, String> companyIdToMap =
                companyVOS.stream().collect(Collectors.toMap(CompanyVO::getCompanyId, CompanyVO::getCompanyName));
        List<Long> itemCodes = orderItemList.stream().map(OrderItemVO::getItemId).collect(Collectors.toList());
        List<ItemEntity> itemEntities = itemService.listByIds(itemCodes);
        Map<Long, ItemEntity> itemEntityMap =
                itemEntities.stream().collect(Collectors.toMap(ItemEntity::getId, c -> c));
        for (OrdersEntity ordersEntity : ordersEntities) {
            ordersEntity.setSync(Boolean.TRUE);
            OrderItemVO item = orderIdToMap.get(ordersEntity.getId());
            ItemEntity itemEntity = itemEntityMap.get(item.getItemId());
            OrdersSuccessVO ordersSuccessVO = new OrdersSuccessVO();
            ordersSuccessVO.setCompanyName(companyIdToMap.get(ordersEntity.getCompanyId()));
            ordersSuccessVO.setItemName(item.getItemName());
            ordersSuccessVO.setPrice(ordersEntity.getAmount() + "元");
            ordersSuccessVO.setOrderPeriod(ordersEntity.getOrderPeriod());
            ordersSuccessVO.setCreateTime(TimeUtils.formatDateTime(ordersEntity.getCreateTime()));
            ordersSuccessVO.setItemNum(item.getItemNum() + "人");
            ordersSuccessVO.setSource(itemEntity.getSource());
            ordersSuccessVO.setProductName(itemEntity.getProductName());
            ordersSuccessVO.setVersion(itemEntity.getVersion());
            if (ordersEntity.getStatus() == 1) {
                ordersSuccessVO.setStatusName("未支付");
            } else if (ordersEntity.getStatus() == 2) {
                ordersSuccessVO.setStatusName("已支付");
            } else {
                ordersSuccessVO.setStatusName("未支付");
            }
            ordersMapper.updateById(ordersEntity);
            saleOrderApi.sendWeCom(ordersSuccessVO);
        }
    }

    @Override
    public OrdersVO createSingleItemOrder(OrderPayRequest orderPayRequest) {
        ItemVO item = itemService.info(orderPayRequest.getItemCode());
        // 免费商品限制购买
        if (item.getPrice().compareTo(new BigDecimal(0)) <= 0) {
            List<OrderItemVO> orderItemVOS = orderItemService.selectByItemAndCompany(item.getId());
            if (CollectionUtils.isNotEmpty(orderItemVOS)) {
                throw new AdminException(AdminResultCode.FREE_PRODUCT_NOT_BUY_AGAIN);
            }
        }
        OrdersEntity orders = buildOrder(orderPayRequest, item);
        List<OrderItemEntity> orderItems = buildOrderItemList(orders, orderPayRequest, item);
        transactionOrder(orders, orderItems);
        return AbstractOrdersConverter.INSTANCE.toVO(orders);
    }

    @Override
    public void payNotify(String orderNo, String transactionId) {
        LambdaQueryWrapper<OrdersEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrdersEntity::getOrderNo, orderNo);
        OrdersEntity orders = ordersMapper.selectOne(queryWrapper);
        log.info("payNotify orderNo:{}, transactionId:{}", orderNo, transactionId);
        if (Objects.isNull(orders)) {
            log.error("payNotify orderNo:{} do not exists", orderNo);
            return;
        }
        if (StringUtils.isNotEmpty(orders.getTransactionId())) {
            log.warn("payNotify orderNo:{} has already updated", orderNo);
            return;
        }
        OrdersEntity updateOrders = new OrdersEntity();
        updateOrders.setId(orders.getId());
        updateOrders.setTransactionId(transactionId);
        updateOrders.setStatus(OrderStatusEnum.PAYED.getStatus());
        updateOrders.setUpdateTime(new Date());
        ordersMapper.updateById(updateOrders);

        List<OrderItemVO> orderItems = orderItemService.getOrderItemList(Collections.singletonList(orders.getId()));
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(orderItems)) {
            log.error("payNotify orderNo:{} do not exists items", orderNo);
            return;
        }

        for (OrderItemVO orderItem : orderItems) {
            List<OrderItemConfigDomain> configVos =
                    JSON.parseArray(orderItem.getItemConfig(), OrderItemConfigDomain.class);
            for (OrderItemConfigDomain orderItemConfigVo : configVos) {
                CompanyAppVO wjCompanyApp = companyAppService.getCurrentInfo();
                if (Objects.isNull(wjCompanyApp)) {
                    CompanyAppEntity companyApp = new CompanyAppEntity();
                    companyApp.setOrgId(orders.getCompanyId());
                    companyApp.setClientId(orderItemConfigVo.getClientId());
                    companyApp.setStartTime(new Date());
                    companyApp.setEndTime(
                            TimeUtils.dateAddDayWithLastSecondAndUnit(new Date(), orderItemConfigVo.getDay(),
                                    orderItemConfigVo.getDayUnit()));
                    companyApp.setEquityId(orderItemConfigVo.getEquityId());
                    companyAppService.save(companyApp);
                } else {
                    // 续费 暂时五极云免费版不支持续费
                    CompanyAppEntity companyApp = new CompanyAppEntity();
                    if (companyApp.getEndTime().after(new Date())) {
                        companyApp.setEndTime(TimeUtils.dateAddDayWithLastSecondAndUnit(companyApp.getEndTime(),
                                orderItemConfigVo.getDay(), orderItemConfigVo.getDayUnit()));
                    } else {
                        companyApp.setEndTime(
                                TimeUtils.dateAddDayWithLastSecondAndUnit(new Date(), orderItemConfigVo.getDay(),
                                        orderItemConfigVo.getDayUnit()));
                    }
                    companyApp.setEquityId(orderItemConfigVo.getEquityId());
                    companyApp.setId(wjCompanyApp.getId());
                    companyAppService.updateById(companyApp);
                }
                if (DictDataTypeEnum.ELECLOUD.name().toLowerCase().equals(orderItemConfigVo.getClientId())) {
                    Map<String, Integer> buyMap = new HashMap<>();
                    buyMap.put("userLimit", orderItem.getItemNum());
                    companyInfoService.saveCompanyInfoWhileOrder(buyMap, orderItemConfigVo.getClientId(),
                            orderItemConfigVo.getEquityId(), orders.getCompanyId());
                }

            }

        }
    }

    private void transactionOrder(OrdersEntity orders, List<OrderItemEntity> orderItems) {
        ordersMapper.insert(orders);
        for (OrderItemEntity orderItem : orderItems) {
            orderItem.setOrderId(orders.getId());
        }
        orderItemService.saveBatch(orderItems);
    }

    private List<OrderItemEntity> buildOrderItemList(OrdersEntity orders, OrderPayRequest orderPayRequest,
                                                     ItemVO item) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setCompanyId(orders.getCompanyId());
        orderItem.setUserId(orders.getUserId());
        orderItem.setItemId(item.getId());
        orderItem.setItemName(item.getItemName());
        orderItem.setItemCode(item.getItemCode());
        orderItem.setItemConfig(item.getItemConfig());
        orderItem.setItemNum(orderPayRequest.getNum());
        orderItem.setItemPrice(item.getPrice());
        orderItem.setAmount(orders.getAmount());
        orderItem.setCreateBy(orders.getCreateBy());
        orderItem.setUpdateBy(orders.getUpdateBy());
        return Collections.singletonList(orderItem);
    }

    private OrdersEntity buildOrder(OrderPayRequest orderPayRequest, ItemVO item) {
        BigDecimal totalAmount = item.getPrice().multiply(new BigDecimal(orderPayRequest.getNum()));
        if (totalAmount.compareTo(item.getThresholdAmount()) <= 0) {
            totalAmount = item.getThresholdAmount();
        }
        OrdersEntity orders = new OrdersEntity();
        orders.setOrderNo(Seq.getId());
        orders.setUserId(UserUtils.getUser().getUserIdLongValue());
        orders.setCompanyId(UserUtils.getUser().getCompanyId());
        orders.setScreenName(item.getItemName());
        orders.setScreenUuid(item.getItemCode());
        orders.setIndexImage("");
        orders.setDownloadUrl("");
        orders.setAmount(totalAmount);
        orders.setOriginalPrice(totalAmount);
        orders.setStatus(OrderStatusEnum.PAYING.getStatus());
        orders.setCreateBy(UserUtils.getUser().getUserName());
        orders.setUpdateBy(UserUtils.getUser().getUserName());
        return orders;
    }
}
