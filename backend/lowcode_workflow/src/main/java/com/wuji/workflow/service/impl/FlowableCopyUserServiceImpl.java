package com.wuji.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.mapper.FlowableCopyUserMapper;
import com.wuji.workflow.model.entity.FlowableCopyUserEntity;
import com.wuji.workflow.service.FlowableCopyUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 抄送对象表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
@Service
public class FlowableCopyUserServiceImpl extends ServiceImpl<FlowableCopyUserMapper, FlowableCopyUserEntity>
        implements FlowableCopyUserService {

    @Autowired
    private FlowableCopyUserMapper flowableCopyUserMapper;

    @Override
    public void create(Long copyId, List<Long> userIdList) {
        List<FlowableCopyUserEntity> flowableCopyUserEntityList = new ArrayList<>();
        for (Long userId : userIdList) {
            FlowableCopyUserEntity flowableCopyUserEntity = new FlowableCopyUserEntity();
            flowableCopyUserEntity.setCopyId(copyId);
            flowableCopyUserEntity.setUserId(userId);
            flowableCopyUserEntityList.add(flowableCopyUserEntity);
        }
        saveBatch(flowableCopyUserEntityList);
    }

    @Override
    public void view(String copyId) {
        LambdaQueryWrapper<FlowableCopyUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableCopyUserEntity::getCopyId, copyId);
        queryWrapper.eq(FlowableCopyUserEntity::getUserId, UserUtils.getUser().getUserId());
        FlowableCopyUserEntity flowableCopyUserEntity = new FlowableCopyUserEntity();
        flowableCopyUserEntity.setUserView(Boolean.TRUE);
        flowableCopyUserEntity.setViewTime(new Date());
        flowableCopyUserMapper.update(flowableCopyUserEntity, queryWrapper);
    }

    @Override
    public void clearByCopyId(List<Long> copyIdList) {
        if (CollectionUtils.isEmpty(copyIdList)) {
            return;
        }
        LambdaQueryWrapper<FlowableCopyUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowableCopyUserEntity::getCopyId, copyIdList);
        flowableCopyUserMapper.delete(queryWrapper);
    }
}
