package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.SmsTemplateMapper;
import com.wuji.admin.model.entity.SmsTemplateEntity;
import com.wuji.admin.service.SmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 五极短信模板 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Service
@DS("slave")
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplateEntity>
        implements SmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateMapper;

    @Override
    public SmsTemplateEntity getBySmsScene(String smsScene) {
        LambdaQueryWrapper<SmsTemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsTemplateEntity::getSmsScene, smsScene);
        return smsTemplateMapper.selectOne(queryWrapper);
    }
}
