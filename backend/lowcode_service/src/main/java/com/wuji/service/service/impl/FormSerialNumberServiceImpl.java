package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractFormSerialNumberConverter;
import com.wuji.service.mapper.FormSerialNumberMapper;
import com.wuji.service.model.entity.FormSerialNumberEntity;
import com.wuji.service.model.request.FormSerialNumberRequest;
import com.wuji.service.model.request.FormSerialNumberResetRequest;
import com.wuji.service.model.vo.FormSerialNumberVO;
import com.wuji.service.service.FormSerialNumberService;
import com.wuji.service.utils.SerialNumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-01
 */
@Service
public class FormSerialNumberServiceImpl extends ServiceImpl<FormSerialNumberMapper, FormSerialNumberEntity>
        implements FormSerialNumberService {

    @Autowired
    private FormSerialNumberMapper formSerialNumberMapper;

    @Override
    public Integer insertOrUpdate(String dateTimeKey, String serialKey, Integer initialValue) {
        LambdaQueryWrapper<FormSerialNumberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormSerialNumberEntity::getDateTime, dateTimeKey);
        queryWrapper.eq(FormSerialNumberEntity::getSerialKey, serialKey);
        FormSerialNumberEntity formSerialNumberEntity = formSerialNumberMapper.selectOne(queryWrapper);
        if (formSerialNumberEntity == null) {
            formSerialNumberEntity = new FormSerialNumberEntity();
            formSerialNumberEntity.setDateTime(dateTimeKey);
            formSerialNumberEntity.setSerialKey(serialKey);
            formSerialNumberEntity.setSerialNumber(initialValue);
            try {
                formSerialNumberMapper.insert(formSerialNumberEntity);
                return initialValue;
            } catch (Exception e) {
                return null;
            }
        } else {
            LambdaQueryWrapper<FormSerialNumberEntity> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(FormSerialNumberEntity::getDateTime, formSerialNumberEntity.getDateTime());
            updateWrapper.eq(FormSerialNumberEntity::getSerialKey, formSerialNumberEntity.getSerialKey());
            updateWrapper.eq(FormSerialNumberEntity::getSerialNumber, formSerialNumberEntity.getSerialNumber());
            FormSerialNumberEntity update = new FormSerialNumberEntity();
            update.setSerialNumber(formSerialNumberEntity.getSerialNumber() + 1);
            int value = formSerialNumberMapper.update(update, updateWrapper);
            if (value == 0) {
                return null;
            } else {
                return update.getSerialNumber();
            }
        }
    }

    @Override
    public void reset(FormSerialNumberResetRequest formSerialNumberResetRequest) {
        String dateTimeKey = SerialNumberUtils.getDateTimeKey(formSerialNumberResetRequest.getCycle());
        final String serialKey =
                formSerialNumberResetRequest.getApplicationId() + "_" + formSerialNumberResetRequest.getFormId() + "_" +
                        formSerialNumberResetRequest.getFieldId();
        LambdaQueryWrapper<FormSerialNumberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormSerialNumberEntity::getDateTime, dateTimeKey);
        queryWrapper.eq(FormSerialNumberEntity::getSerialKey, serialKey);
        formSerialNumberMapper.delete(queryWrapper);
    }

    @Override
    public FormSerialNumberVO getInfo(FormSerialNumberRequest formSerialNumberRequest) {
        String dateTimeKey = SerialNumberUtils.getDateTimeKey(formSerialNumberRequest.getCycle());
        final String serialKey =
                formSerialNumberRequest.getApplicationId() + "_" + formSerialNumberRequest.getFormId() + "_" +
                        formSerialNumberRequest.getFieldId();
        LambdaQueryWrapper<FormSerialNumberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormSerialNumberEntity::getDateTime, dateTimeKey);
        queryWrapper.eq(FormSerialNumberEntity::getSerialKey, serialKey);
        FormSerialNumberEntity formSerialNumberEntity = formSerialNumberMapper.selectOne(queryWrapper);
        return AbstractFormSerialNumberConverter.INSTANCE.toEntity(formSerialNumberEntity);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, Boolean needDate) {
        if (!needDate) {
            return;
        }
        LambdaQueryWrapper<FormSerialNumberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(FormSerialNumberEntity::getSerialKey, sourceApplicationId);
        queryWrapper.eq(FormSerialNumberEntity::getDateTime, "NEVER");
        List<FormSerialNumberEntity> formSerialNumberEntities = formSerialNumberMapper.selectList(queryWrapper);
        for (FormSerialNumberEntity formSerialNumberEntity : formSerialNumberEntities) {
            formSerialNumberEntity.setSerialKey(
                    formSerialNumberEntity.getSerialKey().replace(sourceApplicationId, applicationId));
        }
        saveBatch(formSerialNumberEntities);
    }
}
