package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.entity.FlowableOperateLogEntity;
import com.wuji.workflow.model.request.FlowableOperateLogRequest;
import com.wuji.workflow.model.vo.FlowableOperateLogVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-11-25
 */
public interface FlowableOperateLogService extends IService<FlowableOperateLogEntity> {
    void saveLog(FlowableOperateLogRequest flowableOperateLogRequest);

    List<FlowableOperateLogVO> queryByInstanceId(String processInstanceId);
}
