package com.wuji.admin.model.request;

import com.wuji.common.model.request.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PostSelectRequest extends BasePageRequest {
    @ApiModelProperty("岗位名称")
    private String postName;

    private List<Long> postIdList;
}
