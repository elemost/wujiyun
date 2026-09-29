package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.TaskProgressHolder;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.FormExportDomain;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import com.wuji.service.model.info.ImportProgress;
import com.wuji.service.model.request.ExcelImportDataRequest;
import com.wuji.service.model.request.ExcelMatchHeaderRequest;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormMongoDbExportRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.vo.ExcelAnalysisResultVO;
import com.wuji.service.model.vo.ExcelDataVO;
import com.wuji.service.model.vo.ExcelImportDataErrorVO;
import com.wuji.service.model.vo.ExcelImportDataVO;
import com.wuji.service.model.vo.ExcelImportVO;
import com.wuji.service.model.vo.ExcelLineDataVO;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormMongoDbExcelService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormMongoDbViewService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.TaskService;
import com.wuji.service.utils.DataStreamExecuteUtils;
import com.wuji.service.utils.ExcelUtils;
import com.wuji.service.utils.FormPrivilegeUtils;
import com.wuji.service.utils.MongoExcelUtils;
import com.wuji.service.utils.POIUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormMongoDbExcelServiceImpl implements FormMongoDbExcelService {

    @Autowired
    private FormService formService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private FormMongoDbViewService formMongoDbViewService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public ExcelImportDataVO importExcel(ExcelImportDataRequest excelImportDataRequest) {
        // uploadFileService.downloadFile()
        analysisExcel(null, 0);
        return null;
    }

    private static void addError(ExcelImportVO excelImportVO, FormImportCheckResultVO result, int d,
                                 List<ExcelImportDataErrorVO> errorList) {
        if (StringUtils.isNotEmpty(result.getErrorMessage())) {
            ExcelImportDataErrorVO excelImportDataErrorVO = new ExcelImportDataErrorVO();
            excelImportDataErrorVO.setErrorMessage(result.getErrorMessage());
            excelImportDataErrorVO.setRow(d);
            excelImportDataErrorVO.setColumn(excelImportVO.getCol());
            errorList.add(excelImportDataErrorVO);
        }
    }


    @Override
    public void exportExcel(HttpServletResponse response, FormMongoDbExportRequest formMongoDbExportRequest) {
        FormVO formVO;
        FormVO viewForm =
                formService.info(formMongoDbExportRequest.getFormId(), formMongoDbExportRequest.getApplicationId());
        if (StringUtils.isNotEmpty(viewForm.getSourceId())) {
            formVO = formService.info(viewForm.getSourceId(), formMongoDbExportRequest.getApplicationId());
        } else {
            formVO = viewForm;
        }
        // 创建新的Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(formVO.getFormName());
            List<FormConfigCommon> configCommonList = formMongoDbExportRequest.getConfigCommonList();
            if (CollectionUtils.isEmpty(configCommonList)) {
                configCommonList =
                        formService.getAllFormConfigCommonList(formVO.getId(), formMongoDbExportRequest.getNeedData(),
                                formVO.getApplicationId(), Boolean.FALSE).getFields();
            }
            if (formMongoDbExportRequest.getNeedData() && formMongoDbExportRequest.getContainUuid()) {
                configCommonList.add(0, FormSystemFieldEnum.dataUuidCommon());
            }
            FormPrivilegeVO formPrivilegeVO = formPrivilegeService.detail(formMongoDbExportRequest.getGroupId());
            int i = 0;
            int j = 0;
            List<FormMongoDbExportDomain> formMongoDbExportDomainList = new ArrayList<>();
            List<FormConfigCommon> formConfigCommons =
                    configCommonList.stream().filter(c -> Constants.SUB_FORM_TYPE.equals(c.getType()))
                            .collect(Collectors.toList());
            boolean containSub = Boolean.FALSE;
            if (!formConfigCommons.isEmpty()) {
                containSub = true;
            }
            for (FormConfigCommon formConfigCommon : configCommonList) {
                FormMongoDbExportDomain formMongoDbExportDomain = new FormMongoDbExportDomain();
                formMongoDbExportDomain.setType(formConfigCommon.getType());
                formMongoDbExportDomain.setLabel(formConfigCommon.getLabel());
                formMongoDbExportDomain.setName(formConfigCommon.getName());
                formMongoDbExportDomain.setRow(0);
                formMongoDbExportDomain.setColumn(j);
                formMongoDbExportDomain.setMaxColumn(j);
                if (Constants.SUB_FORM_TYPE.equals(formConfigCommon.getType())) {
                    formMongoDbExportDomain.setMaxRow(0);
                    List<FormMongoDbExportDomain> subFormMongoDbExportDomainList = new ArrayList<>();
                    if (CollectionUtils.isEmpty(formConfigCommon.getColumns())) {
                        continue;
                    }
                    i = 1;
                    for (FormConfigCommon subFormConfigCommon : formConfigCommon.getColumns()) {
                        if (checkNotExport(subFormConfigCommon, formPrivilegeVO, viewForm)) {
                            continue;
                        }
                        FormMongoDbExportDomain subFormMongoDbExportDomain =
                                getFormMongoDbExportDomain(subFormConfigCommon, j, i);
                        subFormMongoDbExportDomainList.add(subFormMongoDbExportDomain);
                        j++;
                    }
                    formMongoDbExportDomain.setChildren(subFormMongoDbExportDomainList);
                    if (CollectionUtils.isEmpty(subFormMongoDbExportDomainList)) {
                        continue;
                    }
                    formMongoDbExportDomain.setMaxColumn(j - 1);
                    formMongoDbExportDomainList.add(formMongoDbExportDomain);
                } else {
                    if (checkNotExport(formConfigCommon, formPrivilegeVO, viewForm)) {
                        continue;
                    }
                    if (containSub) {
                        formMongoDbExportDomain.setMaxRow(1);
                    } else {
                        formMongoDbExportDomain.setMaxRow(0);
                    }
                    formMongoDbExportDomainList.add(formMongoDbExportDomain);
                    j++;
                }
            }
            int headerRow = i;
            MongoExcelUtils.drawHeader(headerRow, formMongoDbExportDomainList, sheet, j);
            if (formMongoDbExportRequest.getNeedData()) {
                QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO = null;
                if (StringUtils.isNotEmpty(viewForm.getSourceId())) {
                    FormViewMongoDbRequest formViewMongoDbRequest = new FormViewMongoDbRequest();
                    formViewMongoDbRequest.setFormId(formMongoDbExportRequest.getFormId());
                    formViewMongoDbRequest.setGroupId(formMongoDbExportRequest.getGroupId());
                    formViewMongoDbRequest.setApplicationId(formMongoDbExportRequest.getApplicationId());
                    formViewMongoDbRequest.setPageSize(10000);
                    formViewMongoDbRequest.setUuidList(formMongoDbExportRequest.getUuidList());
                    formViewMongoDbRequest.setFilter(formMongoDbExportRequest.getFilter());
                    if (StringUtils.isNotEmpty(formMongoDbExportRequest.getGroupId())) {
                        lowcodeDataVOQueryPageVO =
                                formMongoDbViewService.queryListViewPrivilege(formViewMongoDbRequest);
                    } else {
                        lowcodeDataVOQueryPageVO = formMongoDbViewService.queryListView(formViewMongoDbRequest);
                    }
                } else {
                    FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
                    formSearchDataRequest.setFormId(formMongoDbExportRequest.getFormId());
                    formSearchDataRequest.setGroupId(formMongoDbExportRequest.getGroupId());
                    formSearchDataRequest.setApplicationId(formMongoDbExportRequest.getApplicationId());
                    formSearchDataRequest.setPageSize(10000);
                    formSearchDataRequest.setUuidList(formMongoDbExportRequest.getUuidList());
                    formSearchDataRequest.setFilter(formMongoDbExportRequest.getFilter());
                    if (StringUtils.isNotEmpty(formMongoDbExportRequest.getGroupId())) {
                        lowcodeDataVOQueryPageVO = formMongoDbService.queryListPrivilege(formSearchDataRequest);
                    } else {
                        lowcodeDataVOQueryPageVO = formMongoDbService.queryListLink(formSearchDataRequest);
                    }
                }
                drawData(formMongoDbExportDomainList, lowcodeDataVOQueryPageVO, sheet, i, headerRow);
            }
            for (int t = 0; t < j; t++) {
                sheet.setColumnWidth(t, 20 * 256);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(formVO.getFormName() + ".xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception ex) {
            log.error("导出excel失败", ex);
        }
    }

    @Override
    public ExcelAnalysisResultVO analysisExcel(MultipartFile multipartFile, Integer sheetNumber) {
        if (sheetNumber == null) {
            sheetNumber = 0;
        }
        Workbook workBook = POIUtil.getWorkBook(multipartFile);
        Sheet sheet = workBook.getSheetAt(sheetNumber);
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        List<ExcelLineDataVO> excelLineDataVOList = new ArrayList<>();
        boolean existSubForm = MongoExcelUtils.getExcelLineData(sheet, mergedRegions, excelLineDataVOList);
        List<ExcelLineDataVO> returnList = new ArrayList<>();
        for (ExcelLineDataVO excelAnalysisResultVO : excelLineDataVOList) {
            List<ExcelDataVO> excelDataList = excelAnalysisResultVO.getExcelDataList();
            if (CollectionUtils.isNotEmpty(excelDataList)) {
                for (ExcelDataVO excelDataVO : excelDataList) {
                    if (excelDataVO.getValue() != null && StringUtils.isNotEmpty(excelDataVO.getValue().toString())) {
                        returnList.add(excelAnalysisResultVO);
                        break;
                    }
                }
            }
        }
        ExcelAnalysisResultVO excelAnalysisResultVO = new ExcelAnalysisResultVO();
        excelAnalysisResultVO.setExcelLineDataList(returnList);
        excelAnalysisResultVO.setExistSubForm(existSubForm);
        return excelAnalysisResultVO;
    }

    @Override
    public List<ExcelImportVO> matchHeader(ExcelMatchHeaderRequest excelMatchHeaderRequest) {
        FormVO formVO =
                formService.info(excelMatchHeaderRequest.getFormId(), excelMatchHeaderRequest.getApplicationId());
        List<FormConfigCommon> formConfigCommonList =
                formService.getAllFormConfigCommonList(formVO.getId(), Boolean.TRUE, formVO.getApplicationId(),
                        Boolean.FALSE).getFields();
        formConfigCommonList.add(0, FormSystemFieldEnum.dataUuidCommon());
        formConfigCommonList =
                formConfigCommonList.stream().filter(c -> !FormFieldTypeEnum.notImport().contains(c.getType()))
                        .collect(Collectors.toList());
        Map<String, List<FormConfigCommon>> labelMap =
                formConfigCommonList.stream().collect(Collectors.groupingBy(FormConfigCommon::getLabel));
        // 构建导入数据
        return MongoExcelUtils.buildHeader(excelMatchHeaderRequest.getExcelLineDataList(), labelMap,
                excelMatchHeaderRequest.getHeaderLine());
    }

    @Override
    public ExcelImportDataVO importData(ExcelImportDataRequest excelImportDataRequest) {
        List<ExcelImportDataErrorVO> errorList = new ArrayList<>();
        int d = excelImportDataRequest.getHeaderLine() + 1;
        if (excelImportDataRequest.getExistSubForm()) {
            d = excelImportDataRequest.getHeaderLine() + 2;
        }
        List<ExcelImportVO> excelImportList = excelImportDataRequest.getExcelImportList();
        List<ExcelImportVO> subFormTypeList =
                excelImportList.stream().filter(c -> Constants.SUB_FORM_TYPE.equals(c.getType()))
                        .collect(Collectors.toList());

        List<ExcelImportVO> otherList =
                excelImportList.stream().filter(c -> !Constants.SUB_FORM_TYPE.equals(c.getType()))
                        .collect(Collectors.toList());

        List<JSONObject> dataJsonList = new ArrayList<>();
        SystemAllDataNameVO importCheck = adminCommonService.getSystemAllDataName();
        List<ExcelLineDataVO> excelLineDataVOList = excelImportDataRequest.getExcelLineDataList();
        while (excelLineDataVOList.size() > d) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("row", d);
            ExcelLineDataVO excelLineDataVO = excelLineDataVOList.get(d);
            List<ExcelDataVO> excelDataList = excelLineDataVO.getExcelDataList();
            int subMaxRow = 1;
            for (ExcelImportVO excelImportVO : otherList) {
                if (excelImportVO.getFormConfigCommon() == null || StringUtils.isEmpty(excelImportVO.getKey())) {
                    continue;
                }
                if ("system".equals(excelImportVO.getFormConfigCommon().getType()) &&
                        !"uuid".equals(excelImportVO.getFormConfigCommon().getName())) {
                    continue;
                }
                if (!Constants.SUB_FORM_TYPE.equals(excelImportVO.getType())) {
                    ExcelDataVO excelDataVO = excelDataList.get(excelImportVO.getCol());
                    FormDataService formDataService = formDataContext.getHandler(excelImportVO.getType());
                    if (formDataService != null) {
                        FormImportCheckResultVO result =
                                formDataService.dealWhileImport(excelImportVO.getFormConfigCommon(),
                                        excelDataVO.getValue(), importCheck);
                        addError(excelImportVO, result, d, errorList);
                        dataJson.put(excelImportVO.getKey(), result.getValue());
                    } else {
                        dataJson.put(excelImportVO.getKey(), excelDataVO.getValue());
                    }
                    if (excelDataVO.getRowSpan() != null) {
                        subMaxRow = excelDataVO.getRowSpan();
                    }
                }
            }
            buildSubData(subFormTypeList, excelLineDataVOList, d, subMaxRow, dataJson, importCheck, errorList);
            dataJsonList.add(dataJson);
            d = d + subMaxRow;
        }
        ExcelImportDataVO excelImportDataVO = new ExcelImportDataVO();
        if (CollectionUtils.isEmpty(errorList)) {
            TaskCreateRequest taskCreateRequest =
                    new TaskCreateRequest(excelImportDataRequest.getApplicationId(), excelImportDataRequest.getFormId(),
                            "import", null);
            String taskId = taskService.createTask(taskCreateRequest);
            asyncExecutor.execute(() -> {
                insertOrUpdate(excelImportDataRequest, dataJsonList, taskId, UserUtils.getUser());
            });
            excelImportDataVO.setTaskId(taskId);
        }
        excelImportDataVO.setErrorList(errorList);
        excelImportDataVO.setTotalCount(dataJsonList.size());
        return excelImportDataVO;
    }

    private void insertOrUpdate(ExcelImportDataRequest excelImportDataRequest, List<JSONObject> dataJsonList,
                                String taskId, UserDomain userDomain) {
        TaskProgressHolder.initProgress(taskId, dataJsonList.size());
        String formId = excelImportDataRequest.getFormId();
        String applicationId = excelImportDataRequest.getApplicationId();
        FormVO formVO = formService.info(formId, applicationId);
        int insert = 0;
        int update = 0;
        DataStreamExecuteUtils.setNeedNewThread(Boolean.FALSE);
        try {
            if ("1".equals(excelImportDataRequest.getImportMode())) {
                for (JSONObject json : dataJsonList) {
                    UserUtils.setUser(userDomain);
                    Integer row = json.getInteger("row");
                    json.remove("row");
                    try {
                        insert(json, formVO);
                        insert++;
                        TaskProgressHolder.incrementAddCount(taskId);
                    } catch (Exception e) {
                        log.error("新增失败", e);
                        TaskProgressHolder.incrementError(taskId, row, e.getMessage());
                        if (UserUtils.getUser() == null) {
                            UserUtils.setUser(userDomain);
                        }
                    }
                }
            } else if ("2".equals(excelImportDataRequest.getImportMode())) {
                for (JSONObject json : dataJsonList) {
                    UserUtils.setUser(userDomain);
                    Integer row = json.getInteger("row");
                    json.remove("row");
                    try {
                        String uuid = json.getString("uuid");
                        if (StringUtils.isEmpty(uuid)) {
                            continue;
                        }
                        json.remove("uuid");
                        update(json, formVO.getId(), uuid, formVO.getApplicationId());
                        update++;
                        ImportProgress importProgress = new ImportProgress(insert, update, dataJsonList.size());
                        TaskProgressHolder.updateProgress(taskId, importProgress);
                    } catch (Exception e) {
                        TaskProgressHolder.incrementError(taskId, row, e.getMessage());
                        if (UserUtils.getUser() == null) {
                            UserUtils.setUser(userDomain);
                        }
                    }
                }
            } else if ("3".equals(excelImportDataRequest.getImportMode())) {
                for (JSONObject json : dataJsonList) {
                    UserUtils.setUser(userDomain);
                    Integer row = json.getInteger("row");
                    json.remove("row");
                    try {
                        String uuid = json.getString("uuid");
                        json.remove("uuid");
                        if (StringUtils.isEmpty(uuid)) {
                            insert(json, formVO);
                            insert++;
                        } else {
                            update(json, formVO.getId(), uuid, formVO.getApplicationId());
                            update++;
                        }
                    } catch (Exception e) {
                        TaskProgressHolder.incrementError(taskId, row, e.getMessage());
                        if (UserUtils.getUser() == null) {
                            UserUtils.setUser(userDomain);
                        }
                    }
                }
                ImportProgress importProgress = new ImportProgress(insert, update, dataJsonList.size());
                TaskProgressHolder.updateProgress(taskId, importProgress);
            }
        } catch (Exception e) {
            log.error("导入失败", e);
        } finally {
            ImportProgress progress = TaskProgressHolder.getProgress(taskId);
            taskService.taskFinish(taskId, JSONObject.toJSONString(progress));
            TaskProgressHolder.removeProgress(taskId);
            DataStreamExecuteUtils.removeNeedNewThread();
        }
    }

    private void update(JSONObject json, String formId, String uuid, String applicationId) {
        FormUpdateDataRequest formUpdateDataRequest = new FormUpdateDataRequest();
        formUpdateDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        formUpdateDataRequest.setFormId(formId);
        formUpdateDataRequest.setUuid(ObjectId.getGuid());
        formUpdateDataRequest.setInstValue(json);
        formUpdateDataRequest.setUuid(uuid);
        formUpdateDataRequest.setApplicationId(applicationId);
        formMongoDbService.update(formUpdateDataRequest, Boolean.FALSE);
    }

    private void insert(JSONObject json, FormVO formVO) {
        FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
        formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        formInsertDataRequest.setFormId(formVO.getId());
        formInsertDataRequest.setVersion(formVO.getVersion());
        formInsertDataRequest.setUuid(ObjectId.getGuid());
        formInsertDataRequest.setInstValue(json);
        formInsertDataRequest.setApplicationId(formVO.getApplicationId());
        formMongoDbService.insertMongoDbData(formInsertDataRequest, formVO);
    }

    private boolean checkNotExport(FormConfigCommon formConfigCommon, FormPrivilegeVO formPrivilegeVO,
                                   FormVO viewForm) {
        List<FormPrivilegeFieldConfig> fieldPrivilegeList =
                FormPrivilegeUtils.getFormPrivilegeFieldConfigs(formPrivilegeVO, viewForm);
        Map<String, FormPrivilegeFieldConfig> fieldConfigMap =
                fieldPrivilegeList.stream().collect(Collectors.toMap(FormPrivilegeFieldConfig::getName, c -> c));
        if ("system".equals(formConfigCommon.getType()) && "uuid".equals(formConfigCommon.getName())) {
            return false;
        }
        if (FormFieldTypeEnum.notExport().contains(formConfigCommon.getType())) {
            return true;
        } else {
            if (CollectionUtils.isNotEmpty(fieldPrivilegeList)) {
                FormPrivilegeFieldConfig formPrivilegeFieldConfig = fieldConfigMap.get(formConfigCommon.getName());
                if (formPrivilegeFieldConfig != null) {
                    return !formPrivilegeFieldConfig.getVisibleFlag();
                } else {
                    return true;
                }
            } else {
                return false;
                // return formConfigCommon.getVisible() != null && !formConfigCommon.getVisible();
            }
        }
    }

    private FormMongoDbExportDomain getFormMongoDbExportDomain(FormConfigCommon subFormConfigCommon, int j, int i) {
        FormMongoDbExportDomain subFormMongoDbExportDomain = new FormMongoDbExportDomain();
        subFormMongoDbExportDomain.setType(subFormConfigCommon.getType());
        subFormMongoDbExportDomain.setLabel(subFormConfigCommon.getLabel());
        subFormMongoDbExportDomain.setName(subFormConfigCommon.getName());
        subFormMongoDbExportDomain.setRow(i);
        subFormMongoDbExportDomain.setMaxRow(i);
        subFormMongoDbExportDomain.setColumn(j);
        subFormMongoDbExportDomain.setMaxColumn(j);
        return subFormMongoDbExportDomain;
    }

    public void drawData(List<FormMongoDbExportDomain> formMongoDbExportDomainList,
                         QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO, Sheet sheet, int i, int headerRow) {
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        List<FormExportDomain> formExportDomainList =
                drawSubForm(formMongoDbExportDomainList, lowcodeDataVOQueryPageVO, sheet, i, systemAllData);

        int e = headerRow + 1;
        for (FormExportDomain formExportDomain : formExportDomainList) {
            JSONObject instValue = FormSystemFieldEnum.putSystemValue(formExportDomain.getLowcodeDataVO());
            Row row = sheet.getRow(e);
            if (row == null) {
                row = sheet.createRow(e);
            }
            for (FormMongoDbExportDomain formMongoDbExportDomain : formMongoDbExportDomainList) {
                if (Constants.SUB_FORM_TYPE.equals(formMongoDbExportDomain.getType())) {
                    continue;
                }
                FormDataService formDataService = formDataContext.getHandler(formMongoDbExportDomain.getType());
                if (formDataService != null) {
                    formDataService.dealWhileExport(formMongoDbExportDomain.getColumn(), row, instValue,
                            formMongoDbExportDomain, systemAllData);
                } else {
                    String value = instValue.getString(formMongoDbExportDomain.getName());
                    ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, value);
                }
                if (formExportDomain.getMaxRow() != 1) {
                    sheet.addMergedRegion(new CellRangeAddress(e, e + formExportDomain.getMaxRow() - 1,
                            formMongoDbExportDomain.getColumn(), formMongoDbExportDomain.getColumn()));
                }
            }
            e = e + formExportDomain.getMaxRow();
        }
    }

    private List<FormExportDomain> drawSubForm(List<FormMongoDbExportDomain> formMongoDbExportDomainList,
                                               QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO, Sheet sheet, int i,
                                               SystemAllDataVO systemAllData) {
        List<FormMongoDbExportDomain> subFormTypeList =
                formMongoDbExportDomainList.stream().filter(c -> Constants.SUB_FORM_TYPE.equals(c.getType()))
                        .collect(Collectors.toList());
        List<FormExportDomain> formExportDomainList = new ArrayList<>();
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataVOQueryPageVO.getList()) {
            FormExportDomain formExportDomain = new FormExportDomain();
            formExportDomain.setLowcodeDataVO(lowcodeDataVO);
            int maxRow = 1;
            int rowNum = i + 1;
            for (FormMongoDbExportDomain formMongoDbExportDomain : subFormTypeList) {
                JSONObject instValue = lowcodeDataVO.getInstValue();
                JSONArray jsonArray = instValue.getJSONArray(formMongoDbExportDomain.getName());
                if (CollectionUtils.isEmpty(jsonArray)) {
                    for (FormMongoDbExportDomain sub : formMongoDbExportDomain.getChildren()) {
                        Row row = sheet.getRow(rowNum);
                        if (row == null) {
                            row = sheet.createRow(rowNum);
                        }
                        ExcelUtils.writeValue(sub.getColumn(), row, "");
                    }
                } else {
                    for (int d = 0; d < jsonArray.size(); d++) {
                        for (FormMongoDbExportDomain sub : formMongoDbExportDomain.getChildren()) {
                            JSONObject data = jsonArray.getJSONObject(d);
                            Row row = sheet.getRow(rowNum + d);
                            if (row == null) {
                                row = sheet.createRow(rowNum + d);
                            }
                            FormDataService formDataService = formDataContext.getHandler(sub.getType());
                            if (formDataService != null) {
                                formDataService.dealWhileExport(sub.getColumn(), row, data, sub, systemAllData);
                            } else {
                                String value = data.getString(sub.getName());
                                ExcelUtils.writeValue(sub.getColumn(), row, value);
                            }
                        }
                    }
                }
                if (jsonArray != null) {
                    maxRow = Math.max(maxRow, jsonArray.size());
                }
            }
            i = i + maxRow;
            formExportDomain.setMaxRow(maxRow);
            formExportDomainList.add(formExportDomain);
        }
        return formExportDomainList;
    }

    private void buildSubData(List<ExcelImportVO> subFormTypeList, List<ExcelLineDataVO> excelLineDataVOList, int d,
                              int subMaxRow, JSONObject dataJson, SystemAllDataNameVO importCheck,
                              List<ExcelImportDataErrorVO> excelImportDataErrorList) {
        for (ExcelImportVO excelImportVO : subFormTypeList) {
            List<JSONObject> subDataJsonList = new ArrayList<>();
            for (int i = 0; i < subMaxRow; i++) {
                ExcelLineDataVO excelLineDataVO = excelLineDataVOList.get(d + i);
                List<ExcelDataVO> excelDataList = excelLineDataVO.getExcelDataList();
                JSONObject subDataJson = new JSONObject();
                StringBuilder checkValue = new StringBuilder();
                for (ExcelImportVO subexcelImportVO : excelImportVO.getExcelImportList()) {
                    if (subexcelImportVO.getFormConfigCommon() == null) {
                        continue;
                    }
                    ExcelDataVO excelDataVO = excelDataList.get(subexcelImportVO.getCol());
                    FormDataService formDataService = formDataContext.getHandler(subexcelImportVO.getType());
                    if (formDataService != null) {
                        FormImportCheckResultVO result =
                                formDataService.dealWhileImport(subexcelImportVO.getFormConfigCommon(),
                                        excelDataVO.getValue(), importCheck);
                        subDataJson.put(subexcelImportVO.getKey(), result.getValue());
                        addError(subexcelImportVO, result, d + i, excelImportDataErrorList);
                    } else {
                        subDataJson.put(subexcelImportVO.getKey(), excelDataVO.getValue());
                    }
                    checkValue.append(excelDataVO.getValue());
                }
                if (StringUtils.isNotEmpty(checkValue.toString())) {
                    subDataJsonList.add(subDataJson);
                }
            }
            dataJson.put(excelImportVO.getKey(), subDataJsonList);
        }
    }

}
