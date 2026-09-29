package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.info.UserScope;
import com.wuji.admin.model.request.UserCompleteRequest;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserInviteRequest;
import com.wuji.admin.model.request.UserPhoneUpdateRequest;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.request.UserUpdatePasswordRequest;
import com.wuji.admin.model.request.UserUpdateRequest;
import com.wuji.admin.model.request.UserWeComCompleteRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserImportVO;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
public interface UserService extends IService<UserEntity> {

    QueryPageVO<UserReturnVO> queryList(UserRequest userRequest);


    QueryPageVO<UserVO> querySelectList(UserSelectRequest userRequest);

    /**
     * 登录专用
     *
     * @param userName
     * @return
     */
    UserVO queryByUserName(String userName);

    UserVO queryByMobile(String mobile);

    UserVO queryByUuid(String uuid);

    UserVO queryCurrentCompanyUserByMobile(String mobile);

    List<UserVO> queryByIds(List<Long> userIdList);

    Map<Long, String> getIdToNameMap(List<Long> userIdList);

    UserVO info(Long userId);

    UserVO infoOnly(Long userId);

    List<UserVO> infoOnly(List<Long> userIdList);

    void pullData(MultipartFile file);

    void saveDingUserInfo(List<UserPullVO> userDingVOS, UserDomain user, List<DepartmentVO> departmentVOList,
                          String importType);

    Map<String, UserEntity> saveUserBatchByPhone(List<UserPullVO> userDingVOS, UserDomain user, String source);

    void saveUserCompanyList(List<UserPullVO> userDingVOS, CompanyVO info, Map<String, UserEntity> userIdMap,
                             String userType, Boolean admin);

    Long create(UserCreateRequest userCreateRequest);

    Long onlyCreateUser(UserCreateRequest userCreateRequest);

    void update(UserUpdateRequest userUpdateRequest, Boolean roster);

    void delete(String uuid);

    UserImportVO importFile(MultipartFile file, String userType);

    void downloadTemplate(HttpServletResponse response);

    void switchCompany(Long userId, Long companyId);

    String invite(UserInviteRequest userInviteRequest);

    Long complete(UserCompleteRequest userCompleteRequest);

    Long getAdmin();

    void updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest);

    List<Long> getUserByScope(List<UserScope> scopeList);

    UserVO getByCompanyAndUserId(Long companyId, Long userId);

    List<UserVO> queryByMobilePhone(List<String> mobileList);

    void updatePhone(UserPhoneUpdateRequest userPhoneUpdateRequest);

    void weComComplete(UserWeComCompleteRequest userWeComCompleteRequest);

    void bindWeComId(List<Long> userIdList);

    void initUser(Long companyId);
}

