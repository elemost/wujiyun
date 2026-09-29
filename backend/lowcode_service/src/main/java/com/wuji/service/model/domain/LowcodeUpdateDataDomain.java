package com.wuji.service.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.domain.MongoDbUpdateBaseDomain;
import com.wuji.common.model.info.FormUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LowcodeUpdateDataDomain extends MongoDbUpdateBaseDomain {

    @ApiModelProperty("具体参数")
    private JSONObject instValue;

    @ApiModelProperty("应用类型")
    private Integer version;

    @ApiModelProperty("修改时间")
    private Long modifyTime;

    @ApiModelProperty("修改人")
    private FormUser modifier;

    private String formId;

    private String status;

    private String modelId;
}
