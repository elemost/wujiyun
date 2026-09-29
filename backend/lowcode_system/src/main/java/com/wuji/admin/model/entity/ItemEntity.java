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
 * 五极产品
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@Getter
@Setter
@TableName("wj_item")
@ApiModel(value = "ItemEntity对象", description = "五极产品")
public class ItemEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("商品编号")
    private String itemCode;

    @ApiModelProperty("商品名称")
    private String itemName;

    @ApiModelProperty("商品配置")
    private String itemConfig;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("最低价，购买最低总价")
    private BigDecimal thresholdAmount;

    @ApiModelProperty("商品状态")
    private Short status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    private String productName;

    private String version;

    private String source;
}
