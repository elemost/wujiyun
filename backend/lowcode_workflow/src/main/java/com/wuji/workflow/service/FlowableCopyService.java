package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.workflow.model.entity.FlowableCopyEntity;
import com.wuji.workflow.model.request.FlowableCopyCreateRequest;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.vo.FlowableCopyVO;

/**
 * <p>
 * 抄送表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
public interface FlowableCopyService extends IService<FlowableCopyEntity> {
    /**
     * 新增抄送配置
     * @param flowableCopyCreateRequest
     */
    void create(FlowableCopyCreateRequest flowableCopyCreateRequest);

    /**
     * 获取抄送流程列表
     * @param flowableCopyRequest
     * @return
     */
    QueryPageVO<FlowableCopyVO> queryList(FlowableCopyRequest flowableCopyRequest);

    void clear(String applicationId);

    void clear(String applicationId, String formId);

}
