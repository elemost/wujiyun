package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelAnalysisResultVO {
    private List<ExcelLineDataVO> excelLineDataList;

    private Boolean existSubForm;
}
