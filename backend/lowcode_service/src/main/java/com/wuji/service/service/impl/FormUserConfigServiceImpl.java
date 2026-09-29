package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormUserConfigConverter;
import com.wuji.service.mapper.FormUserConfigMapper;
import com.wuji.service.model.entity.FormUserConfigEntity;
import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.request.FormUserConfigSaveRequest;
import com.wuji.service.model.vo.FormUserConfigVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormService;
import com.wuji.service.service.FormUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-18
 */
@Service
public class FormUserConfigServiceImpl extends ServiceImpl<FormUserConfigMapper, FormUserConfigEntity>
        implements FormUserConfigService {

    @Autowired
    private FormUserConfigMapper formUserConfigMapper;

    @Autowired
    private FormService formService;

    @Override
    public void save(FormUserConfigSaveRequest formUserConfigSaveRequest) {
        LambdaQueryWrapper<FormUserConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormUserConfigEntity::getId, formUserConfigSaveRequest.getFormId());
        queryWrapper.eq(FormUserConfigEntity::getUserId, UserUtils.getUser().getUserIdLongValue());
        queryWrapper.eq(FormUserConfigEntity::getConfigType, formUserConfigSaveRequest.getConfigType());
        queryWrapper.eq(FormUserConfigEntity::getApplicationId, formUserConfigSaveRequest.getApplicationId());
        queryWrapper.eq(FormUserConfigEntity::getDeleted, Boolean.FALSE);
        FormUserConfigEntity formUserConfigEntity =
                AbstractFormUserConfigConverter.INSTANCE.toEntity(formUserConfigSaveRequest);
        FormUserConfigEntity exist = formUserConfigMapper.selectOne(queryWrapper);
        if (exist == null) {
            formUserConfigEntity.setUserId(UserUtils.getUser().getUserIdLongValue());
            formUserConfigEntity.setCreator(UserUtils.getUser().getNickName());
            formUserConfigEntity.setModifier(UserUtils.getUser().getNickName());
            formUserConfigEntity.setId(formUserConfigSaveRequest.getFormId());
            formUserConfigMapper.insert(formUserConfigEntity);
        } else {
            formUserConfigEntity.setModifier(UserUtils.getUser().getNickName());
            formUserConfigMapper.update(formUserConfigEntity, queryWrapper);
        }
        if ("CONTROL".equals(formUserConfigSaveRequest.getSource())) {
            FormUpdateRequest formUpdateRequest = new FormUpdateRequest();
            formUpdateRequest.setId(formUserConfigSaveRequest.getFormId());
            formUpdateRequest.setApplicationId(formUserConfigSaveRequest.getApplicationId());
            formUpdateRequest.setFormConfig(formUserConfigSaveRequest.getConfig());
            formService.updateForm(formUpdateRequest);
        }
    }

    @Override
    public FormUserConfigVO getInfo(String formId, String applicationId, String configType) {
        FormUserConfigVO formUserConfigVO = new FormUserConfigVO();
        formUserConfigVO.setFormId(formId);
        formUserConfigVO.setApplicationId(applicationId);
        formUserConfigVO.setConfigType(configType);
        FormVO info = formService.info(formId, applicationId);
        formUserConfigVO.setConfig(info.getFormConfig());
        return formUserConfigVO;
    }
}
