package com.wuji.admin.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CityVO {

    private Integer pid;

    @ApiModelProperty("编号")
    private Integer code;

    private String cityName;

    private String label;

    private String value;

    private List<CityVO> children;
}
