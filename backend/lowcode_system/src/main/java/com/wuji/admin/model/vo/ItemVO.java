package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ItemVO {
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
}
