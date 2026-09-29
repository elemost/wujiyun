package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.constant.UserConstants;
import com.wuji.admin.converter.AbstractUserCompanyConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.UserStateEnum;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.UserCompanyMapper;
import com.wuji.admin.model.entity.UserCompanyEntity;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.common.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-10-28
 */
@Service
@DS("slave")
@Slf4j
public class UserCompanyServiceImpl extends ServiceImpl<UserCompanyMapper, UserCompanyEntity>
        implements UserCompanyService {

    @Autowired
    private UserCompanyMapper userCompanyMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UserService userService;

    @Autowired
    private WeComThirdService weComThirdService;

    @Override
    public Boolean checkAndSave(UserCompanySaveRequest userCompanySaveRequest, Boolean create) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getUserId, userCompanySaveRequest.getUserId());
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        UserCompanyEntity userCompanyEntity = userCompanyMapper.selectOne(queryWrapper);
        if (create) {
            if (StringUtils.isNotEmpty(userCompanySaveRequest.getInviteCode())) {
                // 说明在邀请
                if (userCompanyEntity != null) {
                    if (UserStateEnum.INVITE.getCode().equals(userCompanyEntity.getStatus())) {
                        return true;
                    }
                } else {
                    throw new AdminException(AdminResultCode.USER_NAME_EXIST);
                }
            } else {
                if (userCompanyEntity != null) {
                    throw new AdminException(AdminResultCode.USER_NAME_EXIST);
                }
            }
        }
        UserCompanyEntity userCompany = AbstractUserCompanyConverter.INSTANCE.toEntity(userCompanySaveRequest);
        if (userCompanyEntity != null) {
            userCompany.setId(userCompanyEntity.getId());
        } else {
            userCompany.setUserCompanyCode(userCompany.getUserId() + "_" + userCompany.getCompanyId());
        }
        saveOrUpdate(userCompany);
        return false;
    }

    @Override
    public void save(List<UserCompanySaveRequest> userCompanySaveRequestList) {

        if (CollectionUtils.isEmpty(userCompanySaveRequestList)) {
            return;
        }
        UserCompanySaveRequest first = userCompanySaveRequestList.get(0);
        String key = "user_company_" + first.getUserId();
        boolean lock = redisCache.lock(key, first.getUserId().toString(), 3);
        if (!lock) {
            throw new BizException(ResultCode.LOCK);
        }
        try {
            List<Long> userIdList = userCompanySaveRequestList.stream().map(UserCompanySaveRequest::getUserId)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserCompanyEntity::getUserId, userIdList);
            if (UserUtils.getUser() == null) {
                queryWrapper.eq(UserCompanyEntity::getCompanyId, first.getCompanyId());
            } else {
                queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
            }
            queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
            List<UserCompanyEntity> existList = userCompanyMapper.selectList(queryWrapper);
            Map<Long, UserCompanyEntity> userIdToMap =
                    existList.stream().collect(Collectors.toMap(UserCompanyEntity::getUserId, c -> c));
            List<UserCompanyEntity> userCompanyEntities = new ArrayList<>();
            for (UserCompanySaveRequest userCompanySaveRequest : userCompanySaveRequestList) {
                UserCompanyEntity entity = AbstractUserCompanyConverter.INSTANCE.toEntity(userCompanySaveRequest);
                UserCompanyEntity userCompanyEntity = userIdToMap.get(entity.getUserId());
                entity.setUserCompanyCode(entity.getUserId() + "_" + entity.getCompanyId());
                if (userCompanyEntity != null) {
                    entity.setId(userCompanyEntity.getId());
                }
                userCompanyEntities.add(entity);
            }

            saveOrUpdateBatch(userCompanyEntities);
        } catch (Exception e) {
            log.error("批量保存用户公司信息失败", e);
            throw new AdminException(AdminResultCode.IMPORT_LIST_ERROR);
        } finally {
            redisCache.deleteObject(key);
        }

    }

    @Override
    public void onlySaveBatch(List<UserCompanySaveRequest> userCompanySaveRequestList) {
        if (CollectionUtils.isEmpty(userCompanySaveRequestList)) {
            return;
        }
        UserCompanySaveRequest first = userCompanySaveRequestList.get(0);
        String key = "user_company_" + first.getUserId();
        boolean lock = redisCache.lock(key, first.getUserId().toString(), 3);
        if (!lock) {
            throw new BizException(ResultCode.LOCK);
        }
        try {
            List<UserCompanyEntity> userCompanyEntities = new ArrayList<>();
            for (UserCompanySaveRequest userCompanySaveRequest : userCompanySaveRequestList) {
                UserCompanyEntity entity = AbstractUserCompanyConverter.INSTANCE.toEntity(userCompanySaveRequest);
                entity.setUserCompanyCode(entity.getUserId() + "_" + entity.getCompanyId());
                userCompanyEntities.add(entity);
            }
            saveBatch(userCompanyEntities);
        } catch (Exception e) {
            throw new AdminException(AdminResultCode.IMPORT_LIST_ERROR);
        } finally {
            redisCache.deleteObject(key);
        }
    }

    @Override
    public void delete(Long companyId, List<String> dingUserId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.in(UserCompanyEntity::getDingThirdId, dingUserId);
        UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
        userCompanyEntity.setDelFlag(Constants.DELETED);
        userCompanyMapper.update(userCompanyEntity, queryWrapper);
    }

    @Override
    public void deleteByThirdId(Long companyId, String thirdType, List<String> thirdIdList) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getThirdType, thirdType);
        queryWrapper.in(UserCompanyEntity::getThirdId, thirdIdList);
        UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
        userCompanyEntity.setDelFlag(Constants.DELETED);
        userCompanyMapper.update(userCompanyEntity, queryWrapper);
    }

    @Override
    public void deleteByLarkUserId(Long companyId, List<String> userId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.in(UserCompanyEntity::getLarkUserId, userId);
        UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
        userCompanyEntity.setDelFlag(Constants.DELETED);
        userCompanyMapper.update(userCompanyEntity, queryWrapper);
    }

    @Override
    public List<UserCompanyVO> getByLarkUserId(List<String> larkUserIdlist) {
        if (CollectionUtils.isEmpty(larkUserIdlist)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.in(UserCompanyEntity::getLarkUserId, larkUserIdlist);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntityList = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntityList.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCompanyVO> getByDingTalkUserId(List<String> userId) {
        if (CollectionUtils.isEmpty(userId)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.in(UserCompanyEntity::getDingThirdId, userId);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntityList = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntityList.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCompanyVO> getByWeComUserId(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.in(UserCompanyEntity::getWeComUserId, idList);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntityList = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntityList.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserCompanyVO getByCompanyIdAndSecret(String weComUserId, Long companyId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(UserCompanyEntity::getWeComUserId, weComUserId);
        return AbstractUserCompanyConverter.INSTANCE.toVO(userCompanyMapper.selectOne(queryWrapper));
    }

    @Override
    public List<Long> getUserCompany(Long userId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getUserId, userId);
        queryWrapper.eq(UserCompanyEntity::getUserType, UserTypeEnum.INTERNAL.getCode());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(UserCompanyEntity::getCompanyId).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<UserCompanyVO> getByUserIdList(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserCompanyEntity::getUserId, userIdList);
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCompanyVO> getByUserIdsWithoutCompany(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserCompanyEntity::getUserId, userIdList);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> deleteByUserId(Long userId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getUserId, userId);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        UserCompanyEntity exist = userCompanyMapper.selectOne(queryWrapper);
        if (exist.getAdminUser()) {
            throw new AdminException(AdminResultCode.ADMIN_DELETE_FAIL);
        }
        UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
        userCompanyEntity.setDelFlag(Constants.DELETED);
        userCompanyMapper.update(userCompanyEntity, queryWrapper);
        return getUserCompany(userId);
    }

    @Override
    public List<UserCompanyVO> getByThirdType(Long companyId, String thirdType) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getThirdType, thirdType);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<UserCompanyVO> getAllUser() {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, UserCompanyVO> getAllUserWithDelete() {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        Map<Long, UserCompanyVO> userNameMap = new HashMap<>();
        List<UserCompanyVO> allUser = userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
        for (UserCompanyVO userCompanyVO : allUser) {
            userNameMap.put(userCompanyVO.getUserId(), userCompanyVO);
        }
        return userNameMap;
    }

    @Override
    public Long getAdmin(Long companyId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getAdminUser, Boolean.TRUE);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Boolean.FALSE);
        UserCompanyEntity userCompanyEntity = userCompanyMapper.selectOne(queryWrapper);
        if (userCompanyEntity != null) {
            return userCompanyEntity.getUserId();
        }
        return null;
    }

    @Override
    public List<UserCompanyVO> getAdminList(List<Long> companyIdList) {
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserCompanyEntity::getCompanyId, companyIdList);
        queryWrapper.eq(UserCompanyEntity::getAdminUser, Boolean.TRUE);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Boolean.FALSE);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void setAdmin(Long companyId, Long userId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NORMAL);
        Long count = userCompanyMapper.selectCount(queryWrapper);
        if (count == null || count == 1) {
            queryWrapper.eq(UserCompanyEntity::getUserId, userId);
            UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
            userCompanyEntity.setAdminUser(Boolean.TRUE);
            userCompanyMapper.update(userCompanyEntity, queryWrapper);
        }
    }

    @Override
    public Long userCount(Long companyId, String userType) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, 0);
        queryWrapper.eq(StringUtils.isNotEmpty(userType), UserCompanyEntity::getUserType, userType);
        Long count = userCompanyMapper.selectCount(queryWrapper);
        if (count == null) {
            return 0L;
        }
        return count;
    }

    @Override
    public UserCompanyVO getByUserIdAndCompanyId(Long userId, Long companyId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getUserId, userId);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        return AbstractUserCompanyConverter.INSTANCE.toVO(userCompanyMapper.selectOne(queryWrapper));
    }

    @Override
    public List<UserCompanyVO> getBySourceCompany(Long sourceCompanyId) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getSourceCompanyId, sourceCompanyId);
        queryWrapper.eq(UserCompanyEntity::getUserId, UserUtils.getUser().getUserId());
        // queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, UserConstants.NORMAL);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        return userCompanyEntities.stream().map(AbstractUserCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void bindWeComId(List<Long> userIdList) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(UserCompanyEntity::getDelFlag, "0");
        queryWrapper.in(UserCompanyEntity::getUserId, userIdList);
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper.selectList(queryWrapper);
        List<UserVO> userVOS = userService.infoOnly(userIdList);
        Map<Long, String> collect =
                userVOS.stream().collect(Collectors.toMap(UserVO::getUserId, UserVO::getPhonenumber));
        List<String> errorList = new ArrayList<>();
        for (UserCompanyEntity userCompanyEntity : userCompanyEntities) {
            if (StringUtils.isNotEmpty(userCompanyEntity.getWeComUserId())) {
                continue;
            }
            String phone = collect.get(userCompanyEntity.getUserId());
            if (phone != null) {
                String thirdId = weComThirdService.getThirdId(phone);
                if (thirdId == null) {
                    errorList.add(userCompanyEntity.getNickName());
                }
                userCompanyEntity.setWeComUserId(thirdId);
                userCompanyEntity.setThirdType(CompanyDataSourceEnum.WECOM_THIRD.name());
                userCompanyEntity.setThirdId(thirdId);
                userCompanyMapper.updateById(userCompanyEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(errorList)) {
            throw new AdminException(AdminResultCode.PHONE_NO_AUTH_OR_NOT_EXIST, StringUtils.join(errorList, "，"));
        }
    }

    @Override
    public UserCompanyVO getByJobNumber(Long companyId, String jobNumber) {
        LambdaQueryWrapper<UserCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCompanyEntity::getCompanyId, companyId);
        queryWrapper.eq(UserCompanyEntity::getJobNumber, jobNumber);
        queryWrapper.eq(UserCompanyEntity::getDelFlag, Constants.NU_DELETED);
        return AbstractUserCompanyConverter.INSTANCE.toVO(userCompanyMapper.selectOne(queryWrapper));
    }
}
