package com.wuji.service.service;

import com.wuji.service.model.request.ExcelImportDataRequest;
import com.wuji.service.model.request.ExcelMatchHeaderRequest;
import com.wuji.service.model.request.FormMongoDbExportRequest;
import com.wuji.service.model.vo.ExcelAnalysisResultVO;
import com.wuji.service.model.vo.ExcelImportDataVO;
import com.wuji.service.model.vo.ExcelImportVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FormMongoDbExcelService {
    ExcelImportDataVO importExcel(ExcelImportDataRequest excelImportDataRequest);

    void exportExcel(HttpServletResponse response, FormMongoDbExportRequest formMongoDbExportRequest);

    ExcelAnalysisResultVO analysisExcel(MultipartFile multipartFile, Integer sheet);

    List<ExcelImportVO> matchHeader(ExcelMatchHeaderRequest excelMatchHeaderRequest);

    ExcelImportDataVO importData(ExcelImportDataRequest excelImportDataRequest);
}
