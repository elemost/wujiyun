package com.wuji.admin.aspect.corp.aspect;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.systemapi.client.user.SystemClient;
import com.wuji.systemapi.client.user.model.CompanySyncRequest;
import com.wuji.systemapi.client.user.model.SyncCompanyInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
@Slf4j
public class SyncCompanyInfoAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemClient systemClient;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyAppService companyAppService;

    @After("execution(public * com.wuji.admin.controller..*.*(..)) && @annotation(com.wuji.admin.aspect.corp.annotation.SyncCompany)")
    // 匹配需要拦截的方法
    public void afterMethod() {
        try {
            CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getDelFlag, Constants.NU_DELETED);
            queryWrapper.eq(UserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
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
            SyncCompanyInfoRequest syncCompanyInfoRequest = new SyncCompanyInfoRequest();
            String encrypt = AESUtils.encrypt(info.getCompanyUuid(), JSONObject.toJSONString(companySyncRequest));
            syncCompanyInfoRequest.setSyncData(encrypt);
            systemClient.syncCompanyInfo(syncCompanyInfoRequest);
        } catch (Exception e) {
            log.error("同步数据失败", e);
        }

    }
}
