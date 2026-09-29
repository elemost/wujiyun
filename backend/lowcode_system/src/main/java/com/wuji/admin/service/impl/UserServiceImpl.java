package com.wuji.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractUserCompanyConverter;
import com.wuji.admin.converter.AbstractUserConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyChannelTypeEnum;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.CompanyInfoKeyEnum;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.enums.RosterEnum;
import com.wuji.admin.enums.UserScopeEnum;
import com.wuji.admin.enums.UserStateEnum;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.handler.PullDataContext;
import com.wuji.admin.mapper.UserMapper;
import com.wuji.admin.model.domain.UserExcelDomain;
import com.wuji.admin.model.domain.WeComDomain;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.info.UserScope;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.model.request.UserCompleteRequest;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserDeptSaveRequest;
import com.wuji.admin.model.request.UserInfoSaveRequest;
import com.wuji.admin.model.request.UserIntentionRequest;
import com.wuji.admin.model.request.UserInviteRequest;
import com.wuji.admin.model.request.UserPhoneUpdateRequest;
import com.wuji.admin.model.request.UserPostCreateRequest;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.request.UserUpdatePasswordRequest;
import com.wuji.admin.model.request.UserUpdateRequest;
import com.wuji.admin.model.request.UserWeComCompleteRequest;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.UserImportErrorVO;
import com.wuji.admin.model.vo.UserImportVO;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserInfoService;
import com.wuji.admin.service.UserIntentionService;
import com.wuji.admin.service.UserPostService;
import com.wuji.admin.service.UserRoleService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.model.vo.UserInfoVO;
import com.wuji.common.model.vo.UserPostVO;
import com.wuji.common.model.vo.UserRoleVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.service.ManageCommonService;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.IdUtils;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.PasswordUtil;
import com.wuji.common.utils.SmsCodeUtils;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
@Service
@DS("slave")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserDeptService userDeptService;

    @Autowired
    private UserPostService userPostService;

    @Autowired
    private PostService postService;

    @Autowired
    private PullDataContext pullDataContext;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserIntentionService userIntentionService;

    @Autowired
    private CompanyAppService companyAppService;

    @Autowired
    private ManageCommonService manageCommonService;

    @Autowired
    private SmsCodeUtils smsCodeUtils;

    @Autowired
    private WeComThirdService weComThirdServiceImpl;

    @Override
    public QueryPageVO<UserReturnVO> queryList(UserRequest userRequest) {
        UserDomain user = UserUtils.getUser();
        QueryWrapper<UserEntity> queryWrapper = getUserEntityQueryWrapper(userRequest, user);
        final IPage<UserEntity> userEntityPage =
                userMapper.selectPages(new Page<>(userRequest.getPageNum(), userRequest.getPageSize()), queryWrapper);
        final List<UserEntity> userEntityList = userEntityPage.getRecords();
        if (CollectionUtils.isEmpty(userEntityList)) {
            return PageUtils.toQueryPage(userEntityPage, new ArrayList<>());
        }
        List<UserReturnVO> userVOList = buildUserListInfo(userEntityList);
        for (UserReturnVO userVO : userVOList) {
            userVO.setEncryptPhone(AESUtils.encrypt(Constants.PASSWORD_KEY, userVO.getPhonenumber()));
            userVO.setPhonenumber(StringUtil.maskPhoneNumber(userVO.getPhonenumber()));
        }
        return PageUtils.toQueryPage(userEntityPage, userVOList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public QueryPageVO<UserVO> querySelectList(UserSelectRequest userRequest) {
        UserDomain user = UserUtils.getUser();
        QueryWrapper<UserEntity> queryWrapper = getUserSelectQueryWrapper(userRequest, user);
        final IPage<UserEntity> userEntityPage =
                userMapper.selectPages(new Page<>(userRequest.getPageNum(), userRequest.getPageSize()), queryWrapper);
        final List<UserEntity> userEntityList = userEntityPage.getRecords();
        if (CollectionUtils.isEmpty(userEntityList)) {
            return PageUtils.toQueryPage(userEntityPage, new ArrayList<>());
        }
        List<UserVO> userVOList = buildUserInfo(userEntityList);
        for (UserVO userVO : userVOList) {
            userVO.setEncryptPhone(AESUtils.encrypt(Constants.PASSWORD_KEY, userVO.getPhonenumber()));
            userVO.setPhonenumber(null);
        }
        return PageUtils.toQueryPage(userEntityPage, userVOList);
    }

    private QueryWrapper<UserEntity> getUserSelectQueryWrapper(UserSelectRequest userRequest, UserDomain user) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.del_flag", Constants.NU_DELETED);
        if (CollectionUtils.isNotEmpty(userRequest.getDepartmentIdList())) {
            List<Long> allChildren =
                    DepartmentCache.getAllChildren(user.getCompanyId(), userRequest.getDepartmentIdList());
            userRequest.setDepartmentIdList(allChildren);
        }
        if (CollectionUtils.isNotEmpty(userRequest.getIdList()) ||
                CollectionUtils.isNotEmpty(userRequest.getPostIdList()) ||
                CollectionUtils.isNotEmpty(userRequest.getDepartmentIdList())) {
            queryWrapper.and(c -> c.in(CollectionUtils.isNotEmpty(userRequest.getPostIdList()), "up.post_id",
                            userRequest.getPostIdList()).or(CollectionUtils.isNotEmpty(userRequest.getIdList()),
                            d -> d.in("uc.user_id", userRequest.getIdList()))
                    .or(CollectionUtils.isNotEmpty(userRequest.getDepartmentIdList()),
                            d -> d.in("ud.dept_id", userRequest.getDepartmentIdList())));
        }

        if (CollectionUtils.isNotEmpty(userRequest.getPostIdManageList()) ||
                CollectionUtils.isNotEmpty(userRequest.getDepartmentIdManageList()) ||
                CollectionUtils.isNotEmpty(userRequest.getIdManageList())) {
            queryWrapper.and(c -> c.in(CollectionUtils.isNotEmpty(userRequest.getPostIdManageList()), "up.post_id",
                            userRequest.getPostIdList()).or(CollectionUtils.isNotEmpty(userRequest.getIdList()),
                            d -> d.in("uc.user_id", userRequest.getIdManageList()))
                    .or(CollectionUtils.isNotEmpty(userRequest.getDepartmentIdList()),
                            d -> d.in("ud.dept_id", userRequest.getDepartmentIdManageList())));
        }
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getPostId()), "up.post_id", userRequest.getPostId());
        if (userRequest.getDepartmentId() != null) {
            queryWrapper.in("ud.dept_id", DepartmentCache.getAllChildren(user.getCompanyId(),
                    Collections.singletonList(userRequest.getDepartmentId())));
        }
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getUserType()), "uc.user_type", userRequest.getUserType());
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getId()), "u.user_id", userRequest.getId());
        queryWrapper.eq("uc.del_flag", Constants.NU_DELETED);
        queryWrapper.eq("uc.company_id", user.getCompanyId());
        queryWrapper.like(StringUtils.isNotEmpty(userRequest.getStatus()), "u.status", userRequest.getStatus());
        queryWrapper.in(CollectionUtils.isNotEmpty(userRequest.getSourceCompanyId()), "uc.source_company_id",
                userRequest.getSourceCompanyId());
        queryWrapper.orderByDesc("u.user_id");
        queryWrapper.groupBy("u.user_id");
        return queryWrapper;
    }

    private QueryWrapper<UserEntity> getUserEntityQueryWrapper(UserRequest userRequest, UserDomain user) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(userRequest.getRealName()), "u.real_name", userRequest.getRealName());
        queryWrapper.like(StringUtils.isNotEmpty(userRequest.getNickName()), "uc.nick_name", userRequest.getNickName());
        queryWrapper.like(StringUtils.isNotEmpty(userRequest.getUserName()), "u.user_name", userRequest.getUserName());
        queryWrapper.like(StringUtils.isNotEmpty(userRequest.getPhonenumber()), "u.phonenumber",
                userRequest.getPhonenumber());
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getStatus()), "u.status", userRequest.getStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getDepartmentId()) &&
                !Objects.equals("0", userRequest.getDepartmentId()), "ud.dept_id", userRequest.getDepartmentId());
        queryWrapper.ge(userRequest.getUserType() != null, "uc.user_type", userRequest.getUserType());
        queryWrapper.ge(userRequest.getStartTime() != null, "uc.create_time", userRequest.getStartTime());
        queryWrapper.le(userRequest.getEndTime() != null, "uc.create_time", userRequest.getEndTime());
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getPostId()), "up.post_id", userRequest.getPostId());
        queryWrapper.eq(StringUtils.isNotEmpty(userRequest.getUserType()), "uc.user_type", userRequest.getUserType());
        queryWrapper.eq("u.del_flag", Constants.NU_DELETED);
        queryWrapper.eq("uc.del_flag", Constants.NU_DELETED);
        queryWrapper.eq("uc.company_id", user.getCompanyId());
        queryWrapper.in(CollectionUtils.isNotEmpty(userRequest.getSourceCompanyId()), "uc.source_company_id",
                userRequest.getSourceCompanyId());
        queryWrapper.orderByDesc("u.user_id");
        queryWrapper.groupBy("u.user_id");
        return queryWrapper;
    }


    private UserVO buildUserInfoWhileLogin(UserEntity userEntity) {
        UserVO userVO = buildUserInfo(Collections.singletonList(userEntity)).get(0);
        // List<DepartmentVO> departmentVOList = new ArrayList<>();
        // userVO.setDepartmentList(departmentVOList);
        CompanyVO info = companyService.info(userVO.getCompanyId());
        if (info != null) {
            userVO.setCompanyUuid(info.getCompanyUuid());
        }
        return userVO;
    }

    private List<UserReturnVO> buildUserListInfo(List<UserEntity> userEntityList) {
        List<UserReturnVO> userVOList = new ArrayList<>();
        List<Long> userIds = userEntityList.stream().map(UserEntity::getUserId).collect(Collectors.toList());
        Map<Long, List<UserRoleVO>> userIdRoleMap = userRoleService.getUserRoleByUserIdList(userIds).stream()
                .collect(Collectors.groupingBy(UserRoleVO::getUserId));
        List<UserDeptVO> userDeptVOList = userDeptService.getByUserIdList(userIds, null);
        Map<Long, List<UserDeptVO>> userDeptMap =
                userDeptVOList.stream().collect(Collectors.groupingBy(UserDeptVO::getUserId));
        List<UserPostVO> userPostVOList = userPostService.getListByUserIdList(userIds);
        Map<Long, List<UserPostVO>> userPostMap =
                userPostVOList.stream().collect(Collectors.groupingBy(UserPostVO::getUserId));
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(userIds);
        List<Long> sourceCompanyId =
                userCompanyVOS.stream().map(UserCompanyVO::getSourceCompanyId).collect(Collectors.toList());
        List<UserCompanyVO> adminList = userCompanyService.getAdminList(sourceCompanyId);
        List<String> adminUserList =
                adminList.stream().map(c -> c.getCompanyId() + "_" + c.getUserId()).collect(Collectors.toList());
        Map<Long, UserCompanyVO> userCompanyVOMap =
                userCompanyVOS.stream().collect(Collectors.toMap(UserCompanyVO::getUserId, c -> c));
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        for (UserEntity userEntity : userEntityList) {
            UserReturnVO userVO = AbstractUserConverter.INSTANCE.toReturnVO(userEntity);
            List<UserRoleVO> userRoleVOS = userIdRoleMap.get(userVO.getUserId());
            userVO.setRoles(userRoleVOS);
            if (CollectionUtils.isNotEmpty(userRoleVOS)) {
                userVO.setRoleIdList(userRoleVOS.stream().map(UserRoleVO::getRoleId).collect(Collectors.toList()));
            }
            List<UserDeptVO> userDeptVOS = userDeptMap.get(userEntity.getUserId());
            if (CollectionUtils.isNotEmpty(userDeptVOS)) {
                userVO.setDeptName(userDeptVOS.get(0).getDeptName());
            }
            if (CollectionUtils.isNotEmpty(userDeptVOS)) {
                userVO.setDeptIdList(userDeptVOS.stream().map(UserDeptVO::getDeptId).collect(Collectors.toList()));
            }
            List<UserPostVO> userPostVOS = userPostMap.get(userEntity.getUserId());
            if (CollectionUtils.isNotEmpty(userPostVOS)) {
                userVO.setPostIdList(userPostVOS.stream().map(UserPostVO::getPostId).collect(Collectors.toList()));
            }
            UserCompanyVO userCompanyVO = userCompanyVOMap.get(userEntity.getUserId());
            if (userCompanyVO != null) {
                userVO.setNickName(userCompanyVO.getNickName());
                userVO.setEmail(userCompanyVO.getEmail());
                userVO.setAdminUser(userCompanyVO.getAdminUser());
                userVO.setAvatar(userCompanyVO.getAvatar());
                userVO.setUserType(userCompanyVO.getUserType());
                userVO.setWeComThirdId(userCompanyVO.getWeComUserId());
                if (CompanyDataSourceEnum.LARK.name().equals(info.getDataSource())) {
                    if (StringUtils.isNotEmpty(userCompanyVO.getLarkUserId())) {
                        userVO.setDataSource(CompanyDataSourceEnum.LARK.name());
                    }
                } else if (CompanyDataSourceEnum.DING_TALK.name().equals(info.getDataSource())) {
                    if (StringUtils.isNotEmpty(userCompanyVO.getDingThirdId())) {
                        userVO.setDataSource(CompanyDataSourceEnum.DING_TALK.name());
                    }
                }
                if (adminUserList.contains(userCompanyVO.getSourceCompanyId() + "_" + userCompanyVO.getUserId())) {
                    userVO.setSourceCompanyAdmin(Boolean.TRUE);
                } else {
                    userVO.setSourceCompanyAdmin(Boolean.FALSE);
                }
            }
            userVO.setUserPostList(userPostVOS);
            userVO.setUserDeptList(userDeptVOS);
            userVOList.add(userVO);
        }
        return userVOList;
    }

    private List<UserVO> buildUserInfo(List<UserEntity> userEntityList) {
        List<UserVO> userVOList = new ArrayList<>();
        List<Long> userIds = userEntityList.stream().map(UserEntity::getUserId).collect(Collectors.toList());
        Map<Long, List<UserRoleVO>> userIdRoleMap = userRoleService.getUserRoleByUserIdList(userIds).stream()
                .collect(Collectors.groupingBy(UserRoleVO::getUserId));
        List<UserDeptVO> userDeptVOList = userDeptService.getByUserIdList(userIds, null);
        Map<Long, List<UserDeptVO>> userDeptMap =
                userDeptVOList.stream().collect(Collectors.groupingBy(UserDeptVO::getUserId));
        List<UserPostVO> userPostVOList = userPostService.getListByUserIdList(userIds);
        Map<Long, List<UserPostVO>> userPostMap =
                userPostVOList.stream().collect(Collectors.groupingBy(UserPostVO::getUserId));
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(userIds);
        List<Long> sourceCompanyId =
                userCompanyVOS.stream().map(UserCompanyVO::getSourceCompanyId).collect(Collectors.toList());
        List<UserCompanyVO> adminList = userCompanyService.getAdminList(sourceCompanyId);
        List<String> adminUserList =
                adminList.stream().map(c -> c.getCompanyId() + "_" + c.getUserId()).collect(Collectors.toList());
        Map<Long, UserCompanyVO> userCompanyVOMap =
                userCompanyVOS.stream().collect(Collectors.toMap(UserCompanyVO::getUserId, c -> c));
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        for (UserEntity userEntity : userEntityList) {
            UserVO userVO = AbstractUserConverter.INSTANCE.toVO(userEntity);
            List<UserRoleVO> userRoleVOS = userIdRoleMap.get(userVO.getUserId());
            userVO.setRoles(userRoleVOS);
            if (CollectionUtils.isNotEmpty(userRoleVOS)) {
                userVO.setRoleIdList(userRoleVOS.stream().map(UserRoleVO::getRoleId).collect(Collectors.toList()));
            }
            List<UserDeptVO> userDeptVOS = userDeptMap.get(userEntity.getUserId());
            if (CollectionUtils.isNotEmpty(userDeptVOS)) {
                userVO.setDeptName(userDeptVOS.get(0).getDeptName());
            }
            if (CollectionUtils.isNotEmpty(userDeptVOS)) {
                userVO.setDeptIdList(userDeptVOS.stream().map(UserDeptVO::getDeptId).collect(Collectors.toList()));
            }
            List<UserPostVO> userPostVOS = userPostMap.get(userEntity.getUserId());
            if (CollectionUtils.isNotEmpty(userPostVOS)) {
                userVO.setPostIdList(userPostVOS.stream().map(UserPostVO::getPostId).collect(Collectors.toList()));
            }
            UserCompanyVO userCompanyVO = userCompanyVOMap.get(userEntity.getUserId());
            if (userCompanyVO != null) {
                userVO.setNickName(userCompanyVO.getNickName());
                userVO.setEmail(userCompanyVO.getEmail());
                userVO.setAdminUser(userCompanyVO.getAdminUser());
                userVO.setAvatar(userCompanyVO.getAvatar());
                userVO.setUserType(userCompanyVO.getUserType());
                if (CompanyDataSourceEnum.LARK.name().equals(info.getDataSource())) {
                    if (StringUtils.isNotEmpty(userCompanyVO.getLarkUserId())) {
                        userVO.setDataSource(CompanyDataSourceEnum.LARK.name());
                    }
                } else if (CompanyDataSourceEnum.DING_TALK.name().equals(info.getDataSource())) {
                    if (StringUtils.isNotEmpty(userCompanyVO.getDingThirdId())) {
                        userVO.setDataSource(CompanyDataSourceEnum.DING_TALK.name());
                    }
                }
                if (adminUserList.contains(userCompanyVO.getSourceCompanyId() + "_" + userCompanyVO.getUserId())) {
                    userVO.setSourceCompanyAdmin(Boolean.TRUE);
                } else {
                    userVO.setSourceCompanyAdmin(Boolean.FALSE);
                }
            }
            List<DepartmentVO> departmentVOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userDeptVOS)) {
                for (UserDeptVO userDeptVO : userDeptVOS) {
                    List<DepartmentVO> departmentVOS =
                            departmentService.getParentListById(userDeptVO.getDeptId(), null);
                    for (DepartmentVO departmentVO : departmentVOS) {
                        DepartmentVO addDept = new DepartmentVO();
                        addDept.setDeptName(departmentVO.getDeptName());
                        addDept.setDeptId(departmentVO.getDeptId());
                        departmentVOList.add(addDept);
                    }
                }
            }
            userVO.setDepartmentList(departmentVOList.stream().distinct().collect(Collectors.toList()));
            userVO.setUserPostList(userPostVOS);
            userVO.setUserDeptList(userDeptVOS);
            userVOList.add(userVO);
        }
        return userVOList;
    }

    @Override
    public UserVO queryByUserName(String userName) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserName, userName);
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        final UserEntity userEntity = userMapper.selectOne(queryWrapper);
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyId(userEntity.getCompanyId());
        userDomain.setUserId(userEntity.getUserId().toString());
        userDomain.setUserName(userName);
        UserUtils.setUser(userDomain);
        return buildUserInfoWhileLogin(userEntity);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserVO queryByMobile(String mobile) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserName, mobile);
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        final UserEntity userEntity = userMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            return null;
        }
        return AbstractUserConverter.INSTANCE.toVO(userEntity);
    }

    @Override
    public UserVO queryByUuid(String uuid) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        final UserEntity userEntity = userMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            return null;
        }
        return AbstractUserConverter.INSTANCE.toVO(userEntity);
    }

    @Override
    public UserVO queryCurrentCompanyUserByMobile(String mobile) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getPhonenumber, mobile);
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        final UserEntity userEntity = userMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            return null;
        }
        List<UserCompanyVO> userCompanyVOS =
                userCompanyService.getByUserIdList(Collections.singletonList(userEntity.getUserId()));
        if (CollectionUtils.isEmpty(userCompanyVOS)) {
            return null;
        }
        return buildUserInfo(Collections.singletonList(userEntity)).get(0);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<UserVO> queryByIds(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserEntity::getUserId, userIdList);
        final List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userEntityList)) {
            return new ArrayList<>();
        }
        return buildUserInfo(userEntityList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<Long, String> getIdToNameMap(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserEntity::getUserId, userIdList);
        List<UserCompanyVO> userCompanyVOS = userCompanyService.getByUserIdList(userIdList);
        if (CollectionUtils.isEmpty(userCompanyVOS)) {
            return new HashMap<>();
        }
        Map<Long, String> userNameMap = new HashMap<>();
        for (UserCompanyVO userEntity : userCompanyVOS) {
            String nickName = userEntity.getNickName();
            userNameMap.put(userEntity.getUserId(), nickName);
        }
        for (UserDefaultEnum userDefaultEnum : UserDefaultEnum.values()) {
            userNameMap.put(userDefaultEnum.getId(), userDefaultEnum.getName());
        }
        return userNameMap;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserVO info(Long userId) {
        List<UserEntity> userEntityList;
        if (UserUtils.getUser() == null || UserUtils.getUser().getCompanyId() == null ||
                UserUtils.getUser().getCompanyId() == 0L) {
            UserEntity userEntity = userMapper.selectById(userId);
            return AbstractUserConverter.INSTANCE.toVO(userEntity);
        } else {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("u.del_flag", Constants.NU_DELETED);
            queryWrapper.eq("uc.del_flag", Constants.NU_DELETED);
            queryWrapper.eq("uc.company_id", UserUtils.getUser().getCompanyId());
            queryWrapper.eq("u.user_id", userId);
            userEntityList = userMapper.selectLists(queryWrapper);
        }
        if (CollectionUtils.isEmpty(userEntityList)) {
            return null;
        }
        UserVO userVO = buildUserInfo(userEntityList).get(0);
        List<UserInfoVO> userInfoVOS = userInfoService.getByUserId(Collections.singletonList(userVO.getUserId()));
        userVO.setUserInfoList(userInfoVOS);
        return userVO;
    }

    @Override
    public UserVO infoOnly(Long userId) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(UserEntity::getUserId, userId);
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        return AbstractUserConverter.INSTANCE.toVO(userEntity);
    }

    @Override
    public List<UserVO> infoOnly(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.in(UserEntity::getUserId, userIdList);
        List<UserEntity> userEntityList = userMapper.selectList(queryWrapper);
        return userEntityList.stream().map(AbstractUserConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pullData(MultipartFile file) {
        UserDomain user = UserUtils.getUser();
        List<DepartmentVO> departmentVOList =
                departmentService.queryListNow(user.getCompanyId(), DepartmentTypeEnum.INTERNAL_DEPT.getCode());
        List<UserPullVO> userDingVOS = new ArrayList<>();
        List<String> thirdIdList = new ArrayList<>();
        CompanyVO info = companyService.info(user.getCompanyId());
        CompanyDataSourceEnum dataSourceEnum = CompanyDataSourceEnum.valueOf(info.getDataSource());
        for (DepartmentVO departmentVO : departmentVOList) {
            String thirdId = departmentVO.getThirdId();
            if (thirdId == null) {
                continue;
            }
            if (departmentVO.getDeptId() == 0) {
                thirdId = dataSourceEnum.getRootParentId();
            }
            List<UserPullVO> allUserList =
                    pullDataContext.getHandler(dataSourceEnum.name()).getAllUserList(thirdId, info);
            for (UserPullVO userDingVO : allUserList) {
                if (thirdIdList.contains(userDingVO.getThirdId())) {
                    continue;
                }
                thirdIdList.add(userDingVO.getThirdId());
                userDingVOS.add(userDingVO);
            }
        }
        if (CompanyDataSourceEnum.WECOM == dataSourceEnum) {
            List<UserPullVO> importList = new ArrayList<>();
            Map<String, String> wecomIdToMobileMap = new HashMap<>();
            if (file != null) {
                try {
                    List<WeComDomain> weComDomainList =
                            EasyExcel.read(file.getInputStream(), WeComDomain.class, null).excelType(ExcelTypeEnum.XLSX)
                                    .sheet(0).headRowNumber(1).autoTrim(false).doReadSync();
                    wecomIdToMobileMap = weComDomainList.stream()
                            .collect(Collectors.toMap(WeComDomain::getThirdId, WeComDomain::getPhone));
                } catch (Exception e) {
                    log.error("解析Excel失败", e);
                }
            }
            for (UserPullVO userPullVO : userDingVOS) {
                String mobile = wecomIdToMobileMap.get(userPullVO.getWeComUserId());
                if (StringUtils.isNotEmpty(mobile)) {
                    userPullVO.setPhonenumber(mobile);
                    importList.add(userPullVO);
                }
            }
            userDingVOS = importList;
        }
        saveDingUserInfo(userDingVOS, user, departmentVOList, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDingUserInfo(List<UserPullVO> userDingVOS, UserDomain user, List<DepartmentVO> departmentVOList,
                                 String importType) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        if (departmentVOList == null) {
            if (importType == null) {
                importType = DepartmentTypeEnum.INTERNAL_DEPT.getCode();
            }
            departmentVOList = departmentService.queryListNow(UserUtils.getUser().getCompanyId(), importType);
        }
        Map<String, UserEntity> userIdMap;
        List<UserPullVO> userPullVOList = userDingVOS.stream().filter(c -> StringUtils.isNotEmpty(c.getPhonenumber()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userPullVOList)) {
            userIdMap = saveUserWhilePullByThirdId(userDingVOS, user, info);
        } else {
            userIdMap = saveUserBatchByPhone(userDingVOS, user, info.getDataSource());
        }
        Map<String, Long> postNameMap = importPost(
                userDingVOS.stream().map(UserPullVO::getPosition).filter(StringUtils::isNotEmpty).distinct()
                        .collect(Collectors.toList()));
        Map<String, Long> deptThirdIdToMap =
                departmentVOList.stream().filter(c -> StringUtils.isNotEmpty(c.getThirdId()))
                        .collect(Collectors.toMap(DepartmentVO::getThirdId, DepartmentVO::getDeptId));
        bindUserInfo(userDingVOS, userIdMap, deptThirdIdToMap, postNameMap, info);
    }

    private void bindUserInfo(List<UserPullVO> userDingVOS, Map<String, UserEntity> userIdMap,
                              Map<String, Long> deptThirdIdToMap, Map<String, Long> postNameMap, CompanyVO info) {
        List<UserDeptSaveRequest> userDeptSaveRequestList = new ArrayList<>();
        List<UserPostCreateRequest> userPostCreateRequestList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        for (UserPullVO userPullVO : userDingVOS) {
            UserEntity userEntity = userIdMap.get(userPullVO.getUuid());
            userIdList.add(userEntity.getUserId());
            if (CollectionUtils.isNotEmpty(userPullVO.getDeptIdList())) {
                for (String thirdDeptId : userPullVO.getDeptIdList()) {
                    UserDeptSaveRequest userDeptSaveRequest = new UserDeptSaveRequest();
                    Long deptId = deptThirdIdToMap.get(thirdDeptId);
                    if (deptId == null) {
                        continue;
                    }
                    userDeptSaveRequest.setDeptId(deptId);
                    userDeptSaveRequest.setUserId(userEntity.getUserId());
                    userDeptSaveRequestList.add(userDeptSaveRequest);
                }
            }
            if (StringUtils.isNotBlank(userPullVO.getPosition())) {
                UserPostCreateRequest userPostCreateRequest = new UserPostCreateRequest();
                Long postId = postNameMap.get(userPullVO.getPosition());
                userPostCreateRequest.setPostId(postId);
                userPostCreateRequest.setUserId(userEntity.getUserId());
                userPostCreateRequestList.add(userPostCreateRequest);
            }
        }
        saveUserCompanyList(userDingVOS, info, userIdMap, UserTypeEnum.INTERNAL.getCode(), null);
        userDeptService.save(userDeptSaveRequestList, userIdList);
        userPostService.save(userPostCreateRequestList, userIdList);
    }

    public void saveUserCompanyList(List<UserPullVO> userDingVOS, CompanyVO info, Map<String, UserEntity> userIdMap,
                                    String userType, Boolean admin) {
        List<UserCompanySaveRequest> userCompanySaveRequestList = new ArrayList<>();
        for (UserPullVO userPullVO : userDingVOS) {
            UserEntity userEntity = userIdMap.get(userPullVO.getUuid());
            UserCompanySaveRequest userCompanySaveRequest = AbstractUserCompanyConverter.INSTANCE.toRequest(userPullVO);
            userCompanySaveRequest.setDingUnionId(userPullVO.getUnionid());
            if (UserTypeEnum.INTERNAL.getCode().equals(userType)) {
                if (userPullVO.getSourceCompanyId() != null) {
                    userCompanySaveRequest.setCompanyId(userPullVO.getSourceCompanyId());
                    userCompanySaveRequest.setSourceCompanyId(null);
                } else {
                    userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
                }
            } else {
                userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
            }
            userCompanySaveRequest.setUserId(userEntity.getUserId());
            userCompanySaveRequest.setUserType(userType);
            userCompanySaveRequest.setDingThirdId(userPullVO.getDingThirdId());
            userCompanySaveRequest.setAdminUser(admin);
            if (info != null) {
                userCompanySaveRequest.setThirdType(info.getDataSource());
            }
            userCompanySaveRequestList.add(userCompanySaveRequest);
        }
        userCompanyService.save(userCompanySaveRequestList);
    }

    @Override
    public Map<String, UserEntity> saveUserBatchByPhone(List<UserPullVO> userDingVOS, UserDomain user, String source) {
        List<String> phoneNumberList =
                userDingVOS.stream().map(UserPullVO::getPhonenumber).collect(Collectors.toList());
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.in(UserEntity::getPhonenumber, phoneNumberList);
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        List<UserEntity> existList = userMapper.selectList(sameWrapper);
        Map<String, UserEntity> phonenumberMap =
                existList.stream().collect(Collectors.toMap(UserEntity::getPhonenumber, c -> c));
        userDingVOS = userDingVOS.stream().distinct().collect(Collectors.toList());
        List<UserEntity> insertList = new ArrayList<>();
        List<UserEntity> updateList = new ArrayList<>();
        for (UserPullVO userDingVO : userDingVOS) {
            UserEntity userEntity = phonenumberMap.get(userDingVO.getPhonenumber());
            if (userEntity == null) {
                userEntity = AbstractUserConverter.INSTANCE.toEntity(userDingVO);
                userEntity.setUserName(userDingVO.getPhonenumber());
                userEntity.setUserType("06");
                userEntity.setCreateBy(user.getUserName());
                userEntity.setStatus(Constants.NORMAL);
                userEntity.setUuid(IdUtils.simpleUUID());
                userDingVO.setUuid(userEntity.getUuid());
                if (source.equals(CompanyDataSourceEnum.EXTERNAL_IMPORT.name())) {
                    userEntity.setCompanyId(userDingVO.getSourceCompanyId());
                } else {
                    userEntity.setCompanyId(UserUtils.getUser().getCompanyId());
                }
                userEntity.setUpdateBy(user.getUserName());
                userEntity.setCreateTime(new Date());
                userEntity.setUpdateTime(new Date());
                userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getPhonenumber()));
                insertList.add(userEntity);
            } else {
                if (source.equals(CompanyDataSourceEnum.EXTERNAL_IMPORT.name())) {
                    if (userEntity.getCompanyId() == null || userEntity.getCompanyId() == 0) {
                        userEntity.setCompanyId(userDingVO.getSourceCompanyId());
                        userMapper.updateById(userEntity);
                    }
                }
                updateList.add(userEntity);
            }
            userDingVO.setUuid(userEntity.getUuid());
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            saveBatch(insertList);
        }
        insertList.addAll(updateList);
        return insertList.stream().collect(Collectors.toMap(UserEntity::getUuid, c -> c));
    }

    private Map<String, UserEntity> saveUserWhilePullByThirdId(List<UserPullVO> userDingVOS, UserDomain user,
                                                               CompanyVO companyVO) {

        List<UserCompanyVO> all =
                userCompanyService.getByThirdType(UserUtils.getUser().getCompanyId(), companyVO.getDataSource());
        List<UserEntity> existList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(all)) {
            LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
            sameWrapper.in(UserEntity::getUserId,
                    all.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList()));
            sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
            existList = userMapper.selectList(sameWrapper);
        }
        Map<Long, UserEntity> userIdMap = existList.stream().collect(Collectors.toMap(UserEntity::getUserId, c -> c));
        LambdaQueryWrapper<UserEntity> userNameWrapper = new LambdaQueryWrapper<>();
        userNameWrapper.in(UserEntity::getUserName,
                userDingVOS.stream().map(UserPullVO::getUserName).collect(Collectors.toList()));
        userNameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        List<UserEntity> userEntityList = userMapper.selectList(userNameWrapper);
        Map<String, UserEntity> userNameMap =
                userEntityList.stream().collect(Collectors.toMap(UserEntity::getUserName, c -> c));
        Map<String, UserCompanyVO> userCompanyIdToMap =
                all.stream().collect(Collectors.toMap(UserCompanyVO::getThirdId, c -> c));
        userDingVOS = userDingVOS.stream().distinct().collect(Collectors.toList());
        List<UserEntity> insertList = new ArrayList<>();
        List<UserEntity> updateList = new ArrayList<>();
        for (UserPullVO userDingVO : userDingVOS) {
            UserCompanyVO userCompanyVO = userCompanyIdToMap.get(userDingVO.getThirdId());
            if (userCompanyVO == null) {
                UserEntity sameUserName = userNameMap.get(userDingVO.getUserName());
                if (sameUserName == null) {
                    UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(userDingVO);
                    userEntity.setUserName(userDingVO.getUserName());
                    userEntity.setUserType("06");
                    userEntity.setCreateBy(user.getUserName());
                    userEntity.setStatus(Constants.NORMAL);
                    userEntity.setUuid(IdUtils.simpleUUID());
                    userEntity.setCompanyId(UserUtils.getUser().getCompanyId());
                    userEntity.setUpdateBy(user.getUserName());
                    userEntity.setPassword(PasswordUtil.encryptPassword(
                            ConfigCache.getValue(ConfigEnum.LOWCODE_DEFAULT_PASSWORD.name())));
                    insertList.add(userEntity);
                    userDingVO.setUuid(userEntity.getUuid());
                } else {
                    userDingVO.setUuid(sameUserName.getUuid());
                    updateList.add(sameUserName);
                }
            } else {
                UserEntity userEntity = userIdMap.get(userCompanyVO.getUserId());
                userDingVO.setUuid(userEntity.getUuid());
                updateList.add(userEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            saveBatch(insertList);
        }
        insertList.addAll(updateList);
        return insertList.stream().collect(Collectors.toMap(UserEntity::getUuid, c -> c));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserCreateRequest userCreateRequest) {
        UserDomain user = UserUtils.getUser() != null ? UserUtils.getUser() : new UserDomain();
        checkUserCount(user.getCompanyId(), 1L, userCreateRequest.getUserType());
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userCreateRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        UserEntity exist = userMapper.selectOne(sameWrapper);
        if (exist != null) {
            if (StringUtils.isNotEmpty(userCreateRequest.getUserName())) {
                checkUserNameExist(userCreateRequest.getUserName(), exist.getUserId());
            }
            createUserCompany(userCreateRequest, exist, null);
            if (CollectionUtils.isNotEmpty(userCreateRequest.getRoleIdList())) {
                userRoleService.save(exist.getUserId(), userCreateRequest.getRoleIdList(), Boolean.TRUE);
            }
            userDeptService.save(userCreateRequest.getDeptIdList(), exist.getUserId());
            userPostService.save(exist.getUserId(), userCreateRequest.getPostList());
            userInfoService.save(exist.getUserId(), userCreateRequest.getUserInfoList());
            return exist.getUserId();
        }
        final UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(userCreateRequest);

        userEntity.setCreateBy(user.getUserName());
        userEntity.setStatus(Constants.NORMAL);
        userEntity.setUuid(IdUtils.simpleUUID());
        userEntity.setUserType("06");
        userEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        userEntity.setUpdateBy(user.getUserName());
        userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getPassword()));
        if (CollectionUtils.isNotEmpty(userCreateRequest.getDeptIdList())) {
            userEntity.setDeptId(userCreateRequest.getDeptIdList().get(0));
        }
        userMapper.insert(userEntity);
        createUserCompany(userCreateRequest, userEntity, null);
        if (CollectionUtils.isNotEmpty(userCreateRequest.getRoleIdList())) {
            userRoleService.save(userEntity.getUserId(), userCreateRequest.getRoleIdList(), Boolean.TRUE);
        }
        userDeptService.save(userCreateRequest.getDeptIdList(), userEntity.getUserId());
        userPostService.save(userEntity.getUserId(), userCreateRequest.getPostList());
        userInfoService.save(userEntity.getUserId(), userCreateRequest.getUserInfoList());
        return userEntity.getUserId();
    }

    @Override
    public Long onlyCreateUser(UserCreateRequest userCreateRequest) {
        final UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(userCreateRequest);
        userEntity.setStatus(Constants.NORMAL);
        userEntity.setUuid(IdUtils.simpleUUID());
        userEntity.setUserType("06");
        userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getPhonenumber()));
        if (CollectionUtils.isNotEmpty(userCreateRequest.getDeptIdList())) {
            userEntity.setDeptId(userCreateRequest.getDeptIdList().get(0));
        }
        userMapper.insert(userEntity);
        return userEntity.getUserId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest userUpdateRequest, Boolean roster) {
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userUpdateRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        sameWrapper.ne(UserEntity::getUserId, userUpdateRequest.getUserId());
        UserEntity exist = userMapper.selectOne(sameWrapper);
        if (exist != null) {
            throw new AdminException(AdminResultCode.PHONE_NUMBER_EXIST);
        } else {
            if (StringUtils.isNotEmpty(userUpdateRequest.getUserName())) {
                checkUserNameExist(userUpdateRequest.getUserName(), userUpdateRequest.getUserId());
            }
        }
        final UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(userUpdateRequest);
        userEntity.setUpdateBy(UserUtils.getUser().getUserName());
        if (CollectionUtils.isNotEmpty(userUpdateRequest.getDeptIdList())) {
            userEntity.setDeptId(userUpdateRequest.getDeptIdList().get(0));
        }
        userMapper.updateById(userEntity);
        if (!roster) {
            userRoleService.save(userEntity.getUserId(), userUpdateRequest.getRoleIdList(), Boolean.TRUE);
            userDeptService.save(userUpdateRequest.getDeptIdList(), userEntity.getUserId());
            userPostService.save(userEntity.getUserId(), userUpdateRequest.getPostList());
        }
        userInfoService.save(userEntity.getUserId(), userUpdateRequest.getUserInfoList());
        updateUserCompany(userUpdateRequest);
    }

    private void updateUserCompany(UserUpdateRequest userUpdateRequest) {
        UserCompanySaveRequest userCompanySaveRequest =
                AbstractUserCompanyConverter.INSTANCE.toRequest(userUpdateRequest);
        userCompanySaveRequest.setUserId(userUpdateRequest.getUserId());
        userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        userCompanySaveRequest.setNickName(userUpdateRequest.getNickName());
        userCompanySaveRequest.setEmail(userUpdateRequest.getEmail());
        userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.FALSE);
    }

    private Boolean createUserCompany(UserCreateRequest userCreateRequest, UserEntity userEntity, String status) {
        UserCompanySaveRequest userCompanySaveRequest =
                AbstractUserCompanyConverter.INSTANCE.toRequest(userCreateRequest);
        userCompanySaveRequest.setUserId(userEntity.getUserId());
        userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        userCompanySaveRequest.setNickName(userCreateRequest.getNickName());
        userCompanySaveRequest.setEmail(userCreateRequest.getEmail());
        userCompanySaveRequest.setUserType(userCreateRequest.getUserType());
        userCompanySaveRequest.setInviteCode(userCreateRequest.getInviteCode());
        userCompanySaveRequest.setStatus(status);
        // CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        // if (CompanyDataSourceEnum.WECOM_THIRD.name().equals(info.getDataSource()) &&
        //         StringUtils.isEmpty(userEntity.getPhonenumber())) {
        //     String thirdId = weComThirdService.getThirdId(userEntity.getPhonenumber());
        //     if (thirdId == null) {
        //         throw new AdminException(AdminResultCode.PHONE_NO_AUTH_OR_NOT_EXIST);
        //     }
        //     List<UserCompanyVO> userCompanyVOList =
        //             userCompanyService.getByWeComUserId(Collections.singletonList(thirdId));
        //     if (CollectionUtils.isNotEmpty(userCompanyVOList)) {
        //         if (!Objects.equals(userCompanyVOList.get(0).getUserId(), userEntity.getUserId())) {
        //             throw new AdminException(AdminResultCode.WECOM_HAS_BEEN_BIND);
        //         }
        //     }
        //     userCompanySaveRequest.setThirdId(thirdId);
        //     userCompanySaveRequest.setThirdType(CompanyDataSourceEnum.WECOM_THIRD.name());
        //     userCompanySaveRequest.setWeComUserId(thirdId);
        // }
        return userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.TRUE);
    }

    private void checkUserNameExist(String userName, Long userId) {
        LambdaQueryWrapper<UserEntity> checkUserNameWrapper = new LambdaQueryWrapper<>();
        checkUserNameWrapper.eq(UserEntity::getUserName, userName);
        checkUserNameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        checkUserNameWrapper.ne(UserEntity::getUserId, userId);
        UserEntity checkName = userMapper.selectOne(checkUserNameWrapper);
        if (checkName != null) {
            throw new AdminException(AdminResultCode.USER_NAME_EXIST);
        }
    }

    @Override
    public void delete(String uuid) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUuid, uuid);
        UserEntity exist = userMapper.selectOne(queryWrapper);
        if (exist == null) {
            return;
        }
        List<Long> companyIdList = userCompanyService.deleteByUserId(exist.getUserId());
        LambdaQueryWrapper<UserEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserEntity::getUuid, uuid);
        if (CollectionUtils.isEmpty(companyIdList)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setCompanyId(0L);
            userEntity.setUpdateTime(new Date());
            userEntity.setUpdateBy(UserUtils.getUser().getUserName());
            userMapper.update(userEntity, deleteWrapper);
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setCompanyId(companyIdList.get(0));
            userEntity.setUpdateTime(new Date());
            userEntity.setUpdateBy(UserUtils.getUser().getUserName());
            userMapper.update(userEntity, deleteWrapper);
        }
        manageCommonService.deleteManage(UserUtils.getUser().getCompanyId(), exist.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportVO importFile(MultipartFile file, String userType) {
        UserDomain user = UserUtils.getUser() != null ? UserUtils.getUser() : new UserDomain();
        UserImportVO userImportVO = new UserImportVO();
        try {
            List<UserExcelDomain> userDomainList =
                    EasyExcel.read(file.getInputStream(), UserExcelDomain.class, null).excelType(ExcelTypeEnum.XLSX)
                            .sheet(0).headRowNumber(2).autoTrim(false).doReadSync();
            if (CollectionUtils.isEmpty(userDomainList)) {
                userImportVO.setErrorList(new ArrayList<>());
                return userImportVO;
            }
            checkUserCount(user.getCompanyId(), (long) userDomainList.size(), userType);
            List<UserImportErrorVO> errorList = new ArrayList<>();
            List<String> phoneNumberList = userDomainList.stream().map(UserExcelDomain::getPhonenumber).distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(phoneNumberList)) {
                return new UserImportVO();
            }
            List<UserVO> userVOS = queryByMobilePhone(phoneNumberList);
            List<Long> userIdList = userVOS.stream().map(UserVO::getUserId).collect(Collectors.toList());
            Map<Long, UserCompanyVO> userIdToCompanyMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userIdList)) {
                userIdToCompanyMap = userCompanyService.getByUserIdList(userIdList).stream().collect(
                        Collectors.toMap(UserCompanyVO::getUserId, c -> c, (existing, replacement) -> existing));
            }
            Map<String, UserVO> phoneNumberMap = userVOS.stream()
                    .collect(Collectors.toMap(UserVO::getPhonenumber, c -> c, (existing, replacement) -> existing));
            List<UserExcelDomain> importList = new ArrayList<>();
            // 校验数据是否正确
            int i = 0;
            for (UserExcelDomain userExcelDomain : userDomainList) {
                boolean canImport = Boolean.TRUE;
                if (StringUtils.isEmpty(userExcelDomain.getNickName())) {
                    addErrorMessage(i, "姓名不能为空", errorList, Boolean.FALSE);
                    canImport = Boolean.FALSE;
                }
                if (StringUtils.isEmpty(userExcelDomain.getPhonenumber())) {
                    addErrorMessage(i, "手机号码不能为空", errorList, Boolean.FALSE);
                    canImport = Boolean.FALSE;
                }
                if (!StringUtil.isValidPhoneNumber(userExcelDomain.getPhonenumber())) {
                    addErrorMessage(i, "手机号码错误", errorList, Boolean.FALSE);
                    canImport = Boolean.FALSE;
                }
                UserVO userVO = phoneNumberMap.get(userExcelDomain.getPhonenumber());
                if (userVO != null) {
                    UserCompanyVO userCompanyVO = userIdToCompanyMap.get(userVO.getUserId());
                    if (userCompanyVO != null) {
                        addErrorMessage(i, "当前用户已存在", errorList, Boolean.FALSE);
                        canImport = Boolean.FALSE;
                    }
                }
                if (canImport) {
                    if (StringUtils.isNotEmpty(userExcelDomain.getSex())) {
                        if ("男".equals(userExcelDomain.getSex())) {
                            userExcelDomain.setSex("1");
                        } else {
                            userExcelDomain.setSex("2");
                        }
                    }
                    if (userExcelDomain.getEntryTimeDate() != null) {
                        userExcelDomain.setEntryTime(userExcelDomain.getEntryTimeDate().getTime());
                    }
                    addErrorMessage(i, "", errorList, Boolean.TRUE);
                    importList.add(userExcelDomain);
                }
            }
            userImportVO.setErrorList(errorList);
            if (CollectionUtils.isEmpty(importList)) {
                return userImportVO;
            }
            Map<String, Long> deptFullNameMap = importDept(importList, userType);
            Map<String, Long> postNameMap = importPost(
                    importList.stream().map(UserExcelDomain::getPosition).filter(StringUtils::isNotEmpty).distinct()
                            .collect(Collectors.toList()));
            Map<String, UserEntity> userIdMap = importUser(importList, user);
            // 导入用户部门和职位
            importUserDeptAndPost(importList, userIdMap, deptFullNameMap, postNameMap, userType);
        } catch (Exception e) {
            log.error("导入失败", e);
            throw new AdminException(AdminResultCode.IMPORT_LIST_ERROR, e.getMessage());
        }
        return userImportVO;
    }

    private static void addErrorMessage(int i, String errorMessage, List<UserImportErrorVO> corpCoopImportList,
                                        Boolean success) {
        UserImportErrorVO corpCoopImportErrorVO = new UserImportErrorVO();
        corpCoopImportErrorVO.setRow(i + 1);
        corpCoopImportErrorVO.setErrorMessage(errorMessage);
        corpCoopImportErrorVO.setSuccess(success);
        corpCoopImportList.add(corpCoopImportErrorVO);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try (InputStream is = ResourceReader.class.getResourceAsStream("/template/user.xlsx");
             ServletOutputStream outputStream = response.getOutputStream()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("导入用户.xlsx", "UTF-8"));
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            IOUtils.copy(is, outputStream);
        } catch (Exception e) {
            log.error("生成模板错误", e);
        }
    }

    @Override
    public String invite(UserInviteRequest userInviteRequest) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userInviteRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        UserEntity exist = userMapper.selectOne(sameWrapper);
        String invite = SnowFlakeIdUtils.generateStr();
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setPhonenumber(userInviteRequest.getPhonenumber());
        userCreateRequest.setNickName(userInviteRequest.getNickName());
        userCreateRequest.setInviteCode(invite);
        userCreateRequest.setUserType(userInviteRequest.getUserType());
        if (exist != null) {
            Boolean existUser = createUserCompany(userCreateRequest, exist, UserStateEnum.INVITE.getCode());
            if (existUser) {
                return null;
            } else {
                return invite;
            }
        }
        final UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(userInviteRequest);
        userEntity.setCreateBy(user.getUserName());
        userEntity.setStatus(Constants.NORMAL);
        userEntity.setUuid(IdUtils.simpleUUID());
        userEntity.setUserType("06");
        userEntity.setCompanyId(user.getCompanyId());
        userEntity.setUpdateBy(user.getUserName());
        userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getPassword()));
        if (CollectionUtils.isNotEmpty(userInviteRequest.getDeptIdList())) {
            userEntity.setDeptId(userInviteRequest.getDeptIdList().get(0));
        }
        userMapper.insert(userEntity);
        userDeptService.save(userInviteRequest.getDeptIdList(), userEntity.getUserId());
        createUserCompany(userCreateRequest, userEntity, UserStateEnum.INVITE.getCode());
        return invite;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long complete(UserCompleteRequest userCompleteRequest) {
        UserDomain user = UserUtils.getUser();
        if (StringUtils.isEmpty(userCompleteRequest.getCompanyName())) {
            throw new AdminException(AdminResultCode.COMPANY_NAME_IS_EMPTY);
        }
        if (user.getCompanyId() != null && user.getCompanyId() != 0) {
            throw new AdminException(AdminResultCode.USER_HAS_COMPANY);
        }
        Long companyId =
                companyService.createCompany(userCompleteRequest.getCompanyName(), userCompleteRequest.getParentUuid(),
                        CompanyChannelTypeEnum.SYSTEM.name());
        userIntention(userCompleteRequest, companyId);
        UserEntity userEntity = new UserEntity();
        userEntity.setCompanyId(companyId);
        userEntity.setUserId(Long.valueOf(user.getUserId()));
        userEntity.setNickName(userCompleteRequest.getNickName());
        userEntity.setDemand(userCompleteRequest.getDemand());
        userMapper.updateById(userEntity);

        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setUserId(userEntity.getUserId());
        userCompanySaveRequest.setCompanyId(companyId);
        userCompanySaveRequest.setNickName(userCompleteRequest.getNickName());
        userCompanySaveRequest.setUserType("00");
        userCompanySaveRequest.setAdminUser(Boolean.TRUE);
        userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.TRUE);
        userRoleService.insertWhileRegister(user.getUserIdLongValue(), companyId);
        companyAppService.saveDefault(companyId);
        return companyId;
    }

    private void userIntention(UserCompleteRequest userCompleteRequest, Long companyId) {
        UserIntentionRequest userIntentionDto = new UserIntentionRequest();
        userIntentionDto.setCompanyId(companyId);
        userIntentionDto.setUserId(UserUtils.getUser().getUserIdLongValue());
        userIntentionDto.setCompanyName(userCompleteRequest.getCompanyName());
        userIntentionDto.setPosition(userCompleteRequest.getCompanyRole());
        userIntentionDto.setUnderstandWay(userCompleteRequest.getUnderstandWay());
        userIntentionDto.setNickName(userCompleteRequest.getNickName());
        userIntentionDto.setIndustry(userCompleteRequest.getIndustry());
        userIntentionDto.setManageDemand(userCompleteRequest.getManageDemand());
        userIntentionDto.setMessage(userCompleteRequest.getDemand());
        userIntentionDto.setUseLowcode(userCompleteRequest.getUseLowcode());
        userIntentionDto.setMobile(userCompleteRequest.getMobile());
        userIntentionDto.setIntentionType(2);
        userIntentionService.save(userIntentionDto);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Long getAdmin() {
        return userCompanyService.getAdmin(UserUtils.getUser().getCompanyId());
    }

    @Override
    public void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {
        UserEntity user = userMapper.selectById(userUpdatePasswordRequest.getUserId());
        String originDecrypt =
                AESUtils.decrypt(Constants.PASSWORD_KEY.getBytes(), userUpdatePasswordRequest.getOriginPassword());
        if (!PasswordUtil.matchesPassword(originDecrypt, user.getPassword())) {
            throw new AdminException(AdminResultCode.ORIGIN_PASSWORD_ERROR);
        }

        String decrypt = AESUtils.decrypt(Constants.PASSWORD_KEY.getBytes(), userUpdatePasswordRequest.getPassword());
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userUpdatePasswordRequest.getUserId());
        userEntity.setPassword(PasswordUtil.encryptPassword(decrypt));
        userMapper.updateById(userEntity);
    }

    @Override
    public List<Long> getUserByScope(List<UserScope> scopeList) {
        if (CollectionUtils.isEmpty(scopeList)) {
            return new ArrayList<>();
        }
        List<Long> userIdList = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        for (UserScope userScope : scopeList) {
            if (userScope.getBusinessType().equalsIgnoreCase(UserScopeEnum.USER.getType())) {
                userIdList.add(Long.valueOf(userScope.getBusinessId()));
            } else if (userScope.getBusinessType().equalsIgnoreCase(UserScopeEnum.DEPT.getType())) {
                deptIdList.add(Long.valueOf(userScope.getBusinessId()));
            } else if (userScope.getBusinessType().equalsIgnoreCase(UserScopeEnum.POST.getType())) {
                postIdList.add(Long.valueOf(userScope.getBusinessId()));
            }
        }
        List<Long> allChildren = DepartmentCache.getAllChildren(UserUtils.getUser().getCompanyId(), deptIdList);
        userIdList.addAll(userDeptService.getByDeptIdList(allChildren));
        userIdList.addAll(userPostService.getUserIdByPostIdList(postIdList));
        return userIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public UserVO getByCompanyAndUserId(Long companyId, Long userId) {
        UserEntity userEntity = userMapper.selectById(userId);
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserId, userId);
        UserCompanyVO userCompanyVO = userCompanyService.getByUserIdAndCompanyId(userId, companyId);
        List<UserDeptVO> userDeptVOS = userDeptService.getByUserIdList(Collections.singletonList(userId), companyId);
        CompanyVO info = companyService.info(companyId);
        UserVO userVO = AbstractUserConverter.INSTANCE.toVO(userEntity);
        userVO.setCompanyId(userCompanyVO.getCompanyId());
        userVO.setNickName(userCompanyVO.getNickName());
        userVO.setUserName(userEntity.getNickName());
        userVO.setAdminUser(userCompanyVO.getAdminUser());
        userVO.setCompanyUuid(info.getCompanyUuid());
        userVO.setUserDeptList(userDeptVOS);
        userVO.setUserType(userCompanyVO.getUserType());
        return userVO;
    }

    @Override
    public List<UserVO> queryByMobilePhone(List<String> mobileList) {
        if (CollectionUtils.isEmpty(mobileList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserEntity::getPhonenumber, mobileList);
        queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        return userMapper.selectList(queryWrapper).stream().map(AbstractUserConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePhone(UserPhoneUpdateRequest userPhoneUpdateRequest) {
        smsCodeUtils.checkCode(userPhoneUpdateRequest.getPhonenumber(), userPhoneUpdateRequest.getCode());
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.eq(UserEntity::getPhonenumber, userPhoneUpdateRequest.getPhonenumber());
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        sameWrapper.ne(UserEntity::getUserId, UserUtils.getUser().getUserId());
        UserEntity exist = userMapper.selectOne(sameWrapper);
        if (exist != null) {
            throw new AdminException(AdminResultCode.PHONE_NUMBER_EXIST);
        }
        UserEntity userEntity = userMapper.selectById(UserUtils.getUser().getUserId());
        userEntity.setPhonenumber(userPhoneUpdateRequest.getPhonenumber());
        userEntity.setUserName(userPhoneUpdateRequest.getPhonenumber());
        if (StringUtils.isEmpty(userEntity.getPhonenumber())) {
            userEntity.setPassword(PasswordUtil.encryptPassword(userEntity.getPhonenumber()));
        }
        userMapper.updateById(userEntity);
    }

    @Override
    public void weComComplete(UserWeComCompleteRequest userWeComCompleteRequest) {
        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setUserId(userWeComCompleteRequest.getUserId());
        userCompanySaveRequest.setNickName(userWeComCompleteRequest.getNickName());
        userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        userCompanyService.save(Collections.singletonList(userCompanySaveRequest));
    }

    @Override
    public void bindWeComId(List<Long> userIdList) {
        userCompanyService.bindWeComId(userIdList);
    }

    @Override
    public void initUser(Long companyId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("admin");
        userEntity.setPassword(PasswordUtil.encryptPassword("admin123"));
        userEntity.setCompanyId(companyId);
        userEntity.setStatus(Constants.NU_DELETED);
        userEntity.setUserType("00");
        userEntity.setUuid(IdUtils.simpleUUID());
        userEntity.setNickName("管理员");
        userEntity.setRealName("管理员");
        userMapper.insert(userEntity);
        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setCompanyId(companyId);
        userCompanySaveRequest.setUserId(userEntity.getUserId());
        userCompanySaveRequest.setUserType("00");
        userCompanySaveRequest.setAdminUser(true);
        userCompanySaveRequest.setNickName("管理员");
        userCompanySaveRequest.setStatus(Constants.NU_DELETED);
        userCompanyService.save(Collections.singletonList(userCompanySaveRequest));
    }


    @Override
    public void switchCompany(Long userId, Long companyId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setCompanyId(companyId);
        userMapper.updateById(userEntity);
    }

    private void importUserDeptAndPost(List<UserExcelDomain> userDomainList, Map<String, UserEntity> userIdMap,
                                       Map<String, Long> deptFullNameMap, Map<String, Long> postNameMap,
                                       String userType) {
        List<UserDeptSaveRequest> userDeptSaveRequestList = new ArrayList<>();
        List<UserPostCreateRequest> userPostCreateRequestList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        List<UserCompanySaveRequest> userCompanySaveRequestList = new ArrayList<>();
        List<UserInfoSaveRequest> userInfoSaveRequestList = new ArrayList<>();
        List<String> keyList = Arrays.stream(RosterEnum.values()).map(RosterEnum::getKey).collect(Collectors.toList());
        Long companyId = UserUtils.getUser().getCompanyId();
        for (UserExcelDomain userExcelDomain : userDomainList) {
            UserEntity userEntity = userIdMap.get(userExcelDomain.getUuid());
            if (userEntity == null) {
                continue;
            }
            Long userId = userEntity.getUserId();
            userIdList.add(userId);
            if (StringUtils.isNotBlank(userExcelDomain.getDeptName())) {
                String[] deptNameList = StringUtils.split(userExcelDomain.getDeptName(), ",");
                for (String deptName : deptNameList) {
                    Long deptId = deptFullNameMap.get(deptName.trim());
                    if (deptId != null) {
                        UserDeptSaveRequest userDeptSaveRequest = new UserDeptSaveRequest();
                        userDeptSaveRequest.setDeptId(deptId);
                        userDeptSaveRequest.setUserId(userId);
                        userDeptSaveRequestList.add(userDeptSaveRequest);
                    }
                }
            }
            UserCompanySaveRequest userCompanySaveRequest =
                    AbstractUserCompanyConverter.INSTANCE.toRequest(userExcelDomain);
            userCompanySaveRequest.setCompanyId(companyId);
            userCompanySaveRequest.setUserId(userId);
            userCompanySaveRequest.setUserType(userType);
            userCompanySaveRequestList.add(userCompanySaveRequest);
            if (StringUtils.isNotBlank(userExcelDomain.getPosition())) {
                Long postId = postNameMap.get(userExcelDomain.getPosition());
                if (postId != null) {
                    UserPostCreateRequest userPostCreateRequest = new UserPostCreateRequest();
                    userPostCreateRequest.setPostId(postId);
                    userPostCreateRequest.setUserId(userId);
                    userPostCreateRequestList.add(userPostCreateRequest);
                }
            }
            addUserInfo(userExcelDomain, keyList, userEntity, userInfoSaveRequestList);
        }

        if (CollectionUtils.isNotEmpty(userCompanySaveRequestList)) {
            userCompanyService.save(userCompanySaveRequestList);
        }
        if (CollectionUtils.isNotEmpty(userDeptSaveRequestList)) {
            userDeptService.save(userDeptSaveRequestList, userIdList);
        }
        if (CollectionUtils.isNotEmpty(userPostCreateRequestList)) {
            userPostService.save(userPostCreateRequestList, userIdList);
        }
        if (CollectionUtils.isNotEmpty(userInfoSaveRequestList)) {
            userInfoService.save(userInfoSaveRequestList);
        }
    }


    private void addUserInfo(UserExcelDomain userExcelDomain, List<String> keyList, UserEntity userEntity,
                             List<UserInfoSaveRequest> userInfoSaveRequestList) {
        Class<? extends UserExcelDomain> clazz = userExcelDomain.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                try {
                    String name = field.getName();
                    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = clazz.getMethod(methodName);
                    Object invoke = method.invoke(userExcelDomain);
                    if (invoke != null && keyList.contains(name)) {
                        UserInfoSaveRequest userInfoSaveRequest = new UserInfoSaveRequest();
                        userInfoSaveRequest.setInfoKey(name);
                        userInfoSaveRequest.setInfoValue(invoke.toString());
                        userInfoSaveRequest.setUserId(userEntity.getUserId());
                        userInfoSaveRequestList.add(userInfoSaveRequest);
                    }
                } catch (Exception e) {
                    log.error("获取字段值失败", e);
                }
            }
        } catch (Exception e) {
            log.error("处理class失败", e);
        }
    }

    private Map<String, UserEntity> importUser(List<UserExcelDomain> userDomainList, UserDomain user) {
        List<String> phonenumberList =
                userDomainList.stream().map(UserExcelDomain::getPhonenumber).collect(Collectors.toList());
        LambdaQueryWrapper<UserEntity> sameWrapper = new LambdaQueryWrapper<>();
        sameWrapper.in(UserEntity::getPhonenumber, phonenumberList);
        sameWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
        List<UserEntity> existList = userMapper.selectList(sameWrapper);
        List<UserEntity> userEntityList = new ArrayList<>();
        Map<String, UserEntity> phonenumberMap =
                existList.stream().collect(Collectors.toMap(UserEntity::getPhonenumber, c -> c));
        for (UserExcelDomain userDomain : userDomainList) {
            UserEntity userEntity = phonenumberMap.get(userDomain.getPhonenumber());
            if (userEntity == null) {
                userEntity = AbstractUserConverter.INSTANCE.toEntity(userDomain);
                userEntity.setUserName(userDomain.getPhonenumber());
                userEntity.setUserType("06");
                userEntity.setCreateBy(user.getUserName());
                userEntity.setStatus(Constants.NORMAL);
                userEntity.setUuid(IdUtils.simpleUUID());
                userEntity.setCompanyId(UserUtils.getUser().getCompanyId());
                userEntity.setUpdateBy(user.getUserName());
                userEntity.setPassword(PasswordUtil.encryptPassword(userDomain.getPhonenumber()));
            } else {
                userEntity.setNickName(userDomain.getNickName());
                userEntity.setEmail(userDomain.getEmail());
                userEntity.setUpdateBy(user.getUserName());
                userEntity.setUpdateTime(new Date());
            }
            userEntityList.add(userEntity);
            userDomain.setUuid(userEntity.getUuid());
        }
        saveOrUpdateBatch(userEntityList);
        return userEntityList.stream().collect(Collectors.toMap(UserEntity::getUuid, c -> c));
    }

    private Map<String, Long> importPost(List<String> positionList) {
        List<PostVO> postList = postService.importPost(positionList);
        return postList.stream().collect(Collectors.toMap(PostVO::getPostName, PostVO::getPostId));
    }

    private Map<String, Long> importDept(List<UserExcelDomain> userDomainList, String userType) {
        List<String> deptNameList =
                userDomainList.stream().map(UserExcelDomain::getDeptName).filter(StringUtils::isNotEmpty).distinct()
                        .collect(Collectors.toList());
        departmentService.importDeptName(deptNameList, userType);
        List<DepartmentVO> departmentVOList =
                departmentService.queryListNow(UserUtils.getUser().getCompanyId(), userType);
        return departmentVOList.stream().collect(Collectors.toMap(DepartmentVO::getFullName, DepartmentVO::getDeptId));
    }

    private void checkUserCount(Long companyId, Long addCount, String userType) {
        if (UserTypeEnum.EXTERNAL.getCode().equals(userType)) {
            return;
        }
        CompanyAppDetailVO companyApp = CompanyAppCache.getCompanyApp(companyId);
        ClientFunctionVO clientFunctionVO = companyApp.getClientFunctionMap().get(CompanyInfoKeyEnum.userLimit.name());
        Long count = userCompanyService.userCount(companyId, userType);
        CompanyAppVO companyAppVO = companyApp.getCompanyAppVO();
        if (companyApp.getExpire()) {
            if (10 < count + addCount) {
                String errorMessage = Constants.getErrorMessageApp(companyAppVO, "用户数量");
                throw new BizException(ResultCode.NO_AUTH_1, errorMessage);
            }
        } else {
            if (clientFunctionVO.getLimitCount() < count + addCount) {
                String errorMessage = Constants.getErrorMessageApp(companyAppVO, "用户数量");
                throw new BizException(ResultCode.NO_AUTH_1, errorMessage);
            }
        }
    }
}
