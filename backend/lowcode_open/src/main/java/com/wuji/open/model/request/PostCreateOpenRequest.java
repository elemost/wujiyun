package com.wuji.open.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PostCreateOpenRequest {
    private String postName;

    @ApiModelProperty("显示顺序")
    private Integer postSort = 0;
}
