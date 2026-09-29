package com.wuji.service.model.request;

import com.wuji.service.model.vo.ExcelLineDataVO;
import lombok.Data;

import java.util.List;

@Data
public class ExcelMatchHeaderRequest {
    private String formId;

    private String applicationId;

    private List<ExcelLineDataVO> excelLineDataList;

    private Integer headerLine;
}
