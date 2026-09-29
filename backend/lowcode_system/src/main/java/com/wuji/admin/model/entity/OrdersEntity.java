package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 支付订单表
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@Getter
@Setter
@TableName("wj_orders")
@ApiModel(value = "OrdersEntity对象", description = "支付订单表")
public class OrdersEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(" 订单编号")
    private String orderNo;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("企业id")
    private Long companyId;

    @ApiModelProperty("大屏uuid")
    private String screenUuid;

    @ApiModelProperty("大屏名称")
    private String screenName;

    @ApiModelProperty("大屏封面图")
    private String indexImage;

    @ApiModelProperty("下载地址")
    private String downloadUrl;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("交易号")
    private String transactionId;

    @ApiModelProperty("订单状态,0:未支付,1:支付中,2:已支付,-1:取消订单")
    private Short status;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    private Integer orderPeriod;

    private Boolean sync;
}
