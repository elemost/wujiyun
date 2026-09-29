package com.wuji.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.workflow.mapper.HiIdentitylinkMapper;
import com.wuji.workflow.model.entity.HiIdentitylinkEntity;
import com.wuji.workflow.service.HiIdentitylinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-25
 */
@Service
public class HiIdentitylinkServiceImpl extends ServiceImpl<HiIdentitylinkMapper, HiIdentitylinkEntity>
        implements HiIdentitylinkService {

    @Autowired
    private HiIdentitylinkMapper hiIdentitylinkMapper;

    @Override
    public List<HiIdentitylinkEntity> getByTaskIdList(List<String> taskIdList) {
        LambdaQueryWrapper<HiIdentitylinkEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(HiIdentitylinkEntity::getTaskId, taskIdList);
        return hiIdentitylinkMapper.selectList(queryWrapper);
    }
}
