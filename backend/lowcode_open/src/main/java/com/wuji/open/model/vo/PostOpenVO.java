package com.wuji.open.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PostOpenVO {
    @ApiModelProperty("岗位ID")
    private Long postId;

    @ApiModelProperty("岗位名称")
    private String postName;
}
