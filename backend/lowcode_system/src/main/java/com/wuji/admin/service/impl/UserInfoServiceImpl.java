package com.wuji.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractUserInfoConverter;
import com.wuji.admin.mapper.UserInfoMapper;
import com.wuji.admin.model.entity.UserInfoEntity;
import com.wuji.admin.model.request.UserInfoSaveRequest;
import com.wuji.admin.service.UserInfoService;
import com.wuji.common.model.vo.UserInfoVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-13
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(Long userId, List<UserInfoSaveRequest> userInfoSaveList) {
        if (CollectionUtils.isEmpty(userInfoSaveList)) {
            return;
        }
        List<String> userInfoKey =
                userInfoSaveList.stream().map(UserInfoSaveRequest::getInfoKey).collect(Collectors.toList());
        LambdaQueryWrapper<UserInfoEntity> delete = new LambdaQueryWrapper<>();
        delete.eq(UserInfoEntity::getUserId, userId);
        delete.eq(UserInfoEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        delete.in(UserInfoEntity::getInfoKey, userInfoKey);
        userInfoMapper.delete(delete);

        List<UserInfoEntity> userInfoEntities = new ArrayList<>();
        for (UserInfoSaveRequest userInfoSaveRequest : userInfoSaveList) {
            if (StringUtils.isEmpty(userInfoSaveRequest.getInfoValue())) {
                continue;
            }
            UserInfoEntity userInfoEntity = AbstractUserInfoConverter.INSTANCE.toEntity(userInfoSaveRequest);
            userInfoEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userInfoEntity.setUserId(userId);
            userInfoEntities.add(userInfoEntity);
        }
        saveBatch(userInfoEntities);
    }

    @Override
    public void save(List<UserInfoSaveRequest> userInfoSaveList) {
        List<Long> userIdList =
                userInfoSaveList.stream().map(UserInfoSaveRequest::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        LambdaQueryWrapper<UserInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfoEntity::getUserId, userIdList);
        queryWrapper.eq(UserInfoEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserInfoEntity> userInfoEntityList = userInfoMapper.selectList(queryWrapper);
        Map<String, UserInfoEntity> userKeyMap = userInfoEntityList.stream()
                .collect(Collectors.toMap(c -> c.getUserId() + "_" + c.getInfoKey(), c -> c));
        List<UserInfoEntity> userInfoEntities = new ArrayList<>();
        for (UserInfoSaveRequest userInfoSaveRequest : userInfoSaveList) {
            String userKey = userInfoSaveRequest.getUserId() + "_" + userInfoSaveRequest.getInfoKey();
            UserInfoEntity exist = userKeyMap.get(userKey);
            UserInfoEntity userInfoEntity = AbstractUserInfoConverter.INSTANCE.toEntity(userInfoSaveRequest);
            userInfoEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            if (exist != null) {
                userInfoEntity.setId(exist.getId());
            }
            userInfoEntities.add(userInfoEntity);
        }
        saveOrUpdateBatch(userInfoEntities);
    }

    @Override
    public List<UserInfoVO> getByUserId(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserInfoEntity::getUserId, userIdList);
        queryWrapper.eq(UserInfoEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserInfoEntity> userInfoEntities = userInfoMapper.selectList(queryWrapper);
        return userInfoEntities.stream().map(AbstractUserInfoConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
