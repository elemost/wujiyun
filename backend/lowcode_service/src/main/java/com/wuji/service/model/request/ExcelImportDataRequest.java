package com.wuji.service.model.request;

import com.wuji.service.model.vo.ExcelImportVO;
import com.wuji.service.model.vo.ExcelLineDataVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExcelImportDataRequest {
    private Boolean existSubForm;

    private Integer headerLine;

    private List<ExcelImportVO> excelImportList;

    private List<ExcelLineDataVO> excelLineDataList;

    private String formId;

    private String applicationId;

    @ApiModelProperty("1仅新增 2仅修改 3更新和修改数据")
    private String importMode;

    private String fileId;
}
