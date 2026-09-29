package com.wuji.admin.controller;


import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.admin.aspect.corp.annotation.SyncCompany;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.converter.AbstractUserConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.request.UserCompleteRequest;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserInviteRequest;
import com.wuji.admin.model.request.UserPhoneUpdateRequest;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.request.UserUpdatePasswordRequest;
import com.wuji.admin.model.request.UserUpdateRequest;
import com.wuji.admin.model.request.UserWeComCompleteRequest;
import com.wuji.admin.model.vo.CorpCoopVO;
import com.wuji.admin.model.vo.UserImportVO;
import com.wuji.admin.model.vo.UserReturnInfoVO;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.admin.service.CorpCoopUserService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.Response;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CorpCoopUserService corpCoopUserService;

    @ApiOperation("获取用户列表")
    @PostMapping("/list")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<UserReturnVO> list(@RequestBody UserRequest userRequest) {
        userRequest.setStatus("0");
        return userService.queryList(userRequest);
    }

    @ApiOperation("获取用户列表")
    @PostMapping("/selectList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<UserVO> selectList(@RequestBody UserSelectRequest userRequest) {
        return userService.querySelectList(userRequest);
    }

    @ApiOperation("获取当前用户信息")
    @PostMapping("/currentUser")
    public UserReturnInfoVO currentUser() {
        UserDomain loginUser = UserUtils.getUser();
        UserVO userVO = userService.info(loginUser.getUserIdLongValue());
        UserReturnInfoVO userLoginInfoVO = AbstractUserConverter.INSTANCE.toLoginVO(userVO);
        List<CorpCoopVO> corpCompany = corpCoopUserService.getCorpCompany();
        userLoginInfoVO.setCorpCompanyList(corpCompany);
        userLoginInfoVO.setStartTime(loginUser.getStartTime());
        userLoginInfoVO.setEndTime(loginUser.getEndTime());
        userLoginInfoVO.setPhonenumber(StringUtil.maskPhoneNumber(userVO.getPhonenumber()));
        userLoginInfoVO.setUserName(StringUtil.maskPhoneNumber(userVO.getUserName()));
        return userLoginInfoVO;
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/info/{userId}")
    public UserVO info(@PathVariable Long userId) {
        UserVO info = userService.info(userId);
        return info;
    }

    @ApiOperation("通过id获取用户信息")
    @PostMapping("/listById")
    public List<UserVO> info(@RequestBody List<Long> userIdList) {
        List<UserVO> userVOS = userService.queryByIds(userIdList);
        for (UserVO userVO : userVOS) {
            userVO.setPhonenumber(StringUtil.maskPhoneNumber(userVO.getPhonenumber()));
        }
        return userVOS;
    }

    @ApiOperation("创建用户")
    @PostMapping("/create")
    @SyncCompany
    public Response<Long> create(@RequestBody UserCreateRequest userCreateRequest) {
        if ("06".equals(userCreateRequest.getUserType())) {
            userCreateRequest.setUserType("00");
        }
        return Response.success(userService.create(userCreateRequest));
    }

    @ApiOperation("修改用户")
    @PostMapping("/update")
    public void update(@RequestBody UserUpdateRequest userUpdateRequest) {
        boolean validPhoneNumber = StringUtil.isValidPhoneNumber(userUpdateRequest.getPhonenumber());
        if (StringUtils.isNotEmpty(userUpdateRequest.getPhonenumber()) && !validPhoneNumber) {
            throw new AdminException(AdminResultCode.PHONE_NUMBER_ERROR);
        }
        userService.update(userUpdateRequest, Boolean.FALSE);
    }

    @ApiOperation("修改用户密码")
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        userService.updatePassword(userUpdatePasswordRequest);
    }

    @ApiOperation("修改用户")
    @PostMapping("/update/roster")
    public void updateRoster(@RequestBody UserUpdateRequest userUpdateRequest) {
        boolean validPhoneNumber = StringUtil.isValidPhoneNumber(userUpdateRequest.getPhonenumber());
        if (StringUtils.isNotEmpty(userUpdateRequest.getPhonenumber()) && !validPhoneNumber) {
            throw new AdminException(AdminResultCode.PHONE_NUMBER_ERROR);
        }
        userService.update(userUpdateRequest, Boolean.TRUE);
    }

    @ApiOperation("删除用户")
    @PostMapping("/delete/{uuid}")
    @SyncCompany
    public void delete(@PathVariable String uuid) {
        userService.delete(uuid);
    }

    @ApiOperation("拉取数据")
    @PostMapping("/pullData")
    public void pullData(MultipartFile file) {
        log.info("拉取用户数据开始");
        userService.pullData(file);
        log.info("拉取用户数据结束");
    }

    @ApiOperation("导入用户数据")
    @PostMapping("/import")
    @SyncCompany
    public UserImportVO importFile(MultipartFile multipartFile, String userType) {
        UserImportVO userImportVO = userService.importFile(multipartFile, userType);
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
        return userImportVO;
    }

    @ApiOperation("导入用户数据模板")
    @GetMapping("/template")
    public void importFile(HttpServletResponse response) {
        userService.downloadTemplate(response);
    }

    @ApiOperation("邀请用户")
    @PostMapping("/invite")
    public Response<String> invite(@RequestBody UserInviteRequest userInviteRequest) {
        String inviteCode = userService.invite(userInviteRequest);
        // 发送邮件
        if (inviteCode != null) {

        }
        return Response.success(inviteCode);
    }

    @ApiOperation("完善用户信息")
    @PostMapping("/complete")
    public Response<Long> complete(@RequestBody UserCompleteRequest userCompleteRequest) {
        Long companyId = userService.complete(userCompleteRequest);
        Response<Long> success = Response.success(companyId);
        Map<String, Object> otherData = new HashMap<>();
        otherData.put("userId", UserUtils.getUser().getUserIdLongValue());
        success.setOtherData(otherData);
        return success;
    }

    @ApiOperation("修改手机号码")
    @PostMapping("/updatePhone")
    public void updatePhone(@RequestBody UserPhoneUpdateRequest userPhoneUpdateRequest) {
        userService.updatePhone(userPhoneUpdateRequest);
    }

    @ApiOperation("三方平台登录后需完善用户信息")
    @PostMapping("/weCom/complete")
    public void weComComplete(@RequestBody UserWeComCompleteRequest userWeComCompleteRequest) {
        userService.weComComplete(userWeComCompleteRequest);
    }

    @PostMapping(value = "/bindThirdWeComId")
    public void bindWeComId(@RequestBody List<Long> userIdList) {
        userService.bindWeComId(userIdList);
    }
}
