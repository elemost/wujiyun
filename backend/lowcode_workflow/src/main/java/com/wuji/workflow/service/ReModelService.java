package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.entity.ReModelEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-05-18
 */
public interface ReModelService extends IService<ReModelEntity> {
    Map<String,String> getByDeploymentId(List<String> deploymentIds);
}
