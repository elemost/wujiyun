package com.wuji.service.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.components.ApplicationCorpCoopComponent;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.request.ExcelImportDataRequest;
import com.wuji.service.model.request.ExcelMatchHeaderRequest;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormMongoDbBatchRequest;
import com.wuji.service.model.request.FormMongoDbDeleteRequest;
import com.wuji.service.model.request.FormMongoDbExportRequest;
import com.wuji.service.model.request.FormMongoDbLinkRequest;
import com.wuji.service.model.request.FormMongoDbSummaryRequest;
import com.wuji.service.model.request.FormMongodbLinkSelectRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.vo.ExcelAnalysisResultVO;
import com.wuji.service.model.vo.ExcelImportDataVO;
import com.wuji.service.model.vo.ExcelImportVO;
import com.wuji.service.model.vo.FormMongoDbLinkVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormMongoDbExcelService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormMongoDbViewService;
import com.wuji.service.service.FormRuleExecuteService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/form/mongodb")
public class FormMongoDbController {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormMongoDbExcelService formMongoDbExcelService;

    @Autowired
    private FormMongoDbViewService formMongoDbViewService;

    @Autowired
    private ApplicationCorpCoopComponent applicationCorpCoopComponent;

    @Autowired
    private FormRuleExecuteService formRuleExecuteService;

    @ApiOperation("插入数据")
    @PostMapping("/insert")
    public Response<String> insertData(@RequestBody FormInsertDataRequest formInsertDataRequest) {
        if (StringUtils.isEmpty(formInsertDataRequest.getStatus())) {
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        }
        formInsertDataRequest.setDataStreamTrigger(Boolean.TRUE);
        formRuleExecuteService.checkVerifySubmit(formInsertDataRequest.getApplicationId(),
                formInsertDataRequest.getFormId(), formInsertDataRequest.getUuid(),
                formInsertDataRequest.getInstValue());
        return Response.success(formMongoDbService.insertData(formInsertDataRequest));
    }


    @ApiOperation("修改数据")
    @PutMapping("/update")
    public void updateData(@RequestBody FormUpdateDataRequest formUpdateDataRequest) {
        applicationCorpCoopComponent.exchangeCompany(formUpdateDataRequest.getApplicationId());
        formUpdateDataRequest.setDataStreamTrigger(Boolean.TRUE);
        if (!"AUDIT".equals(formUpdateDataRequest.getSource())) {
            formRuleExecuteService.checkVerifySubmit(formUpdateDataRequest.getApplicationId(),
                    formUpdateDataRequest.getFormId(), formUpdateDataRequest.getUuid(),
                    formUpdateDataRequest.getInstValue());
        }
        formMongoDbService.updateData(formUpdateDataRequest, Boolean.FALSE);
    }

    @ApiOperation("删除数据")
    @DeleteMapping("/delete/{uuid}")
    public void deleteData(@PathVariable String uuid, @RequestBody FormUpdateDataRequest formUpdateDataRequest) {
        formMongoDbService.deleteData(uuid, formUpdateDataRequest.getFormId(), formUpdateDataRequest.getApplicationId(),
                new ArrayList<>());
    }

    @ApiOperation("删除数据")
    @DeleteMapping("/batchDelete")
    public void batchDelete(@RequestBody FormMongoDbDeleteRequest formMongoDbDeleteRequest) {
        formMongoDbService.batchDelete(formMongoDbDeleteRequest);
    }


    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public QueryPageVO<LowcodeDataVO> queryList(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        return formMongoDbService.queryList(formSearchDataRequest);
    }

    @ApiOperation("视图查询数据")
    @PostMapping("/queryListView")
    public QueryPageVO<LowcodeDataVO> queryListView(@RequestBody FormViewMongoDbRequest formViewMongoDbRequest) {
        return formMongoDbViewService.queryListView(formViewMongoDbRequest);
    }


    @ApiOperation("查询数据")
    @PostMapping("/infoAll")
    public LowcodeDataVO infoAll(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        return formMongoDbService.infoAll(formSearchDataRequest);
    }


