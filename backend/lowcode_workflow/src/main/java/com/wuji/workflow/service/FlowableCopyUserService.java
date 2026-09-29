package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.entity.FlowableCopyUserEntity;

import java.util.List;

/**
 * <p>
 * 抄送对象表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
public interface FlowableCopyUserService extends IService<FlowableCopyUserEntity> {
    void create(Long copyId, List<Long> userIdList);

    void view(String copyId);

    void clearByCopyId(List<Long> copyIdList);
}
