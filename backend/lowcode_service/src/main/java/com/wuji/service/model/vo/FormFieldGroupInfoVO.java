package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormConfigCommon;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FormFieldGroupInfoVO {
    private List<FormFieldGroupInfoVO> subGroups;

    private Map<String, BigDecimal> summaries = new HashMap<>();

    private Integer total;

    private String value;

    private String fieldId;

    private FormConfigCommon formConfigCommon;
}
