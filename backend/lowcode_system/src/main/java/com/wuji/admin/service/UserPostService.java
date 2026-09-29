package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserPostEntity;
import com.wuji.admin.model.request.UserPostCreateRequest;
import com.wuji.common.model.vo.UserPostVO;

import java.util.List;

/**
 * <p>
 * 用户与岗位关联表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
public interface UserPostService extends IService<UserPostEntity> {
    void save(Long userId, List<Long> postId);

    void save(List<UserPostCreateRequest> userPostList, List<Long> userIdList);

    List<UserPostVO> getListByUserIdList(List<Long> userIdList);

    List<Long> getUserIdByPostIdList(List<Long> postIdList);

    List<Long> getCurrentUserPostIdList();
}
