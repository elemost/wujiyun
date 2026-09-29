package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractThirdUserConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.ThirdUserMapper;
import com.wuji.admin.model.entity.ThirdUserEntity;
import com.wuji.admin.model.vo.ThirdUserVO;
import com.wuji.admin.service.ThirdUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Service
@Slf4j
@DS("slave")
public class ThirdUserServiceImpl extends ServiceImpl<ThirdUserMapper, ThirdUserEntity> implements ThirdUserService {

    @Autowired
    private ThirdUserMapper thirdUserMapper;


    @Override
    public ThirdUserVO getByOpenId(String openId, Short thirdType) {
        LambdaQueryWrapper<ThirdUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThirdUserEntity::getOpenId, openId);
        queryWrapper.eq(ThirdUserEntity::getThirdType, thirdType);
        ThirdUserEntity thirdUserEntity = thirdUserMapper.selectOne(queryWrapper);
        return AbstractThirdUserConverter.INSTANCE.toVO(thirdUserEntity);
    }

    @Override
    public ThirdUserVO getByUnionId(String unionId, Short thirdType) {
        LambdaQueryWrapper<ThirdUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThirdUserEntity::getOpenId, unionId);
        queryWrapper.eq(ThirdUserEntity::getThirdType, thirdType);
        ThirdUserEntity thirdUserEntity = thirdUserMapper.selectOne(queryWrapper);
        return AbstractThirdUserConverter.INSTANCE.toVO(thirdUserEntity);
    }

    @Override
    public void saveOpenId(Long userId, String openId, Short thirdType, String unionId) {
        LambdaQueryWrapper<ThirdUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThirdUserEntity::getUserId, userId);
        queryWrapper.eq(ThirdUserEntity::getThirdType, thirdType);
        ThirdUserEntity checkBind = thirdUserMapper.selectOne(queryWrapper);
        if (checkBind != null) {
            throw new AdminException(AdminResultCode.MOBILE_BIND);
        }
        ThirdUserVO thirdUser = getByOpenId(openId, thirdType);
        if (thirdUser == null) {
            ThirdUserEntity thirdUserSave = new ThirdUserEntity();
            thirdUserSave.setUserId(userId);
            thirdUserSave.setOpenId(openId);
            thirdUserSave.setThirdType(thirdType);
            thirdUserSave.setUnionId(unionId);
            thirdUserMapper.insert(thirdUserSave);
        }
    }

}
