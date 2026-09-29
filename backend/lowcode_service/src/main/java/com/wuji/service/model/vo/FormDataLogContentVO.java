package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormDataLogContentVO {
    @ApiModelProperty("表单控件id")
    private String label;
    @ApiModelProperty("表单控件名")
    private String name;

    private String type;
    @ApiModelProperty("前值")
    private Object preValue;
    @ApiModelProperty("当前值")
    private Object curValue;

    private List<FormDataLogContentSubVO> children;
}
