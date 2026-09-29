package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CityEntity;
import com.wuji.admin.model.vo.CityVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-04
 */
public interface CityService extends IService<CityEntity> {
    List<CityVO> getList();
}
