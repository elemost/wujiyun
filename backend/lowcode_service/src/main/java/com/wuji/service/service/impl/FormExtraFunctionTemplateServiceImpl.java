package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionTemplate;
import com.wuji.service.model.request.FormDataDownloadRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormExtraFunctionTemplateService;
import com.wuji.service.service.FormImgService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormExtraFunctionTemplateServiceImpl extends FormExtraFunctionServiceImpl
        implements FormExtraFunctionTemplateService {

    @Autowired
    private FormImgService formImgService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private FormService formService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public void downloadExcel(FormDataDownloadRequest formDataDownloadRequest,
                              HttpServletResponse httpServletResponse) {
        LowcodeDataDomain info = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            info = formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                    formDataDownloadRequest.getApplicationId());
        } else {
            info.setInstValue(new JSONObject());
        }
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(info);
        FormExtraFunctionVO formExtraFunctionVO = info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        FormImgVO formImgVO = formImgService.getImg(formExtraFunctionTemplate.getFileId());
        dealExportDate(formDataDownloadRequest, jsonObject);
        try {
            URL url = new URL(formImgVO.getImgUrl());
            getOutputStream(formImgVO.getFileName(), httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = httpServletResponse.getOutputStream(); InputStream fis = url.openStream()) {
                //读取模板
                XLSTransformer transformer = new XLSTransformer();
                //向模板中写入内容
                Workbook workbook = transformer.transformXLS(fis, jsonObject);
                //写入成功后转化为输出流
                workbook.write(os);
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }
    }

    private List<FormConfigCommon> dealExportDate(FormDataDownloadRequest formDataDownloadRequest,
                                                  JSONObject jsonObject) {
        FieldExistNameVO fieldExistNameVO =
                formService.getAllFormConfigCommonList(formDataDownloadRequest.getFormId(), Boolean.TRUE,
                        formDataDownloadRequest.getApplicationId(), Boolean.FALSE);
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        for (FormConfigCommon formConfigCommon : fieldExistNameVO.getFields()) {
            FormDataService formDataService = formDataContext.getHandler(formConfigCommon.getType());
            if (formDataService != null) {
                formDataService.customTemplate(jsonObject, formConfigCommon, formDataDownloadRequest.getFormId(), systemAllData);
            } else {
                jsonObject.putIfAbsent(formConfigCommon.getName(), "");
            }
        }
        return fieldExistNameVO.getFields();
    }

    @Override
    public void downloadExcelTest(FormDataDownloadRequest formDataDownloadRequest,
                                  HttpServletResponse httpServletResponse) {
        LowcodeDataDomain info =
                formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                        formDataDownloadRequest.getApplicationId());
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(info);
        dealExportDate(formDataDownloadRequest, jsonObject);
        String templatePath = "/Users/huangzeman/Desktop/文件/模版.xlsx";
        try {
            getOutputStream("file.xlsx", httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try (ServletOutputStream os = httpServletResponse.getOutputStream();
                 FileInputStream fis = new FileInputStream(templatePath)) {
                //读取模板
                XLSTransformer transformer = new XLSTransformer();
                //向模板中写入内容
                Workbook workbook = transformer.transformXLS(fis, jsonObject);
                //写入成功后转化为输出流
                workbook.write(os);
            }
        } catch (Exception e) {
            log.error("导出数据失败", e);
            throw new ServiceException(ServiceResultCode.TEMPLATE_EXPORT_ERROR);
        }
    }

    @Override
    public void downloadWord(FormDataDownloadRequest formDataDownloadRequest, HttpServletResponse httpServletResponse) {
        LowcodeDataDomain info = new LowcodeDataDomain();
        if (StringUtils.isNotEmpty(formDataDownloadRequest.getUuid())) {
            info = formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                    formDataDownloadRequest.getApplicationId());
        } else {
            info.setInstValue(new JSONObject());
        }
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(info);
        FormExtraFunctionVO formExtraFunctionVO = info(formDataDownloadRequest.getTemplateId());
        FormExtraFunctionTemplate formExtraFunctionTemplate =
                JSONObject.parseObject(formExtraFunctionVO.getConfigJson().toJSONString(),
                        FormExtraFunctionTemplate.class);
        FormImgVO formImgVO = formImgService.getImg(formExtraFunctionTemplate.getFileId());
        List<FormConfigCommon> formConfigCommonList = dealExportDate(formDataDownloadRequest, jsonObject);
        List<FormConfigCommon> subFormList = formConfigCommonList.stream()
                .filter(c -> FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(c.getType()))
                .collect(Collectors.toList());
        try {
            URL url = new URL(formImgVO.getImgUrl());
            getOutputStream(formImgVO.getFileName(), httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8");
            XWPFDocument document = null;
            try (ServletOutputStream os = httpServletResponse.getOutputStream(); InputStream fis = url.openStream()) {
                List<String> subFormNameList =
                        subFormList.stream().map(FormConfigCommon::getName).collect(Collectors.toList());
                WordTemplateProcessor wordTemplateProcessor =
                        new WordTemplateProcessor(fis, jsonObject, subFormNameList);
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
    public void downloadWordTest(FormDataDownloadRequest formDataDownloadRequest,
                                 HttpServletResponse httpServletResponse) {
        LowcodeDataDomain info =
                formMongoDbService.info(formDataDownloadRequest.getUuid(), formDataDownloadRequest.getFormId(),
                        formDataDownloadRequest.getApplicationId());
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(info);
        List<FormConfigCommon> formConfigCommonList = dealExportDate(formDataDownloadRequest, jsonObject);
        List<FormConfigCommon> subFormList = formConfigCommonList.stream()
                .filter(c -> FormFieldTypeEnum.SUB_FORM_TYPE.getFieldType().equals(c.getType()))
                .collect(Collectors.toList());
        String templatePath = "/Users/huangzeman/Desktop/文件/模板-test.docx";
        try {
            getOutputStream("output.docx", httpServletResponse,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=utf-8");
            XWPFDocument document = null;
            try (ServletOutputStream os = httpServletResponse.getOutputStream();
                 FileInputStream fis = new FileInputStream(templatePath)) {
                List<String> subFormNameList =
                        subFormList.stream().map(FormConfigCommon::getName).collect(Collectors.toList());
                WordTemplateProcessor wordTemplateProcessor = new WordTemplateProcessor(fis, jsonObject, subFormNameList);
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

    // public static void main(String[] args) throws IOException {
    //
    //     // 模板路径
    //     String templatePath = "/Users/huangzeman/Desktop/文件/模板.docx";
    //
    //     // 数据填充
    //     Map<String, Object> data = new HashMap<>();
    //     data.put("name", "张三");
    //     data.put("age", 25);
    //     data.put("gender", "男");
    //     data.put("hobbies", "读书、运动");
    //
    //     // 读取模板并渲染数据
    //     XWPFTemplate template = XWPFTemplate.compile(templatePath).render(new HashMap<>(data));
    //
    //     // 输出到新文件
    //     String outputPath = "/Users/huangzeman/Desktop/文件/template/output.docx";
    //     template.writeToFile(outputPath);
    //     System.out.println("导出成功：" + outputPath);
    // }
}
