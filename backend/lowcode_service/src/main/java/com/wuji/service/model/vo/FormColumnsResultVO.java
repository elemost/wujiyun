package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormColumn;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormColumnsResultVO {
    private List<FormColumn> columnList;

    private Map<String, List<Integer>> metricsMap;

    private Integer maxNumber;
}
