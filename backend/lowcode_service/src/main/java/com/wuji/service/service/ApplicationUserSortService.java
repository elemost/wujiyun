package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationUserSortEntity;
import com.wuji.service.model.request.ApplicationUserSortSaveRequest;
import com.wuji.service.model.vo.ApplicationUserSortVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-02
 */
public interface ApplicationUserSortService extends IService<ApplicationUserSortEntity> {
    void saveSort(ApplicationUserSortSaveRequest applicationUserSortSaveRequest);

    List<ApplicationUserSortVO> ownList();
}
