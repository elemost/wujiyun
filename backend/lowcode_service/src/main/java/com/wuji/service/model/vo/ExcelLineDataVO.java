package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelLineDataVO {
    private List<ExcelDataVO> excelDataList;

    private Integer rowNum;
}
