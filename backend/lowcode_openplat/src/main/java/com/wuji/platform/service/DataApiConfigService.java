package com.wuji.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.platform.model.entity.DataApiConfigEntity;
import com.wuji.platform.model.request.DataApiConfigCreateRequest;
import com.wuji.platform.model.request.DataApiConfigRequest;
import com.wuji.platform.model.request.DataApiConfigUpdateRequest;
import com.wuji.platform.model.vo.DataApiConfigVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
public interface DataApiConfigService extends IService<DataApiConfigEntity> {
    String create(DataApiConfigCreateRequest dataApiConfigCreateRequest);

    void update(DataApiConfigUpdateRequest dataApiConfigUpdateRequest);

    DataApiConfigVO info(String id);

    List<DataApiConfigVO> queryList(DataApiConfigRequest dataApiConfigRequest);

    void delete(String id);
}
