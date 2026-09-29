package com.wuji.service.model.info;

import com.wuji.service.enums.SerialNumberTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SerialNumberConfig {

    /**
     * @see SerialNumberTypeEnum
     */
    private String type;

    @ApiModelProperty("位数")
    private Integer digit;

    @ApiModelProperty("初始值")
    private Integer initialValue;

    @ApiModelProperty("是否固定位数")
    private Boolean fixDigit;

    @ApiModelProperty("循环周期")
    private String cycle;

    @ApiModelProperty("谷底字符")
    private String fixedCharacter;

    @ApiModelProperty("日期格式")
    private String format;

    @ApiModelProperty("日期格式类型")
    private String formatType;
}
