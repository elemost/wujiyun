package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelImportDataVO {
    private Integer insertCount;

    private Integer updateCount;

    private List<ExcelImportDataErrorVO> errorList;

    private Integer totalCount;

    private String taskId;
}
