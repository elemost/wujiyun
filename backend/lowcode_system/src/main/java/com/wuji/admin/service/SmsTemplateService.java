package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.SmsTemplateEntity;

/**
 * <p>
 * 五极短信模板 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
public interface SmsTemplateService extends IService<SmsTemplateEntity> {

    SmsTemplateEntity getBySmsScene(String smsScene);

}
