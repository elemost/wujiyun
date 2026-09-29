package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractUserQicodeLoginConverter;
import com.wuji.admin.mapper.UserQicodeLoginMapper;
import com.wuji.admin.model.entity.UserQicodeLoginEntity;
import com.wuji.admin.model.vo.UserQicodeLoginVO;
import com.wuji.admin.service.ThirdUserService;
import com.wuji.admin.service.UserQicodeLoginService;
import com.wuji.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户扫码登录表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Service
@DS("slave")
public class UserQicodeLoginServiceImpl extends ServiceImpl<UserQicodeLoginMapper, UserQicodeLoginEntity>
        implements UserQicodeLoginService {

    @Autowired
    private UserQicodeLoginMapper userQicodeLoginMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ThirdUserService thirdUserService;

    @Override
    public UserQicodeLoginVO getByTicket(String ticket) {
        LambdaQueryWrapper<UserQicodeLoginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQicodeLoginEntity::getTicket, ticket);
        queryWrapper.eq(UserQicodeLoginEntity::getUsed, 0);
        queryWrapper.last(" limit 1");
        UserQicodeLoginEntity userQicodeLoginEntity = userQicodeLoginMapper.selectOne(queryWrapper);
        return AbstractUserQicodeLoginConverter.INSTANCE.toVO(userQicodeLoginEntity);
    }

    @Override
    public void used(Long id) {
        UserQicodeLoginEntity userQicodeLoginEntity = new UserQicodeLoginEntity();
        userQicodeLoginEntity.setId(id);
        userQicodeLoginEntity.setUsed((byte) 1);
        userQicodeLoginMapper.updateById(userQicodeLoginEntity);
    }
}
