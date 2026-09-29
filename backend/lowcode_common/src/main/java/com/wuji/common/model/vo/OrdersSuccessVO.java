package com.wuji.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrdersSuccessVO {

    @ApiModelProperty("天数")
    private Integer orderPeriod;

    @ApiModelProperty("公司名")
    private String companyName;

    @ApiModelProperty("产品版本")
    private String itemName;

    @ApiModelProperty("价格")
    private String price;

    @ApiModelProperty("人数")
    private String itemNum;

    @ApiModelProperty("下单时间")
    private String createTime;

    private String version;

    private String productName;

    private String source;

    private String statusName;
}
