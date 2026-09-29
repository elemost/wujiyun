package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.admin.model.entity.NoticeEntity;

/**
 * <p>
 * 通知公告表 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */

@DS("slave")
public interface NoticeMapper extends BaseMapper<NoticeEntity> {

}
