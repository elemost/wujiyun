package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormImage;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormImgService;
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
public class FormFileServiceImpl extends FormCommonServiceImpl implements FormDataService {


    @Autowired
    private FormImgService formImgService;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.INPUT_FILE.getFieldType();
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
}
