package com.wuji.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分页
 *
 * @author hzm
 */
@Data
public class BasePageRequest {

    @NotNull(message = "page参数不能为空")
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @NotNull(message = "pageSize参数不能为空")
    @ApiModelProperty("返回条数")
    private Integer pageSize = 100;


    public Integer getOffSet() {
        return this.getPageSize() * (this.getPageNum() - 1);
    }
}
