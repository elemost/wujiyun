package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TemplateApplicationImgEntity;
import com.wuji.service.model.request.TemplateApplicationImgRequest;
import com.wuji.service.model.vo.TemplateApplicationImgVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
public interface TemplateApplicationImgService extends IService<TemplateApplicationImgEntity> {
    List<TemplateApplicationImgVO> listById(String applicationId);

    void saveAll(List<TemplateApplicationImgRequest>templateApplicationImgRequestList, List<String> applicationId);
}
