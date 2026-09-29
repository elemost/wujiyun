package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationShareEntity;
import com.wuji.service.model.request.ApplicationShareCreateRequest;
import com.wuji.service.model.vo.ApplicationShareVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-05-09
 */
public interface ApplicationShareService extends IService<ApplicationShareEntity> {
    String createShare(ApplicationShareCreateRequest applicationShareCreateRequest);

    ApplicationShareVO info(String shareId);
}
