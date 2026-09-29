package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionTemplate;
import com.wuji.service.model.info.FormInfoRelation;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.request.FormDataDownloadRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.FormTemplateMoreService;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.service.utils.WordTemplateProcessor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormTemplateMoreServiceImpl implements FormTemplateMoreService {

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormService formService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public void downloadWordMore(FormDataDownloadRequest formDataDownloadRequest,
                                 HttpServletResponse httpServletResponse) {
        FormExtraFunctionVO formExtraFunctionVO =
                formExtraFunctionServiceImpl.info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            lowcodeDataDomain =
                    formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                            formDataDownloadRequest.getApplicationId());
        } else {
            lowcodeDataDomain.setInstValue(new JSONObject());
        }
        JSONObject mainJson = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        JSONObject downloadJson = new JSONObject();
        List<FormConfigCommon> subFormList =
                dealJson(formDataDownloadRequest, mainJson, formExtraFunctionTemplate.getRelations(), downloadJson);
        try {
            URL url = new URL(formExtraFunctionTemplate.getFileUrl());
            getOutputStream(formExtraFunctionTemplate.getFileName(), httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8");
            XWPFDocument document = null;
            try (ServletOutputStream os = httpServletResponse.getOutputStream(); InputStream fis = url.openStream()) {
                List<String> subFormNameList =
                        subFormList.stream().map(FormConfigCommon::getName).collect(Collectors.toList());
                WordTemplateProcessor wordTemplateProcessor =
                        new WordTemplateProcessor(fis, downloadJson, subFormNameList);
                document = wordTemplateProcessor.processTemplate();
                document.write(os);
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }

    }

    @Override
    public void downloadExcelMoreTest(FormDataDownloadRequest formDataDownloadRequest,
                                      HttpServletResponse httpServletResponse) {
        FormExtraFunctionVO formExtraFunctionVO =
                formExtraFunctionServiceImpl.info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            lowcodeDataDomain =
                    formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                            formDataDownloadRequest.getApplicationId());
        } else {
            lowcodeDataDomain.setInstValue(new JSONObject());
        }
        JSONObject mainJson = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        JSONObject downloadJson = new JSONObject();
        dealJson(formDataDownloadRequest, mainJson, formExtraFunctionTemplate.getRelations(), downloadJson);
        String templatePath = "/Users/huangzeman/Desktop/文件/商品信息.docx";
        try {
            getOutputStream("file.xlsx", httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = httpServletResponse.getOutputStream();
                 FileInputStream fis = new FileInputStream(templatePath)) {
                //读取模板
                XLSTransformer transformer = new XLSTransformer();
                //向模板中写入内容
                Workbook workbook = transformer.transformXLS(fis, downloadJson);
                //写入成功后转化为输出流
                workbook.write(os);
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }
    }

    @Override
    public void downloadExcelMore(FormDataDownloadRequest formDataDownloadRequest,
                                  HttpServletResponse httpServletResponse) {
        FormExtraFunctionVO formExtraFunctionVO =
                formExtraFunctionServiceImpl.info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            lowcodeDataDomain =
                    formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                            formDataDownloadRequest.getApplicationId());
        } else {
            lowcodeDataDomain.setInstValue(new JSONObject());
        }
        JSONObject mainJson = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        JSONObject downloadJson = new JSONObject();
        dealJson(formDataDownloadRequest, mainJson, formExtraFunctionTemplate.getRelations(), downloadJson);
        try {
            URL url = new URL(formExtraFunctionTemplate.getFileUrl());
            getOutputStream(formExtraFunctionTemplate.getFileName(), httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = httpServletResponse.getOutputStream(); InputStream fis = url.openStream()) {
                //读取模板
                XLSTransformer transformer = new XLSTransformer();
                //向模板中写入内容
                Workbook workbook = transformer.transformXLS(fis, downloadJson);
                //写入成功后转化为输出流
                workbook.write(os);
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }
    }

    @Override
    public void downloadWordMoreTest(FormDataDownloadRequest formDataDownloadRequest,
                                     HttpServletResponse httpServletResponse) {
        FormExtraFunctionVO formExtraFunctionVO =
                formExtraFunctionServiceImpl.info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            lowcodeDataDomain =
                    formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                            formDataDownloadRequest.getApplicationId());
        } else {
            lowcodeDataDomain.setInstValue(new JSONObject());
        }
        JSONObject mainJson = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        JSONObject downloadJson = new JSONObject();
        List<FormConfigCommon> subFormList =
                dealJson(formDataDownloadRequest, mainJson, formExtraFunctionTemplate.getRelations(), downloadJson);
        String templatePath = "/Users/huangzeman/Desktop/文件/商品信息.docx";
        try {
            getOutputStream("output.docx", httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8");
            XWPFDocument document = null;
            try (ServletOutputStream os = httpServletResponse.getOutputStream();
                 FileInputStream fis = new FileInputStream(templatePath)) {
                List<String> subFormNameList =
                        subFormList.stream().map(FormConfigCommon::getName).collect(Collectors.toList());
                WordTemplateProcessor wordTemplateProcessor =
                        new WordTemplateProcessor(fis, downloadJson, subFormNameList);
                document = wordTemplateProcessor.processTemplate();
                document.write(os);
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }
    }

    private List<FormConfigCommon> dealJson(FormDataDownloadRequest formDataDownloadRequest, JSONObject mainJson,
                                            List<FormInfoRelation> formInfoRelations, JSONObject downloadJson) {
        if (StringUtils.isEmpty(formDataDownloadRequest.getUuid())) {
            return new ArrayList<>();
        }
        Map<Long, DataStreamCalculateVO> nodeIdMap = getCalculateMap(mainJson);
        List<String> formIdList =
                formInfoRelations.stream().map(FormInfoRelation::getFormId).collect(Collectors.toList());
        formIdList.add(formDataDownloadRequest.getFormId());
        List<FieldExistNameVO> fieldExistNameVOS = formService.getAllFormConfigCommonList(formIdList, Boolean.TRUE,
                formDataDownloadRequest.getApplicationId(), Boolean.FALSE);
        Map<String, FieldExistNameVO> fieldExistNameVOMap =
                fieldExistNameVOS.stream().collect(Collectors.toMap(FieldExistNameVO::getFormId, c -> c));
        List<FormConfigCommon> subForms = new ArrayList<>();
        for (FormInfoRelation formInfoRelation : formInfoRelations) {
            FieldExistNameVO fieldExistNameVO = fieldExistNameVOMap.get(formInfoRelation.getFormId());
            List<JSONObject> formMongoData =
                    getFormMongoData(formInfoRelation, nodeIdMap, fieldExistNameVO, formDataDownloadRequest);
            downloadJson.put(formInfoRelation.getFormId(), formMongoData);
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setName(formInfoRelation.getFormId());
            subForms.add(formConfigCommon);
        }
        FieldExistNameVO fieldExistNameVO = fieldExistNameVOMap.get(formDataDownloadRequest.getFormId());
        List<FormConfigCommon> formConfigCommonList =
                dealExportDateMore(formDataDownloadRequest.getFormId(), mainJson, fieldExistNameVO);
        List<FormConfigCommon> subFormList = formConfigCommonList.stream()
                .filter(c -> FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(c.getType()))
                .collect(Collectors.toList());
        downloadJson.putAll(mainJson);
        subForms.addAll(subFormList);
        return subForms;
    }

    private List<JSONObject> getFormMongoData(FormInfoRelation formInfoRelation,
                                              Map<Long, DataStreamCalculateVO> nodeIdMap,
                                              FieldExistNameVO fieldExistNameVO,
                                              FormDataDownloadRequest formDataDownloadRequest) {
        FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
        formSearchDataRequest.setFormId(formInfoRelation.getFormId());
        formSearchDataRequest.setApplicationId(formDataDownloadRequest.getApplicationId());
        MongodbSearchFilter filter =
                MongoSearchUtils.toFilter(formInfoRelation.getCondition(), nodeIdMap, new HashMap<>());
        formSearchDataRequest.setFilter(filter);
        formSearchDataRequest.setPageSize(1000);
        QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO = formMongoDbService.queryList(formSearchDataRequest);
        List<LowcodeDataVO> lowcodeDataVOS = lowcodeDataVOQueryPageVO.getList();
        List<JSONObject> returnList = new ArrayList<>();
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataVOS) {
            JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataVO);
            dealExportDateMore(lowcodeDataVO.getFormId(), jsonObject, fieldExistNameVO);
            returnList.add(jsonObject);
        }
        return returnList;
    }

    private List<FormConfigCommon> dealExportDateMore(String formId, JSONObject jsonObject,
                                                      FieldExistNameVO fieldExistNameVO) {
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        for (FormConfigCommon formConfigCommon : fieldExistNameVO.getFields()) {
            FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
            if (formDataService != null) {
                formDataService.customTemplate(jsonObject, formConfigCommon, formId, systemAllData);
            } else {
                Object value = jsonObject.getString(formConfigCommon.getName());
                if (value == null) {
                    jsonObject.put(formConfigCommon.getName(), "");
                    jsonObject.put(formConfigCommon.getName() + "_" + formId, "");
                } else {
                    jsonObject.put(formConfigCommon.getName() + "_" + formId, value);
                }
            }
        }
        return fieldExistNameVO.getFields();
    }

    private static Map<Long, DataStreamCalculateVO> getCalculateMap(JSONObject mainJson) {
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setJsonValue(mainJson);
        dataStreamCalculateVO.setNodeId(1L);
        Map<Long, DataStreamCalculateVO> nodeIdMap = new HashMap<>();
        nodeIdMap.put(1L, dataStreamCalculateVO);
        return nodeIdMap;
    }

    private static void getOutputStream(String fileName, HttpServletResponse response, String contentType)
            throws Exception {
        try {
            response.setContentType(contentType);
            response.setCharacterEncoding("utf8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
        } catch (IOException e) {
            throw new Exception("导出excel表格失败!", e);
        }
    }
}
