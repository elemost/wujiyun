package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wuji.common.context.UploadFileContext;
import com.wuji.common.model.vo.FunctionTypeVO;
import com.wuji.common.model.vo.FunctionVO;
import com.wuji.common.model.vo.UploadFilePartVO;
import com.wuji.common.properties.CosProperties;
import com.wuji.common.properties.SystemProperties;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.model.request.CommonUrlRequest;
import com.wuji.service.model.request.FormImgRequest;
import com.wuji.service.model.vo.FileVO;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.service.CommonService;
import com.wuji.service.service.FormImgService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonServiceImpl implements CommonService {


    @Autowired
    private UploadFileContext uploadFileContext;

    @Autowired
    private CosProperties cosProperties;

    @Autowired
    private FormImgService formImgService;

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public FileVO uploadFile(MultipartFile multipartFile, FileTypeEnum fileTypeEnum, Boolean secret) {
        if (secret) {
            fileTypeEnum = FileTypeEnum.AUTH;
        }
        String timeNow = ObjectId.getGuid();
        String path = fileTypeEnum.getPath() + timeNow;
        UploadFilePartVO ret = uploadFileContext.getHandler(systemProperties.getUploadType()).updateByParts(multipartFile, path);
        FileVO fileVO = new FileVO();
        fileVO.setFileUrl(ret.getUrl());
        fileVO.setFilePath(ret.getRet());
        fileVO.setFileName(multipartFile.getOriginalFilename());
        FormImgRequest formImgRequest = new FormImgRequest();
        formImgRequest.setImgType(fileTypeEnum.name());
        formImgRequest.setImgUrl(fileVO.getFileUrl());
        formImgRequest.setBucket(cosProperties.getBucketName());
        formImgRequest.setImgKey(ret.getRet());
        formImgRequest.setId(timeNow);
        formImgRequest.setSecret(secret);
        formImgRequest.setFileName(multipartFile.getOriginalFilename());
        formImgService.insert(formImgRequest);
        fileVO.setId(timeNow);
        fileVO.setImgUrl(formImgRequest.getImgUrl());
        return fileVO;
    }

    @Override
    public String downloadFile(String fileUrl) {
        FormImgVO formImgVO = formImgService.getByImg(fileUrl);
        if (formImgVO == null) {
            return fileUrl;
        }
        return uploadFileContext.getHandler(systemProperties.getUploadType())
                .downloadFile(formImgVO.getImgKey(), formImgVO.getBucket());
    }

    @Override
    public List<FunctionTypeVO> getFunctionList() {
        try (InputStream is = ResourceReader.class.getResourceAsStream("/json/function.json");) {
            String result = IOUtils.toString(is, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object object = jsonObject.get("function");
            Map<String, List<FunctionVO>> groupNameToMap =
                    JSONArray.parseArray(JSONObject.toJSONString(object), FunctionVO.class).stream()
                            .collect(Collectors.groupingBy(FunctionVO::getType));
            List<String> groupNameList =
                    Lists.newArrayList("逻辑函数", "数学函数", "文本函数", "日期函数", "数组", "其他");
            List<FunctionTypeVO> functionTypeVOList = new ArrayList<>();
            for (String groupName : groupNameList) {
                FunctionTypeVO functionTypeVO = new FunctionTypeVO();
                functionTypeVO.setGroupName(groupName);
                functionTypeVO.setItems(groupNameToMap.get(groupName));
                functionTypeVOList.add(functionTypeVO);
            }
            return functionTypeVOList;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public List<FunctionTypeVO> getMongoFunctionList() {
        try (InputStream is = ResourceReader.class.getResourceAsStream("/json/mongoFunction.json");) {
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object object = jsonObject.get("function");
            Map<String, List<FunctionVO>> groupNameToMap =
                    JSONArray.parseArray(JSONObject.toJSONString(object), FunctionVO.class).stream()
                            .collect(Collectors.groupingBy(FunctionVO::getType));
            List<String> groupNameList = Lists.newArrayList("数学函数");
            List<FunctionTypeVO> functionTypeVOList = new ArrayList<>();
            for (String groupName : groupNameList) {
                FunctionTypeVO functionTypeVO = new FunctionTypeVO();
                functionTypeVO.setGroupName(groupName);
                functionTypeVO.setItems(groupNameToMap.get(groupName));
                functionTypeVOList.add(functionTypeVO);
            }
            return functionTypeVOList;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public List<FunctionTypeVO> getFactoryFunctionList() {
        try (InputStream is = ResourceReader.class.getResourceAsStream("/json/factoryFunction.json");) {
            String result = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object object = jsonObject.get("function");
            Map<String, List<FunctionVO>> groupNameToMap =
                    JSONArray.parseArray(JSONObject.toJSONString(object), FunctionVO.class).stream()
                            .collect(Collectors.groupingBy(FunctionVO::getType));
            List<String> groupNameList = Lists.newArrayList("逻辑函数", "数学函数", "文本函数", "日期函数");
            List<FunctionTypeVO> functionTypeVOList = new ArrayList<>();
            for (String groupName : groupNameList) {
                FunctionTypeVO functionTypeVO = new FunctionTypeVO();
                functionTypeVO.setGroupName(groupName);
                functionTypeVO.setItems(groupNameToMap.get(groupName));
                functionTypeVOList.add(functionTypeVO);
            }
            return functionTypeVOList;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String getUrl(CommonUrlRequest commonUrlRequest) {
        String otherData = commonUrlRequest.getOtherData();
        String applicationId = commonUrlRequest.getApplicationId();
        String formId = commonUrlRequest.getFormId();
        String type = commonUrlRequest.getType();
        String format = "";
        if (type.equals("list")) {
            format = String.format("https://cloud.elemost.com/%s/web/independent/dataManage/%s", applicationId, formId);
            if (StringUtils.isNotEmpty(otherData)) {
                format = format + "?" + otherData;
            }
        } else {
            format = String.format("https://cloud.elemost.com/%s/web/independent/form/%s", applicationId, formId);
            if (StringUtils.isNotEmpty(otherData)) {
                format = format + "?" + otherData;
            }
        }
        String url =
                "https://cloud.elemost.com/smallProgram/companyWxLoginThree?suiteId=" + commonUrlRequest.getSuiteId() +
                        "&redirect=";
        return url + URLEncoder.encode(format);
    }
}
