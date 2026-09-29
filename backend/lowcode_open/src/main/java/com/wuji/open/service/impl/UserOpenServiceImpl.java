package com.wuji.open.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.UserMapper;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.IdUtils;
import com.wuji.common.utils.PasswordUtil;
import com.wuji.common.utils.UserUtils;
import com.wuji.open.converter.AbstractUserOpenConverter;
import com.wuji.open.model.request.UserDeleteOpenRequest;
import com.wuji.open.model.request.UserInfoRequest;
import com.wuji.open.model.request.UserOpenSaveRequest;
import com.wuji.open.model.request.UserOpenUpdateRequest;
import com.wuji.open.model.vo.UserOpenVO;
import com.wuji.open.service.UserOpenService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOpenServiceImpl implements UserOpenService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private UserService userService;


    @Override
    public void create(UserOpenSaveRequest userOpenSaveRequest) {
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userOpenSaveRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        UserEntity exist = userMapper.selectOne(sameWrapper);
        if (exist != null) {
            checkUserNameExist(userOpenSaveRequest.getPhonenumber(), exist.getUserId());
            createUserCompany(userOpenSaveRequest, exist);
            userDeptService.save(userOpenSaveRequest.getDeptIdList(), exist.getUserId());
            return;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setPhonenumber(userOpenSaveRequest.getPhonenumber());
        userEntity.setUserType("06");
        userEntity.setStatus(Constants.NORMAL);
        userEntity.setUuid(IdUtils.simpleUUID());
        userEntity.setUserType("06");
        userEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        userEntity.setUserName(userOpenSaveRequest.getPhonenumber());
        userEntity.setPassword(PasswordUtil.encryptPassword(userOpenSaveRequest.getPhonenumber()));
        if (CollectionUtils.isNotEmpty(userOpenSaveRequest.getDeptIdList())) {
            userEntity.setDeptId(userOpenSaveRequest.getDeptIdList().get(0));
        }
        userMapper.insert(userEntity);
        createUserCompany(userOpenSaveRequest, userEntity);
        userDeptService.save(userOpenSaveRequest.getDeptIdList(), userEntity.getUserId());
    }

    @Override
    public void update(UserOpenUpdateRequest userOpenUpdateRequest) {
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userOpenUpdateRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        UserEntity exist = userMapper.selectOne(sameWrapper);
        if (exist == null) {
            throw new AdminException(AdminResultCode.USER_NAME_NOT_EXIST);
        }
        userDeptService.save(userOpenUpdateRequest.getDeptIdList(), exist.getUserId());
        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setUserId(exist.getUserId());
        userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        userCompanySaveRequest.setNickName(userOpenUpdateRequest.getNickName());
        userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.FALSE);
    }

    @Override
    public UserOpenVO queryByPhone(UserInfoRequest userInfoRequest) {
        UserVO userVO = userService.queryCurrentCompanyUserByMobile(userInfoRequest.getPhonenumber());
        return AbstractUserOpenConverter.INSTANCE.toVO(userVO);
    }

    @Override
    public void delete(UserDeleteOpenRequest userInfoRequest) {
        UserVO userVO = userService.queryByMobile(userInfoRequest.getPhonenumber());
        userService.delete(userVO.getUuid());
    }

    private void createUserCompany(UserOpenSaveRequest userOpenSaveRequest, UserEntity userEntity) {
        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setUserId(userEntity.getUserId());
        userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        userCompanySaveRequest.setNickName(userOpenSaveRequest.getNickName());
        userCompanySaveRequest.setUserType("00");
        userCompanySaveRequest.setStatus(userEntity.getStatus());
        userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.TRUE);
    }

    private void checkUserNameExist(String phonenumber, Long userId) {
        LambdaQueryWrapper<UserEntity> checkUserNameWrapper = new LambdaQueryWrapper<>();
        checkUserNameWrapper.eq(UserEntity::getPhonenumber, phonenumber);
        checkUserNameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        checkUserNameWrapper.ne(UserEntity::getUserId, userId);
        UserEntity checkName = userMapper.selectOne(checkUserNameWrapper);
        if (checkName != null) {
            throw new AdminException(AdminResultCode.USER_NAME_EXIST);
        }
    }
}
