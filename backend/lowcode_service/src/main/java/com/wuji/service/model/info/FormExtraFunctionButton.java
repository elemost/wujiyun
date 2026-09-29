package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionButton {
    private String id;

    private String name;

    private List<String> showLocation;

    private MongodbSearchFilter filter;

    @ApiModelProperty("info create update")
    private String action;

    private JSONObject actionConfig;

    @ApiModelProperty("action 为 create update的时候 business为表单id")
    private String businessId;

    private String applicationId;

    private String deactivationDisplayMode;

    private String deactivationDisplayTip;

    private Boolean conforms;

    private String url;

    // NORMAL QRCODE
    private String triggeredType;

    private String target;

    private String detailId;

    private Boolean onlyShowConditionField;

    private String assistantId;

    private List<String> actionAfterEvent;

}
