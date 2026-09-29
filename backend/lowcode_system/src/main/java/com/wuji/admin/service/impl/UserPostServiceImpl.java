package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.UserPostMapper;
import com.wuji.admin.model.entity.UserPostEntity;
import com.wuji.admin.model.request.UserPostCreateRequest;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserPostService;
import com.wuji.common.model.vo.UserPostVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户与岗位关联表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
@Service
@DS("slave")
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPostEntity> implements UserPostService {

    @Autowired
    private UserPostMapper userPostMapper;

    @Autowired
    private PostService postService;

    @Override
    public void save(Long userId, List<Long> postIdList) {
        LambdaQueryWrapper<UserPostEntity> delete = new LambdaQueryWrapper<>();
        delete.eq(UserPostEntity::getUserId, userId);
        delete.eq(UserPostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        userPostMapper.delete(delete);
        if (CollectionUtils.isEmpty(postIdList)) {
            return;
        }
        List<UserPostEntity> userPostEntityList = new ArrayList<>();
        for (Long postId : postIdList) {
            UserPostEntity userPostEntity = new UserPostEntity();
            userPostEntity.setPostId(postId);
            userPostEntity.setUserId(userId);
            userPostEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userPostEntityList.add(userPostEntity);
        }
        saveBatch(userPostEntityList);
    }

    @Override
    public void save(List<UserPostCreateRequest> userPostList, List<Long> userIdList) {
        LambdaQueryWrapper<UserPostEntity> delete = new LambdaQueryWrapper<>();
        delete.in(UserPostEntity::getUserId, userIdList);
        delete.eq(UserPostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        userPostMapper.delete(delete);
        if (CollectionUtils.isEmpty(userPostList)) {
            return;
        }
        List<UserPostEntity> userPostEntityList = new ArrayList<>();
        for (UserPostCreateRequest userPostCreateRequest : userPostList) {
            UserPostEntity userPostEntity = new UserPostEntity();
            userPostEntity.setPostId(userPostCreateRequest.getPostId());
            userPostEntity.setUserId(userPostCreateRequest.getUserId());
            userPostEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userPostEntityList.add(userPostEntity);
        }
        saveBatch(userPostEntityList);
    }

    @Override
    public List<UserPostVO> getListByUserIdList(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserPostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserPostEntity::getUserId, userIdList);
        queryWrapper.eq(UserPostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserPostEntity> userPostEntityList = userPostMapper.selectList(queryWrapper);
        List<Long> postIdList = userPostEntityList.stream().map(UserPostEntity::getPostId).collect(Collectors.toList());
        Map<Long, String> postMap = postService.getListByIdList(postIdList);
        List<UserPostVO> userPostVOList = new ArrayList<>();
        for (UserPostEntity userPostEntity : userPostEntityList) {
            UserPostVO userPostVO = new UserPostVO();
            userPostVO.setUserId(userPostEntity.getUserId());
            userPostVO.setPostId(userPostEntity.getPostId());
            userPostVO.setPostName(postMap.get(userPostEntity.getPostId()));
            userPostVOList.add(userPostVO);
        }
        return userPostVOList;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Long> getUserIdByPostIdList(List<Long> postIdList) {
        if (CollectionUtils.isEmpty(postIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserPostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserPostEntity::getPostId, postIdList);
        queryWrapper.eq(UserPostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserPostEntity> userPostEntityList = userPostMapper.selectList(queryWrapper);
        return userPostEntityList.stream().map(UserPostEntity::getUserId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getCurrentUserPostIdList() {
        if (CollectionUtils.isEmpty(UserUtils.getUser().getPostIdList())) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserPostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserPostEntity::getPostId, UserUtils.getUser().getPostIdList());
        queryWrapper.eq(UserPostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserPostEntity> userPostEntityList = userPostMapper.selectList(queryWrapper);
        return userPostEntityList.stream().map(UserPostEntity::getUserId).collect(Collectors.toList());
    }
}
