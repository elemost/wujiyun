package com.wuji.admin.start;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.api.CompanyPluginApi;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.start.handler.StartHandler;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.PublicIpUtil;
import com.wuji.common.utils.UserUtils;
import com.wuji.systemapi.client.user.SystemClient;
import com.wuji.systemapi.client.user.model.CompanySyncRequest;
import com.wuji.systemapi.client.user.model.SyncCompanyInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("companyInitHandlerImpl")
@Slf4j
public class UserInitHandlerImpl implements StartHandler {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyAppService companyAppService;

    @Autowired
    private SystemClient systemClient;

    @Autowired
    private CompanyPluginApi companyPluginApi;

    @Override
    public void initAction() {
        Long companyId = companyService.initCompany();
        if (companyId == null) {
            return;
        }
        companyPluginApi.init(companyId);
        userService.initUser(companyId);
        try {
            CompanyVO info = companyService.info(companyId);
            UserDomain user = new UserDomain();
            user.setCompanyId(companyId);
            user.setCompanyUuid(info.getCompanyUuid());
            UserUtils.setUser(user);
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
            queryWrapper.eq(UserEntity::getCompanyId, companyId);
            long count = userService.count(queryWrapper);
            CompanySyncRequest companySyncRequest = new CompanySyncRequest();
            companySyncRequest.setCompanyUuid(UserUtils.getUser().getCompanyUuid());
            companySyncRequest.setCompanyName(info.getCompanyName());
            companySyncRequest.setCreateTime(info.getCreateTime());
            companySyncRequest.setUserCount((int) count);
            CompanyAppVO currentInfo = companyAppService.getCurrentInfo();
            if (currentInfo != null) {
                companySyncRequest.setClientId(currentInfo.getClientId());
                companySyncRequest.setEquityId(currentInfo.getEquityId());
                companySyncRequest.setStartTime(currentInfo.getStartTime());
                companySyncRequest.setEndTime(currentInfo.getEndTime());
            }
            companySyncRequest.setRemoteAddr(PublicIpUtil.getPublicIp());
            SyncCompanyInfoRequest syncCompanyInfoRequest = new SyncCompanyInfoRequest();
            String encrypt = AESUtils.encrypt(info.getCompanyUuid(), JSONObject.toJSONString(companySyncRequest));
            syncCompanyInfoRequest.setSyncData(encrypt);
            syncCompanyInfoRequest.setCompanyUuid(info.getCompanyUuid());
            syncCompanyInfoRequest.setUuid(PublicIpUtil.getInitUuid());
            systemClient.syncCompanyInfo(syncCompanyInfoRequest);
        } catch (Exception e) {
            log.error("同步数据失败", e);
        }
    }
}