    @ApiOperation("数据详情")
    @GetMapping("/info/{uuid}")
    public LowcodeDataDomain info(@RequestParam("formId") String formId, @PathVariable String uuid,
                                  @RequestParam("applicationId") String applicationId) {
        return formMongoDbService.info(uuid, formId, applicationId);
    }

    @ApiOperation("数据联动")
    @PostMapping("/link")
    public FormMongoDbLinkVO link(@RequestBody FormMongoDbLinkRequest formMongoDbLinkRequest) {
        applicationCorpCoopComponent.exchangeCompany(formMongoDbLinkRequest.getApplicationId());
        return formMongoDbService.link(formMongoDbLinkRequest);
    }

    @ApiOperation("数据联动")
    @PostMapping("/link/list")
    public FormMongoDbLinkVO linkList(@RequestBody FormMongoDbLinkRequest formMongoDbLinkRequest) {
        applicationCorpCoopComponent.exchangeCompany(formMongoDbLinkRequest.getApplicationId());
        return formMongoDbService.linkList(formMongoDbLinkRequest);
    }


    @ApiOperation("数据联动")
    @PostMapping("/link/select")
    public List<Object> linkSelect(@RequestBody FormMongodbLinkSelectRequest formMongodbLinkSelectRequest) {
        applicationCorpCoopComponent.exchangeCompany(formMongodbLinkSelectRequest.getApplicationId());
        return formMongoDbService.linkSelect(formMongodbLinkSelectRequest);
    }

    @ApiOperation("查询数据")
    @PostMapping("/queryListLink")
    public QueryPageVO<LowcodeDataVO> queryListLink(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        applicationCorpCoopComponent.exchangeCompany(formSearchDataRequest.getApplicationId());
        return formMongoDbService.queryListLink(formSearchDataRequest);
    }

    @ApiOperation("数据联动")
    @PostMapping("/select")
    public List<Object> select(@RequestBody FormMongodbLinkSelectRequest formMongodbLinkSelectRequest) {
        applicationCorpCoopComponent.exchangeCompany(formMongodbLinkSelectRequest.getApplicationId());
        return formMongoDbService.linkSelect(formMongodbLinkSelectRequest);
    }

    @ApiOperation("导出数据")
    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response,
                            @RequestBody FormMongoDbExportRequest formMongoDbExportRequest) {
        formMongoDbExcelService.exportExcel(response, formMongoDbExportRequest);
    }

    @ApiOperation("导入数据")
    @PostMapping("/importExcel")
    public ExcelImportDataVO importExcel(@RequestBody ExcelImportDataRequest excelImportDataRequest) {
        return formMongoDbExcelService.importExcel(excelImportDataRequest);
    }

    @ApiOperation("解析excel")
    @PostMapping("/analysisExcel")
    public ExcelAnalysisResultVO analysisExcel(MultipartFile multipartFile, Integer sheet) {
        return formMongoDbExcelService.analysisExcel(multipartFile, sheet);
    }

    @ApiOperation("匹配header")
    @PostMapping("/matchHeader")
    public List<ExcelImportVO> matchHeader(@RequestBody ExcelMatchHeaderRequest excelMatchHeaderRequest) {
        return formMongoDbExcelService.matchHeader(excelMatchHeaderRequest);
    }

    @ApiOperation("导入数据")
    @PostMapping("/importData")
    public ExcelImportDataVO importData(@RequestBody ExcelImportDataRequest excelImportDataRequest) {
        return formMongoDbExcelService.importData(excelImportDataRequest);
    }

    @ApiOperation("汇总")
    @PostMapping("/summary")
    public JSONObject summary(@RequestBody FormMongoDbSummaryRequest formMongoDbSummaryRequest) {
        return formMongoDbService.summary(formMongoDbSummaryRequest);
    }

    @ApiOperation("批量修改")
    @PostMapping("/updateBatch")
    public Response<String> updateBatch(@RequestBody FormMongoDbBatchRequest formMongoDbBatchRequest) {
        return Response.success(formMongoDbService.updateBatch(formMongoDbBatchRequest));
    }
}
