package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.QRCodeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormExtraFunctionConverter;
import com.wuji.service.enums.FormExtraFunctionRelationTypeEnum;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.mapper.FormExtraFunctionMapper;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.info.FormExtraFunctionButtonAction;
import com.wuji.service.model.info.FormExtraFunctionTitle;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionSortRequest;
import com.wuji.service.model.request.FormExtraFunctionUpdateRequest;
import com.wuji.service.model.vo.FormExtraFunctionRelationVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionRelationVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import com.wuji.service.service.FormExtraFunctionRelationService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.TemplateFormExtraFunctionService;
import com.wuji.service.utils.TemplateDealConfigUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
@Service("formExtraFunctionServiceImpl")
public class FormExtraFunctionServiceImpl extends ServiceImpl<FormExtraFunctionMapper, FormExtraFunctionEntity>
        implements FormExtraFunctionService {

    @Autowired
    private FormExtraFunctionMapper formExtraFunctionMapper;

    @Autowired
    private FormExtraFunctionRelationService formExtraFunctionRelationService;

    @Autowired
    private TemplateFormExtraFunctionService templateFormExtraFunctionService;

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Value("${image.upload.path:/home/wj/app-work/lowcode/}")
    private String imageUploadPath;

    @Override
    public String create(FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        FormExtraFunctionEntity formExtraFunctionEntity =
                AbstractFormExtraFunctionConverter.INSTANCE.toEntity(formExtraFunctionCreateRequest);
        if (formExtraFunctionCreateRequest.getConfigJson() != null) {
            formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionCreateRequest.getConfigJson()));
        }
        formExtraFunctionEntity.setId(ObjectId.getGuid());
        formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
        formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());

        QueryWrapper<FormExtraFunctionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("form_id", formExtraFunctionCreateRequest.getFormId());
        queryWrapper.eq("application_id", formExtraFunctionCreateRequest.getApplicationId());
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.eq("function_type", formExtraFunctionCreateRequest.getFunctionType());
        Integer sort = formExtraFunctionMapper.maxSort(queryWrapper);
        formExtraFunctionEntity.setSort(sort + 1);
        formExtraFunctionMapper.insert(formExtraFunctionEntity);
        formExtraFunctionRelationService.save(formExtraFunctionEntity.getId(),
                FormExtraFunctionRelationTypeEnum.PRIVILEGE.name(),
                formExtraFunctionCreateRequest.getPrivilegeIdList());
        return formExtraFunctionEntity.getId();
    }

    @Override
    public String save(FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formExtraFunctionCreateRequest.getFormId());
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, formExtraFunctionCreateRequest.getApplicationId());
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectOne(queryWrapper);
        if (formExtraFunctionEntity == null) {
            formExtraFunctionEntity = new FormExtraFunctionEntity();
            formExtraFunctionEntity.setId(ObjectId.getGuid());
            formExtraFunctionEntity.setFormId(formExtraFunctionCreateRequest.getFormId());
            formExtraFunctionEntity.setApplicationId(formExtraFunctionCreateRequest.getApplicationId());
            formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionCreateRequest.getConfigJson()));
            formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_TITLE.name());
            formExtraFunctionMapper.insert(formExtraFunctionEntity);
        } else {
            formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionCreateRequest.getConfigJson()));
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            formExtraFunctionMapper.updateById(formExtraFunctionEntity);
        }
        return formExtraFunctionEntity.getId();
    }

    @Override
    public void update(FormExtraFunctionUpdateRequest formExtraFunctionUpdateRequest) {
        FormExtraFunctionEntity formExtraFunctionEntity =
                AbstractFormExtraFunctionConverter.INSTANCE.toEntity(formExtraFunctionUpdateRequest);
        formExtraFunctionEntity.setConfig(JSONObject.toJSONString(formExtraFunctionUpdateRequest.getConfigJson()));
        formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
        formExtraFunctionMapper.updateById(formExtraFunctionEntity);
        formExtraFunctionRelationService.save(formExtraFunctionEntity.getId(),
                FormExtraFunctionRelationTypeEnum.PRIVILEGE.name(),
                formExtraFunctionUpdateRequest.getPrivilegeIdList());
    }

    @Override
    public FormExtraFunctionVO info(String id) {
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectById(id);
        if (formExtraFunctionEntity == null) {
            return null;
        }
        FormExtraFunctionVO formExtraFunctionVO =
                AbstractFormExtraFunctionConverter.INSTANCE.toVO(formExtraFunctionEntity);
        formExtraFunctionVO.setConfigJson(JSONObject.parseObject(formExtraFunctionEntity.getConfig()));
        if (UserUtils.getUser() != null) {
            List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList =
                    formExtraFunctionRelationService.getByFunctionIdList(Collections.singletonList(id),
                            formExtraFunctionEntity.getApplicationId());
            formExtraFunctionVO.setFormExtraFunctionRelationList(formExtraFunctionRelationVOList);
        }
        return formExtraFunctionVO;
    }

    @Override
    public List<FormExtraFunctionVO> getByFormId(String formId, String applicationId, String functionType) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formId);
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, functionType);
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByAsc(FormExtraFunctionEntity::getSort, FormExtraFunctionEntity::getCreateTime);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return new ArrayList<>();
        }
        List<FormExtraFunctionVO> formExtraFunctionList = new ArrayList<>();
        List<String> idList =
                formExtraFunctionEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList =
                formExtraFunctionRelationService.getByFunctionIdList(idList, applicationId);
        Map<String, List<FormExtraFunctionRelationVO>> functionMap = formExtraFunctionRelationVOList.stream()
                .collect(Collectors.groupingBy(FormExtraFunctionRelationVO::getFunctionId));
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            FormExtraFunctionVO formExtraFunctionVO =
                    AbstractFormExtraFunctionConverter.INSTANCE.toVO(formExtraFunctionEntity);
            if (StringUtils.isNotEmpty(formExtraFunctionEntity.getConfig())) {
                if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(functionType)) {
                    formExtraFunctionVO.setFormExtraFunctionButton(
                            JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionButton.class));
                } else if (FormExtraFunctionTypeEnum.CUSTOM_TITLE.name().equals(functionType)) {
                    formExtraFunctionVO.setFormExtraFunctionTitle(
                            JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionTitle.class));
                } else if (FormExtraFunctionTypeEnum.INFO.name().equals(functionType)) {
                    formExtraFunctionVO.setInfo(JSONObject.parseObject(formExtraFunctionEntity.getConfig()));
                } else {
                    formExtraFunctionVO.setConfigJson(JSONObject.parseObject(formExtraFunctionEntity.getConfig()));
                }
            }
            formExtraFunctionVO.setFormExtraFunctionRelationList(functionMap.get(formExtraFunctionEntity.getId()));
            formExtraFunctionList.add(formExtraFunctionVO);
        }
        return formExtraFunctionList;
    }

    @Override
    public List<FormExtraFunctionVO> getByFormIdAndGroupId(String applicationId, String formId, List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOS = new ArrayList<>();
        if (!groupIds.contains(Constants.ADMIN_PRIVILEGE)) {
            formExtraFunctionRelationVOS = formExtraFunctionRelationService.getByBusinessIds(groupIds, null);
            if (CollectionUtils.isEmpty(formExtraFunctionRelationVOS)) {
                return new ArrayList<>();
            }
        }
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formId);
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name());
        queryWrapper.orderByAsc(FormExtraFunctionEntity::getSort, FormExtraFunctionEntity::getCreateTime);
        if (!groupIds.contains(Constants.ADMIN_PRIVILEGE)) {
            queryWrapper.in(FormExtraFunctionEntity::getId,
                    formExtraFunctionRelationVOS.stream().map(FormExtraFunctionRelationVO::getFunctionId)
                            .collect(Collectors.toList()));
        }
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        List<FormExtraFunctionVO> formExtraFunctionList = new ArrayList<>();
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            FormExtraFunctionVO formExtraFunctionVO =
                    AbstractFormExtraFunctionConverter.INSTANCE.toVO(formExtraFunctionEntity);
            formExtraFunctionVO.setFormExtraFunctionButton(
                    JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionButton.class));
            formExtraFunctionList.add(formExtraFunctionVO);
        }
        return formExtraFunctionList;
    }

    @Override
    public void delete(String id) {
        FormExtraFunctionEntity formExtraFunctionEntity = new FormExtraFunctionEntity();
        formExtraFunctionEntity.setId(id);
        formExtraFunctionEntity.setDeleted(Boolean.TRUE);
        formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
        formExtraFunctionMapper.updateById(formExtraFunctionEntity);
    }

    @Override
    public List<FormExtraFunctionVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return new ArrayList<>();
        }
        List<FormExtraFunctionVO> formExtraFunctionList = new ArrayList<>();
        List<String> idList =
                formExtraFunctionEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<FormExtraFunctionRelationVO> formExtraFunctionRelationVOList =
                formExtraFunctionRelationService.getByFunctionIdList(idList, applicationId);
        Map<String, List<FormExtraFunctionRelationVO>> functionMap = formExtraFunctionRelationVOList.stream()
                .collect(Collectors.groupingBy(FormExtraFunctionRelationVO::getFunctionId));
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            FormExtraFunctionVO formExtraFunctionVO =
                    AbstractFormExtraFunctionConverter.INSTANCE.toVO(formExtraFunctionEntity);
            formExtraFunctionVO.setFormExtraFunctionRelationList(functionMap.get(formExtraFunctionEntity.getId()));
            formExtraFunctionList.add(formExtraFunctionVO);
        }
        return formExtraFunctionList;
    }

    @Override
    public void useTemplate(String applicationId, String templateApplicationId, Map<String, String> privilegeMap) {
        List<TemplateFormExtraFunctionVO> templateFormExtraFunctionVOList =
                templateFormExtraFunctionService.getByApplicationId(templateApplicationId);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = new ArrayList<>();
        Map<String, String> functionIdMap = new HashMap<>();
        for (TemplateFormExtraFunctionVO templateFormExtraFunctionVO : templateFormExtraFunctionVOList) {
            FormExtraFunctionEntity formExtraFunctionEntity =
                    AbstractFormExtraFunctionConverter.INSTANCE.toEntity(templateFormExtraFunctionVO);
            String guid = ObjectId.getGuid();
            functionIdMap.put(templateFormExtraFunctionVO.getId(), guid);
            formExtraFunctionEntity.setId(guid);
            formExtraFunctionEntity.setApplicationId(applicationId);
            formExtraFunctionEntity.setCreator(UserUtils.getUser().getNickName());
            formExtraFunctionEntity.setModifier(UserUtils.getUser().getNickName());
            if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(templateFormExtraFunctionVO.getFunctionType())) {
                formExtraFunctionEntity.setConfig(
                        TemplateDealConfigUtil.dealFunctionConfig(formExtraFunctionEntity.getConfig()));
            }
            formExtraFunctionEntityList.add(formExtraFunctionEntity);
        }
        buildConfig(formExtraFunctionEntityList, functionIdMap, new HashMap<>());
        saveBatch(formExtraFunctionEntityList);
        formExtraFunctionRelationService.useTemplate(functionIdMap, privilegeMap);
    }

    private static void buildConfig(List<FormExtraFunctionEntity> formExtraFunctionEntityList,
                                    Map<String, String> functionIdMap, Map<String, String> dataStreamIdMap) {
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(formExtraFunctionEntity.getFunctionType())) {
                JSONObject jsonObject = JSONObject.parseObject(formExtraFunctionEntity.getConfig());
                String action = jsonObject.getString("action");
                if ("info".equals(action)) {
                    String businessId = jsonObject.getString("businessId");
                    jsonObject.put("businessId", functionIdMap.get(businessId));
                } else if ("INTELLECTUAL_ASSISTANT".equals(action)) {
                    String assistantId = jsonObject.getString("assistantId");
                    String newDataStreamId = dataStreamIdMap.get(assistantId);
                    if (newDataStreamId != null && assistantId != null) {
                        jsonObject.put("assistantId", newDataStreamId);
                    }
                }
                formExtraFunctionEntity.setConfig(jsonObject.toJSONString());
            }
        }
    }

    @Override
    public void useTemplateDefaultPrivilege(String applicationId, String templateApplicationId,
                                            Map<String, String> categoryToPrivilegeMap) {
        List<TemplateFormExtraFunctionVO> templateFormExtraFunctionVOList =
                templateFormExtraFunctionService.getByApplicationId(templateApplicationId);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = new ArrayList<>();
        List<TemplateFormExtraFunctionRelationVO> formExtraFunctionRelationVOList = new ArrayList<>();
        Map<String, String> functionIdMap = new HashMap<>();
        for (TemplateFormExtraFunctionVO templateFormExtraFunctionVO : templateFormExtraFunctionVOList) {
            FormExtraFunctionEntity formExtraFunctionEntity =
                    AbstractFormExtraFunctionConverter.INSTANCE.toEntity(templateFormExtraFunctionVO);
            String guid = ObjectId.getGuid();
            functionIdMap.put(templateFormExtraFunctionVO.getId(), guid);
            formExtraFunctionEntity.setId(guid);
            formExtraFunctionEntity.setApplicationId(applicationId);
            if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(templateFormExtraFunctionVO.getFunctionType())) {
                formExtraFunctionEntity.setConfig(
                        TemplateDealConfigUtil.dealFunctionConfig(formExtraFunctionEntity.getConfig()));
                if (CollectionUtils.isNotEmpty(templateFormExtraFunctionVO.getFormExtraFunctionRelationList())) {
                    TemplateFormExtraFunctionRelationVO templateFormExtraFunctionRelationVO =
                            new TemplateFormExtraFunctionRelationVO();
                    templateFormExtraFunctionRelationVO.setFunctionId(guid);
                    templateFormExtraFunctionRelationVO.setBusinessType(
                            FormExtraFunctionRelationTypeEnum.PRIVILEGE.name());
                    templateFormExtraFunctionRelationVO.setBusinessId(
                            categoryToPrivilegeMap.get(templateFormExtraFunctionVO.getFormId()));
                    formExtraFunctionRelationVOList.add(templateFormExtraFunctionRelationVO);
                }
            }
            formExtraFunctionEntityList.add(formExtraFunctionEntity);
        }
        buildConfig(formExtraFunctionEntityList, functionIdMap, new HashMap<>());
        saveBatch(formExtraFunctionEntityList);
        formExtraFunctionRelationService.useTemplateDefault(formExtraFunctionRelationVOList);
    }

    @Override
    public void generateQrcode(HttpServletResponse httpServletResponse, String id, String dataUuid) {
        FormExtraFunctionVO info = info(id);
        FormExtraFunctionButton formExtraFunctionButton =
                JSONObject.parseObject(info.getConfig(), FormExtraFunctionButton.class);
        FormPublicPublishVO formPublicPublishVO =
                formPublicPublishService.info(info.getApplicationId(), formExtraFunctionButton.getBusinessId(),
                        "FORM_FILL");
        if (formPublicPublishVO == null) {
            return;
        }
        String url = ConfigCache.getValue(ConfigEnum.LOWCODE_PUBLIC_PUBLISH_URL.name());
        String path = String.format("publish/%s", formPublicPublishVO.getId());
        url = url + path;
        url = String.format("%s?dataUuid=%s&buttonId=%s", url, dataUuid, info.getId());
        String qrCode = QRCodeUtils.generateAndSaveQRCode(url, imageUploadPath, String.valueOf(info.getId()));
        if (qrCode == null) {
            return;
        }
        File file = new File(qrCode);
        try {
            FileUtils.copyFile(file, httpServletResponse.getOutputStream());
        } catch (Exception e) {
            log.error("生成二维码失败:" + e);
        } finally {
            file.delete();
        }
    }

    @Override
    public List<FormExtraFunctionButtonAction> getActions(String id, String applicationId, String formId) {
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectById(id);
        if (formExtraFunctionEntity == null) {
            return null;
        }
        FormExtraFunctionButton formExtraFunctionButton =
                JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionButton.class);
        JSONObject actionConfig = formExtraFunctionButton.getActionConfig();
        String actions = actionConfig.getString("actions");
        return JSONArray.parseArray(actions, FormExtraFunctionButtonAction.class);
    }

    @Override
    public void sort(FormExtraFunctionSortRequest formExtraFunctionSortRequest) {
        if (CollectionUtils.isEmpty(formExtraFunctionSortRequest.getIdList())) {
            return;
        }
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formExtraFunctionSortRequest.getFormId());
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, formExtraFunctionSortRequest.getApplicationId());
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, formExtraFunctionSortRequest.getFunctionType());
        queryWrapper.in(FormExtraFunctionEntity::getId, formExtraFunctionSortRequest.getIdList());
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return;
        }
        Map<String, FormExtraFunctionEntity> formExtraFunctionEntityMap =
                formExtraFunctionEntityList.stream().collect(Collectors.toMap(BaseUuidEntity::getId, c -> c));
        int sort = 0;
        for (String id : formExtraFunctionSortRequest.getIdList()) {
            FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionEntityMap.get(id);
            if (formExtraFunctionEntity != null) {
                formExtraFunctionEntity.setSort(sort);
            }
            sort++;
        }
        updateBatchById(formExtraFunctionEntityList);
    }

    @Override
    public void copy(String formId, String newFormId, String applicationId, Map<String, String> privilegeMap,
                     Map<String, String> dataStreamIdMap) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getFormId, formId);
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return;
        }
        Map<String, String> functionIdMap = new HashMap<>();
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            String guid = ObjectId.getGuid();
            functionIdMap.put(formExtraFunctionEntity.getId(), guid);
            formExtraFunctionEntity.setId(guid);
            formExtraFunctionEntity.setFormId(newFormId);
        }
        buildConfig(formExtraFunctionEntityList, functionIdMap, dataStreamIdMap);
        saveBatch(formExtraFunctionEntityList);
        formExtraFunctionRelationService.copy(functionIdMap, privilegeMap);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, Map<String, String> privilegeMap,
                                Boolean share) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getApplicationId, sourceApplicationId);
        queryWrapper.eq(FormExtraFunctionEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByAsc(FormExtraFunctionEntity::getSort, FormExtraFunctionEntity::getCreateTime);
        List<FormExtraFunctionEntity> formExtraFunctionEntityList = formExtraFunctionMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formExtraFunctionEntityList)) {
            return;
        }
        Map<String, String> functionIdMap = new HashMap<>();
        for (FormExtraFunctionEntity formExtraFunctionEntity : formExtraFunctionEntityList) {
            String guid = ObjectId.getGuid();
            functionIdMap.put(formExtraFunctionEntity.getId(), guid);
            formExtraFunctionEntity.setApplicationId(applicationId);
            formExtraFunctionEntity.setId(guid);
            if (share) {
                if (FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name().equals(formExtraFunctionEntity.getFunctionType())) {
                    formExtraFunctionEntity.setConfig(
                            TemplateDealConfigUtil.dealFunctionConfig(formExtraFunctionEntity.getConfig()));
                }
            }
        }
        buildConfig(formExtraFunctionEntityList, functionIdMap, new HashMap<>());
        saveBatch(formExtraFunctionEntityList);
        formExtraFunctionRelationService.copy(functionIdMap, privilegeMap);
    }
}
