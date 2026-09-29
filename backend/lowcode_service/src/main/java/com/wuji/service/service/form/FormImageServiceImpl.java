package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.context.UploadFileContext;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormImage;
import com.wuji.common.properties.SystemProperties;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormImgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormImageServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Autowired
    private FormImgService formImgService;

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private UploadFileContext uploadFileContext;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_IMAGE.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                if (StringUtils.isNotEmpty(url)) {
                    if (url.contains("?")) {
                        jsonObject.put("url", url.split("\\?")[0]);
                    }
                }
            }
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        JSONArray jsonArray = instValue.getJSONArray(formConfigCommon.getName());
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                if (StringUtils.isNotEmpty(url)) {
                    if (url.contains("?")) {
                        jsonObject.put("url", url.split("\\?")[0]);
                    }
                }
            }
        }
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        List<String> urlList = new ArrayList<>();
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONArray jsonArray = lowcodeDataVO.getInstValue().getJSONArray(formConfigCommon.getName());
            if (jsonArray != null) {
                List<FormImage> formImages = jsonArray.toJavaList(FormImage.class);
                urlList.addAll(formImages.stream().map(FormImage::getUrl).collect(Collectors.toList()));
            }
        }
        List<FormImgVO> formImgVOS = formImgService.getByImgList(urlList);
        Map<String, FormImgVO> imgUrlMap = formImgVOS.stream().collect(Collectors.toMap(FormImgVO::getImgUrl, c -> c));
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONArray jsonArray = lowcodeDataVO.getInstValue().getJSONArray(formConfigCommon.getName());
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String url = jsonObject.getString("url");
                    FormImgVO formImgVO = imgUrlMap.get(url);
                    if (formImgVO != null && formImgVO.getSecret()) {
                        jsonObject.put("url", uploadFileContext.getHandler(systemProperties.getUploadType())
                                .downloadFile(url, formImgVO.getBucket()));
                    }
                }
            }
        }
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        List<String> urlList = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray jsonArray = jsonObject.getJSONArray(mongodbSearchField.getName());
            if (jsonArray != null) {
                List<FormImage> formImages = jsonArray.toJavaList(FormImage.class);
                urlList.addAll(formImages.stream().map(FormImage::getUrl).collect(Collectors.toList()));
            }
        }
        List<FormImgVO> formImgVOS = formImgService.getByImgList(urlList);
        Map<String, FormImgVO> imgUrlMap = formImgVOS.stream().collect(Collectors.toMap(FormImgVO::getImgUrl, c -> c));
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray jsonArray = jsonObject.getJSONArray(mongodbSearchField.getName());
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String url = json.getString("url");
                    FormImgVO formImgVO = imgUrlMap.get(url);
                    if (formImgVO != null && formImgVO.getSecret()) {
                        json.put("url", uploadFileContext.getHandler(systemProperties.getUploadType())
                                .downloadFile(url, formImgVO.getBucket()));
                    }
                }
            }
        }
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        if (value == null) {
            return new FormSyncCheckResultVO();
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(value));
        List<String> fileIdList = jsonArray.toJavaList(String.class);
        if (CollectionUtils.isEmpty(fileIdList)) {
            return new FormSyncCheckResultVO();
        }
        List<FormImgVO> formImgVOList = formImgService.getByIdList(fileIdList);
        List<FormImage> formImages = new ArrayList<>();
        for (FormImgVO formImgVO : formImgVOList) {
            FormImage formImage = new FormImage();
            formImage.setName(formImgVO.getFileName());
            formImage.setUrl(formImgVO.getImgUrl());
            formImages.add(formImage);
        }
        formSyncCheckResultVO.setValue(formImages);
        return formSyncCheckResultVO;
    }

    @Override
    public void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue,
                              SystemAllDataVO systemAllData, JSONObject sendJson) {
        Object value = instValue.get(formExtraFunctionSync.getName());
        if (value == null) {
            return;
        }
        List<FormImage> formImages = JSONArray.parseArray(JSONObject.toJSONString(value), FormImage.class);
        List<String> urlList = formImages.stream().map(FormImage::getUrl).collect(Collectors.toList());
        List<FormImgVO> formImgVOS = formImgService.getByImgList(urlList);
        Map<String, String> imgUrlToIdMap = new HashMap<>();
        for (FormImgVO formImgVO : formImgVOS) {
            imgUrlToIdMap.put(formImgVO.getImgUrl(), formImgVO.getId());
        }
        List<String> fileIdList = new ArrayList<>();
        for (FormImage formImage : formImages) {
            String id = imgUrlToIdMap.get(formImage.getUrl());
            if (StringUtils.isNotEmpty(id)) {
                fileIdList.add(id);
            }
        }
        sendJson.put(formExtraFunctionSync.getMappingField(), fileIdList);
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        Object value = instValue.putIfAbsent(formConfigCommon.getName(), "");
        List<FormImage> formImages = JSONArray.parseArray(JSONArray.toJSONString(value), FormImage.class);
        if (CollectionUtils.isEmpty(formImages)) {
            putValue(instValue, null, formId, formConfigCommon);
            return;
        }
        FormImage formImage = formImages.get(0);
        byte[] buffer =
                uploadFileContext.getHandler(systemProperties.getUploadType()).getBytesByUrl(formImage.getUrl());
        putValue(instValue, buffer, formId, formConfigCommon);
    }
}
