package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbLinkVO {
    @ApiModelProperty("具体参数")
    private JSONObject instValue;

    private List<JSONObject> instValueList;
}
