package com.wuji.workflow.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.UserService;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.converter.FlowableOperateLogConverter;
import com.wuji.workflow.enums.FlowCommentEnum;
import com.wuji.workflow.mapper.FlowableOperateLogMapper;
import com.wuji.workflow.model.entity.FlowableOperateLogEntity;
import com.wuji.workflow.model.request.FlowableOperateLogRequest;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;
import com.wuji.workflow.service.FlowableOperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-11-25
 */
@Service
public class FlowableOperateLogServiceImpl extends ServiceImpl<FlowableOperateLogMapper, FlowableOperateLogEntity>
        implements FlowableOperateLogService {

    @Autowired
    private FlowableOperateLogMapper flowableOperateLogMapper;

    @Autowired
    private UserService userService;

    @Override
    public void saveLog(FlowableOperateLogRequest flowableOperateLogRequest) {
        FlowableOperateLogEntity flowableOperateLogEntity =
                FlowableOperateLogConverter.INSTANCE.toEntity(flowableOperateLogRequest);
        flowableOperateLogEntity.setCreator(UserUtils.getUser().getUserId());
        flowableOperateLogEntity.setDuration(DateUtil.formatBetween(
                (flowableOperateLogEntity.getCreateTime().getTime() -
                        flowableOperateLogEntity.getTaskCreateTime().getTime()), BetweenFormatter.Level.SECOND));
        flowableOperateLogMapper.insert(flowableOperateLogEntity);
    }

    @Override
    public List<FlowableOperateLogVO> queryByInstanceId(String processInstanceId) {
        LambdaQueryWrapper<FlowableOperateLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowableOperateLogEntity::getProcessInstanceId, processInstanceId);
        queryWrapper.orderByDesc(FlowableOperateLogEntity::getCreateTime);
        List<FlowableOperateLogEntity> flowableOperateLogEntities = flowableOperateLogMapper.selectList(queryWrapper);
        List<Long> creator =
                flowableOperateLogEntities.stream().map(c -> Long.valueOf(c.getCreator())).collect(Collectors.toList());
        Map<Long, String> idToNameMap = userService.getIdToNameMap(creator);
        List<FlowableOperateLogVO> flowableOperateLogVOS = new ArrayList<>();
        for (FlowableOperateLogEntity flowableOperateLogEntity : flowableOperateLogEntities) {
            FlowableOperateLogVO flowableOperateLogVO =
                    FlowableOperateLogConverter.INSTANCE.toVO(flowableOperateLogEntity);
            flowableOperateLogVO.setCreatorName(idToNameMap.get(Long.valueOf(flowableOperateLogEntity.getCreator())));
            flowableOperateLogVO.setOperateName(FlowCommentEnum.getRemarkByType(flowableOperateLogVO.getOperate()));
            flowableOperateLogVO.setEnd(Boolean.TRUE);
            flowableOperateLogVO.setActivityName(flowableOperateLogVO.getTaskName());
            flowableOperateLogVOS.add(flowableOperateLogVO);
        }
        return flowableOperateLogVOS;
    }
}
