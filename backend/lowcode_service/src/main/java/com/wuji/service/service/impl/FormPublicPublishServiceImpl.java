package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.FormPublicPublishCache;
import com.wuji.service.converter.AbstractFormPublicPublishConverter;
import com.wuji.service.mapper.FormPublicPublishMapper;
import com.wuji.service.model.entity.FormPublicPublishEntity;
import com.wuji.service.model.info.FormPublicPublishConfig;
import com.wuji.service.model.request.FormPublicPublishSaveRequest;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.TemplateFormPublicPublishService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-10
 */
@Service
public class FormPublicPublishServiceImpl extends ServiceImpl<FormPublicPublishMapper, FormPublicPublishEntity>
        implements FormPublicPublishService {

    @Autowired
    private FormPublicPublishMapper formPublicPublishMapper;

    @Autowired
    private TemplateFormPublicPublishService templateFormPublicPublishService;

    @Override
    public FormPublicPublishVO save(FormPublicPublishSaveRequest formPublicPublishSaveRequest) {
        FormPublicPublishVO info =
                info(formPublicPublishSaveRequest.getApplicationId(), formPublicPublishSaveRequest.getFormId(),
                        formPublicPublishSaveRequest.getPublishType());
        FormPublicPublishEntity formPublicPublishEntity =
                AbstractFormPublicPublishConverter.INSTANCE.toEntity(formPublicPublishSaveRequest);
        if (info == null) {
            formPublicPublishEntity.setId(ObjectId.getGuid());
            formPublicPublishEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPublicPublishEntity.setModifierName(UserUtils.getUser().getNickName());
            formPublicPublishEntity.setAccessToken(
                    Objects.requireNonNull(AESUtils.encrypt(SnowFlakeIdUtils.generateStr())).replaceAll("\\+", " "));
            formPublicPublishMapper.insert(formPublicPublishEntity);
        } else {
            formPublicPublishEntity.setId(info.getId());
            formPublicPublishEntity.setModifierName(UserUtils.getUser().getNickName());
            if (!Objects.equals(info.getState(), formPublicPublishEntity.getState())) {
                formPublicPublishEntity.setAccessToken(
                        Objects.requireNonNull(AESUtils.encrypt(SnowFlakeIdUtils.generateStr()))
                                .replaceAll("\\+", " "));

            }
            formPublicPublishMapper.updateById(formPublicPublishEntity);
            FormPublicPublishCache.clear(
                    formPublicPublishSaveRequest.getFormId() + "_" + formPublicPublishSaveRequest.getApplicationId());
        }
        return AbstractFormPublicPublishConverter.INSTANCE.toVO(formPublicPublishEntity);
    }

    @Override
    public FormPublicPublishVO info(String id) {
        LambdaQueryWrapper<FormPublicPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPublicPublishEntity::getId, id);
        FormPublicPublishEntity formPublicPublishEntity = formPublicPublishMapper.selectOne(queryWrapper);
        return AbstractFormPublicPublishConverter.INSTANCE.toVO(formPublicPublishEntity);
    }

    @Override
    public FormPublicPublishVO info(String applicationId, String formId, String publishType) {
        LambdaQueryWrapper<FormPublicPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPublicPublishEntity::getFormId, formId);
        queryWrapper.eq(FormPublicPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPublicPublishEntity::getPublishType, publishType);
        queryWrapper.last(" limit 1");
        FormPublicPublishEntity formPublicPublishEntity = formPublicPublishMapper.selectOne(queryWrapper);
        if (formPublicPublishEntity == null) {
            return null;
        }
        return AbstractFormPublicPublishConverter.INSTANCE.toVO(formPublicPublishEntity);
    }

    @Override
    public void checkSecret(String accessToken, String accessType, String formId, String applicationId) {
        FormPublicPublishVO config = FormPublicPublishCache.getConfig(formId + "_" + applicationId);
        if (config == null) {
            throw new BizException(ResultCode.NO_AUTH_1);
        }
        if (config.getState() == 0) {
            throw new BizException(ResultCode.NO_AUTH_1);
        }
        if (StringUtils.isEmpty(accessToken)) {
            throw new BizException(ResultCode.NO_AUTH_1);
        }
        if (!accessToken.equals(config.getAccessToken())) {
            throw new BizException(ResultCode.NO_AUTH_1);
        }
        FormPublicPublishConfig formPublicPublishConfig =
                JSONObject.parseObject(config.getConfig(), FormPublicPublishConfig.class);
        if ("custom".equals(formPublicPublishConfig.getInDateLimit())) {
            if (!(new Date().after(formPublicPublishConfig.getStartTime()) &&
                    formPublicPublishConfig.getEndTime().after(new Date()))) {
                throw new BizException(ResultCode.NO_AUTH_1);
            }
        }
    }

    @Override
    public List<FormPublicPublishVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormPublicPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPublicPublishEntity::getApplicationId, applicationId);
        List<FormPublicPublishEntity> formPublicPublishEntityList = formPublicPublishMapper.selectList(queryWrapper);
        return formPublicPublishEntityList.stream().map(AbstractFormPublicPublishConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useTemplate(String templateApplicationId, String applicationId) {
        List<TemplateFormPublicPublishVO> templateFormPublicPublishVOList =
                templateFormPublicPublishService.getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormPublicPublishVOList)) {
            return;
        }
        List<FormPublicPublishEntity> formPublicPublishEntityList = new ArrayList<>();
        for (TemplateFormPublicPublishVO templateFormPublicPublishVO : templateFormPublicPublishVOList) {
            FormPublicPublishEntity formPublicPublishEntity =
                    AbstractFormPublicPublishConverter.INSTANCE.toEntity(templateFormPublicPublishVO);
            formPublicPublishEntity.setId(ObjectId.getGuid());
            formPublicPublishEntity.setAccessToken(
                    Objects.requireNonNull(AESUtils.encrypt(SnowFlakeIdUtils.generateStr())).replaceAll("\\+", " "));
            formPublicPublishEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPublicPublishEntity.setModifierName(UserUtils.getUser().getNickName());
            formPublicPublishEntityList.add(formPublicPublishEntity);
        }
        saveBatch(formPublicPublishEntityList);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId) {
        List<FormPublicPublishVO> sourceFormPublicPublishList = getByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(sourceFormPublicPublishList)) {
            return;
        }
        List<FormPublicPublishEntity> formPublicPublishEntityList = new ArrayList<>();
        for (FormPublicPublishVO formPublicPublishVO : sourceFormPublicPublishList) {
            FormPublicPublishEntity formPublicPublishEntity =
                    AbstractFormPublicPublishConverter.INSTANCE.toEntity(formPublicPublishVO);
            formPublicPublishEntity.setApplicationId(applicationId);
            formPublicPublishEntity.setId(ObjectId.getGuid());
            formPublicPublishEntity.setAccessToken(
                    Objects.requireNonNull(AESUtils.encrypt(SnowFlakeIdUtils.generateStr())).replaceAll("\\+", " "));
            formPublicPublishEntity.setCreatorName(UserUtils.getUser().getNickName());
            formPublicPublishEntity.setModifierName(UserUtils.getUser().getNickName());
            formPublicPublishEntityList.add(formPublicPublishEntity);
        }
        saveBatch(formPublicPublishEntityList);
    }
}
