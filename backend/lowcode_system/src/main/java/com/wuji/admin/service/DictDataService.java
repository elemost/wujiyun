package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.DictDataEntity;
import com.wuji.admin.model.vo.DictDataVO;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
public interface DictDataService extends IService<DictDataEntity> {
    List<DictDataVO> getByType(String type);
}
