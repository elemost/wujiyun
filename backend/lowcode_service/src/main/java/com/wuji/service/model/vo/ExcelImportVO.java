package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormConfigCommon;
import lombok.Data;

import java.util.List;

@Data
public class ExcelImportVO {
    private String key;

    private Integer col;

    private Integer maxCol;

    private String type;

    private Object value;

    private FormConfigCommon formConfigCommon;

    private List<ExcelImportVO> excelImportList;
}
