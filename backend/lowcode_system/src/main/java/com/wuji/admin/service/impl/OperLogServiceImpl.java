package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractOperLogConverter;
import com.wuji.admin.mapper.OperLogMapper;
import com.wuji.admin.model.domain.OperLogDomain;
import com.wuji.admin.model.entity.OperLogEntity;
import com.wuji.admin.service.OperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-23
 */
@Service
@DS("slave")
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLogEntity> implements OperLogService {

    @Autowired
    private OperLogMapper operLogMapper;

    @Override
    public void save(OperLogDomain operLogDomain) {
        final OperLogEntity operLogEntity = AbstractOperLogConverter.INSTANCE.toEntity(operLogDomain);
        operLogMapper.insert(operLogEntity);
    }
}
