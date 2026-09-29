package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.entity.FlowableConfigEntity;
import com.wuji.workflow.model.vo.FlowableConfigVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-24
 */
public interface FlowableConfigService extends IService<FlowableConfigEntity> {
    void save(String modelId, FormModelDesignerDomain formModelDesignerDomain);

    void delete(List<String> modelIdList);

    FlowableConfigVO getByModelId(String modelId);
}
