package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.FormExtraFunctionTitle;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormExtraFunctionVO {

    private String applicationId;

    private String id;

    private String config;

    private JSONObject configJson;

    private String formId;

    private String functionType;

    // 自定义按钮
    private FormExtraFunctionButton formExtraFunctionButton;

    private FormExtraFunctionTitle formExtraFunctionTitle;

    private List<FormExtraFunctionRelationVO> formExtraFunctionRelationList;

    private List<FormExtraFunctionSync> syncList;

    private JSONObject info;

    private Integer sort;
}
