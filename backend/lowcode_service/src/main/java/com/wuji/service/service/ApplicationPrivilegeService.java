package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationPrivilegeEntity;
import com.wuji.service.model.request.ApplicationPrivilegeSaveRequest;
import com.wuji.service.model.vo.ApplicationPrivilegeVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-10-14
 */
public interface ApplicationPrivilegeService extends IService<ApplicationPrivilegeEntity> {
    void save(String applicationId, String privilegeType,
              List<ApplicationPrivilegeSaveRequest> applicationPrivilegeSaveList);

    List<ApplicationPrivilegeVO> getPriviegeList(String applicationId);

    void saveDefaultPrivilege(String applicationId);

    void copy(String applicationId, String sourceApplicationId);

    List<Long> getScope(String applicationId);
}
