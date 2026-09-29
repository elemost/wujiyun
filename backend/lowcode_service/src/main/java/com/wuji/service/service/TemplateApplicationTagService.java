package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateApplicationTagEntity;
import com.wuji.service.model.request.TemplateApplicationTagRequest;
import com.wuji.service.model.vo.TemplateApplicationTagVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-03
 */
public interface TemplateApplicationTagService extends IService<TemplateApplicationTagEntity> {
    List<TemplateApplicationTagVO> getByApplicationIdList(List<String> applicationIdList);

    void saveAll(List<TemplateApplicationTagRequest> templateApplicationTagEntityList, List<String> applicationIdList);
}
