package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItemVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("企业id")
    private Long companyId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("商品id")
    private Long itemId;

    @ApiModelProperty("商品编号")
    private String itemCode;

    @ApiModelProperty("商品名称")
    private String itemName;

    @ApiModelProperty("商品配置")
    private String itemConfig;

    @ApiModelProperty("购买数量")
    private Integer itemNum;

    @ApiModelProperty("商品单价")
    private BigDecimal itemPrice;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}
