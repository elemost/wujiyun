package com.wuji.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.workflow.mapper.ReModelMapper;
import com.wuji.workflow.model.entity.ReModelEntity;
import com.wuji.workflow.service.ReModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-05-18
 */
@Service
public class ReModelServiceImpl extends ServiceImpl<ReModelMapper, ReModelEntity> implements ReModelService {

    @Autowired
    private ReModelMapper reModelMapper;
    @Override
    public Map<String, String> getByDeploymentId(List<String> deploymentIds) {
        LambdaQueryWrapper<ReModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ReModelEntity::getDeploymentId, deploymentIds);
        List<ReModelEntity> list = reModelMapper.getByDeploymentId(queryWrapper);
        return list.stream().collect(Collectors.toMap(ReModelEntity::getDeploymentId, ReModelEntity::getId));
    }
}
