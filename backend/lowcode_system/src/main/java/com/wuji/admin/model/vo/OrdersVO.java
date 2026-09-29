package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrdersVO {
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
