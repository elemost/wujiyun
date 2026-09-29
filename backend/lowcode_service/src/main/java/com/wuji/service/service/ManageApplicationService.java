package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ManageApplicationEntity;
import com.wuji.service.model.vo.ManageApplicationVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
public interface ManageApplicationService extends IService<ManageApplicationEntity> {
    void delete(String groupId);

    void update(String groupId, List<String> applicationIdList);

    List<ManageApplicationVO> getByGroupIds(List<String> groupId);

    List<String> getByApplicationIdList(List<String> applicationList);
}
