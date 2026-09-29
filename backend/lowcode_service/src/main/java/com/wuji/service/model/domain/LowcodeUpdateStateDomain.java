package com.wuji.service.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.domain.MongoDbUpdateBaseDomain;
import com.wuji.common.model.info.FormUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LowcodeUpdateStateDomain extends MongoDbUpdateBaseDomain {
    private String status;

    private String processInstanceId;

    private JSONObject instValue;

    private String processStatus;

    @ApiModelProperty("修改时间")
    private Long modifyTime;

    @ApiModelProperty("修改人")
    private FormUser modifier;
}
