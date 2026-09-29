package com.wuji.open.components;

import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.open.enums.OpenResultCode;
import com.wuji.open.exception.OpenException;
import com.wuji.platform.model.request.SecretLogSaveRequest;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.platform.service.SecretLogService;
import com.wuji.platform.service.SecretService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class OpenPlatformComponent {

    @Autowired
    private UserService userService;

    @Autowired
    private SecretService secretService;

    @Autowired
    private SecretLogService secretLogService;

    public void cacheUserDomain(String creator, SecretVO secret) {
        UserDomain userDomain = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        userDomain.setCompanyId(secret.getCompanyId());
        UserUtils.setUser(userDomain);
        if (creator != null) {
            UserVO user = userService.queryCurrentCompanyUserByMobile(creator);
            if (user == null) {
                throw new OpenException(OpenResultCode.CURRENT_USER_NOT_EXIST);
            }
            userDomain.setUserId(user.getUserId().toString());
            userDomain.setNickName(user.getNickName());
            if (CollectionUtils.isNotEmpty(user.getDeptIdList())) {
                userDomain.setDeptIdList(user.getDeptIdList());
            } else {
                userDomain.setDeptIdList(new ArrayList<>());
            }
            UserUtils.setUser(userDomain);
        }
    }

    public SecretVO checkAndGetSecret(String appKey) {
        SecretVO secret = secretService.getSecret(appKey);
        if (secret == null) {
            throw new OpenException(OpenResultCode.APP_KEY_NOT_EXIST);
        }
        if ("CLOSE".equals(secret.getState())) {
            throw new OpenException(OpenResultCode.APP_KEY_CLOSE);
        }
        return secret;
    }

    public void saveSecretLog(String requestParam, SecretVO secret, String result, String apiName,
                              String errorMessage) {
        SecretLogSaveRequest secretLogSaveRequest = new SecretLogSaveRequest();
        secretLogSaveRequest.setRequestParam(requestParam);
        secretLogSaveRequest.setSecretId(secret.getId());
        secretLogSaveRequest.setResult(result);
        secretLogSaveRequest.setApiName(apiName);
        secretLogSaveRequest.setErrorMessage(errorMessage);
        secretLogService.save(secretLogSaveRequest);
    }

}
