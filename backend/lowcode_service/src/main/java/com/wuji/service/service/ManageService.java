package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ManageEntity;
import com.wuji.service.model.request.ManageCreateRequest;
import com.wuji.service.model.request.ManageUpdateRequest;
import com.wuji.service.model.request.MangeListRequest;
import com.wuji.service.model.vo.ManageVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-08
 */
public interface ManageService extends IService<ManageEntity> {
    void create(ManageCreateRequest manageCreateRequest);

    void update(ManageUpdateRequest manageUpdateRequest);

    List<ManageVO> manageList(MangeListRequest mangeListRequest);

    List<ManageVO> info(List<String> idList);

    void delete(String id);

    ManageVO currentUserInfo();

    Map<Long, ManageVO> userManegeMap(Long companyId);
}
