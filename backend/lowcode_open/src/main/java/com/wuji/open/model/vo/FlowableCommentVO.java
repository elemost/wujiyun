package com.wuji.open.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlowableCommentVO {
    @ApiModelProperty("操作类型")
    private String operate;

    private String operateName;

    @ApiModelProperty("创建人")
    private String creator;

    private String creatorName;

    private String duration;

    @ApiModelProperty("评论")
    private String comment;
}
