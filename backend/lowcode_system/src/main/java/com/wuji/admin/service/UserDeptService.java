package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserDeptEntity;
import com.wuji.admin.model.request.UserDeptSaveRequest;
import com.wuji.common.model.vo.UserDeptVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-11
 */
public interface UserDeptService extends IService<UserDeptEntity> {
    void save(List<Long> deptId, Long userId);

    void save(List<UserDeptSaveRequest> deptIdList, List<Long> userIdList);

    List<UserDeptVO> getByUserIdList(List<Long> userIdList, Long companyId);

    List<Long> getByDeptIdList(List<Long> deptIdList);

    void delete(Long userId);

}
