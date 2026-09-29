package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.mapper.FormUseTimeMapper;
import com.wuji.service.model.entity.FormUseTimeEntity;
import com.wuji.service.service.FormUseTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
@Service
public class FormUseTimeServiceImpl extends ServiceImpl<FormUseTimeMapper, FormUseTimeEntity>
        implements FormUseTimeService {

    @Autowired
    private FormUseTimeMapper formUseTimeMapper;

    @Override
    public void save(String formId, String applicationId) {
        LambdaQueryWrapper<FormUseTimeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormUseTimeEntity::getFormId, formId);
        queryWrapper.eq(FormUseTimeEntity::getUserId, UserUtils.getUser().getUserId());
        queryWrapper.eq(FormUseTimeEntity::getApplicationId, applicationId);
        queryWrapper.last(" limit 1");
        FormUseTimeEntity formUseTimeEntity = formUseTimeMapper.selectOne(queryWrapper);
        if (formUseTimeEntity != null) {
            formUseTimeEntity.setUseTime(new Date());
            formUseTimeEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            formUseTimeMapper.updateById(formUseTimeEntity);
        } else {
            formUseTimeEntity = new FormUseTimeEntity();
            formUseTimeEntity.setUserId(UserUtils.getUser().getUserId());
            formUseTimeEntity.setFormId(formId);
            formUseTimeEntity.setUseTime(new Date());
            formUseTimeEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            formUseTimeEntity.setApplicationId(applicationId);
            formUseTimeMapper.insert(formUseTimeEntity);
        }
    }

    @Override
    public List<FormUseTimeEntity> getLatestForm(Integer limitCount) {
        LambdaQueryWrapper<FormUseTimeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormUseTimeEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(FormUseTimeEntity::getUserId, UserUtils.getUser().getUserId());
        queryWrapper.orderByDesc(FormUseTimeEntity::getUseTime);
        queryWrapper.last(" limit " + limitCount);
        queryWrapper.groupBy(FormUseTimeEntity::getFormId);
        queryWrapper.groupBy(FormUseTimeEntity::getApplicationId);
        return formUseTimeMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteByForm(String formId, String applicationId) {
        LambdaQueryWrapper<FormUseTimeEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FormUseTimeEntity::getFormId, formId);
        deleteWrapper.eq(FormUseTimeEntity::getApplicationId, applicationId);
        formUseTimeMapper.delete(deleteWrapper);
    }
}
