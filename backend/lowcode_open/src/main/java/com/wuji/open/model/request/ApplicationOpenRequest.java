package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationOpenRequest extends FormOpenCommonRequest {

    @NotNull(message = "page参数不能为空")
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @NotNull(message = "pageSize参数不能为空")
    @ApiModelProperty("返回条数")
    private Integer pageSize = 100;

}
