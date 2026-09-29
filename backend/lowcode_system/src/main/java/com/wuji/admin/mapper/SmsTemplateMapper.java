package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.SmsTemplateEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 五极短信模板 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@DS("slave")
@Repository
public interface SmsTemplateMapper extends BaseMapper<SmsTemplateEntity> {

}
