package com.wuji.admin.service.pull;


import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.cache.WeComCache;
import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.admin.client.wecom.model.DepartmentInfoResult;
import com.wuji.admin.client.wecom.model.JsApiTicketVO;
import com.wuji.admin.client.wecom.model.UserInfoByCodeResult;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.model.request.WeComSignatureRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.WeComSignatureVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.WeComService;
import com.wuji.admin.utils.DingCallbackCrypto;
import com.wuji.admin.utils.WeComCallbackCrypto;
import com.wuji.admin.utils.XMLParse;
import com.wuji.common.model.info.WeComConfig;
import com.wuji.common.utils.UserUtils;
import com.wuji.common.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("weComServiceImpl")
@Slf4j
@DS("slave")
public class WeComServiceImpl extends WeComCommonServiceImpl implements WeComService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private RedisCache redisCache;


    @Override
    public String dataSource() {
        return CompanyDataSourceEnum.WECOM.name();
    }

    @Override
    public Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                         String clientId) {
        CompanyVO companyVO = companyService.getBySecretId(clientId);
        WeComConfig weComConfig = JSONObject.parseObject(companyVO.getPullConfig(), WeComConfig.class);
        String echostr = json.getString("echostr");

        Map<String, Object> map = new HashMap<>();
        try {
            DingCallbackCrypto callbackCrypto =
                    new DingCallbackCrypto(weComConfig.getAesToken(), weComConfig.getAesKey(), weComConfig.getCorpId());
            String decryptMsg = callbackCrypto.getDecryptMsg(msg_signature, timeStamp, nonce, echostr);
            map.put("decrypt", decryptMsg);
            return map;
        } catch (Exception e) {
            log.error("获取钉钉推送失败", e);
        }
        return map;
    }

    @Override
    public String getAccessToken(Long companyId) {
        CompanyVO companyVO = companyService.info(companyId);
        WeComConfig weComConfig = JSONObject.parseObject(companyVO.getPullConfig(), WeComConfig.class);
        return weComClient.getAccessToken(weComConfig.getCorpId(), weComConfig.getClientSecret()).getAccess_token();
    }

    @Override
    public List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO) {
        String accessToken = WeComCache.getAccessToken(companyVO.getCompanyId());
        return getDepartmentPullVOS(deptId, accessToken);
    }

    @Override
    public List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO) {
        String accessToken = WeComCache.getAccessToken(companyVO.getCompanyId());
        return getAllUser(deptId, companyVO, accessToken);
    }

    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        String accessToken = WeComCache.getAccessToken(companyVO.getCompanyId());
        return getUserPullVOS(ids, companyVO, accessToken);
    }

    @Override
    public DepartmentPullVO getDeptById(String deptId, CompanyVO companyVO) {
        String accessToken = WeComCache.getAccessToken(companyVO.getCompanyId());
        DepartmentInfoResult weComResult = weComClient.departmentInfo(accessToken, deptId);
        return buildDeptPull(weComResult.getDepartment());
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        String accessToken = WeComCache.getAccessToken(companyVO.getCompanyId());
        UserInfoByCodeResult infoByAccessToken = weComClient.getInfoByAccessToken(accessToken, code);
        return infoByAccessToken.getUserid();
    }


    @Override
    public void receive(String msg_signature, String timeStamp, String nonce, JSONObject json, String clientId) {
        CompanyVO companyVO = companyService.getBySecretId(clientId);
        WeComConfig weComConfig = JSONObject.parseObject(companyVO.getPullConfig(), WeComConfig.class);
        String echostr = json.getString("echostr");
        try {
            DingCallbackCrypto callbackCrypto =
                    new DingCallbackCrypto(weComConfig.getAesToken(), weComConfig.getAesKey(), weComConfig.getCorpId());
            Object[] encrypt = XMLParse.extract(echostr);
            String decryptMsg = callbackCrypto.getDecryptMsg(msg_signature, timeStamp, nonce, encrypt[1].toString());
            log.info(decryptMsg);
            Document xml = XmlUtil.parseXml(decryptMsg);
            String changeType = xml.getElementsByTagName("ChangeType").item(0).getTextContent();
            String msgType = xml.getElementsByTagName("MsgType").item(0).getTextContent();
            String event = xml.getElementsByTagName("Event").item(0).getTextContent();
            cacheUser(companyVO);
            if ("event".equals(msgType)) {
                if ("change_contact".equals(event)) {
                    changeContact(changeType, xml, companyVO, UserUtils.getUser(), dataSource());
                }
            }
        } catch (Exception e) {
            log.error("获取钉钉推送失败", e);
        }
    }

    @Override
    public String getSuiteToken() {
        return null;
    }

    private String getCompanyJsApiTicket(Long companyId) {
        CompanyVO info = companyService.info(companyId);
        String ticketKey = "wecom_ticket_company_" + info.getSecretId();
        String ticket = redisCache.getCacheObject(ticketKey);
        if (ticket == null) {
            String accessToken = WeComCache.getAccessToken(info.getCompanyId());
            JsApiTicketVO jsApiTicket = weComClient.getCompanyJsApiTicket(accessToken);
            ticket = jsApiTicket.getTicket();
            redisCache.setCacheObject(ticketKey, ticket, 90, TimeUnit.MINUTES);
        }
        return ticket;
    }

    private String getAppJsApiTicket(Long companyId) {
        CompanyVO info = companyService.info(companyId);
        String ticketKey = "wecom_ticket_app_" + info.getSecretId();
        String ticket = redisCache.getCacheObject(ticketKey);
        if (ticket == null) {
            String accessToken = WeComCache.getAccessToken(info.getCompanyId());
            JsApiTicketVO agentConfig = weComClient.getTicket(accessToken, "agent_config");
            ticket = agentConfig.getTicket();
            redisCache.setCacheObject(ticketKey, ticket, 90, TimeUnit.MINUTES);
        }
        return ticket;
    }

    @Override
    public WeComSignatureVO getCompanySignature(WeComSignatureRequest weComSignatureRequest) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());

        WeComConfig weComConfig = JSONObject.parseObject(info.getPullConfig(), WeComConfig.class);
        String nonestr = RandomStringUtils.randomAlphanumeric(16);
        long time = new Date().getTime() / 1000;
        String companyJsApiTicket = getCompanyJsApiTicket(UserUtils.getUser().getCompanyId());
        String signature = WeComCallbackCrypto.generateSignature(companyJsApiTicket, nonestr, time,
                weComSignatureRequest.getUrl());
        WeComSignatureVO weComSignatureVO = new WeComSignatureVO();
        weComSignatureVO.setSignature(signature);
        weComSignatureVO.setTimestamp(time);
        weComSignatureVO.setNoncestr(nonestr);
        weComSignatureVO.setAgentId(weComConfig.getClientId());
        weComSignatureVO.setCorpId(weComConfig.getCorpId());
        return weComSignatureVO;
    }

    @Override
    public WeComSignatureVO geAppSignature(WeComSignatureRequest weComSignatureRequest) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        WeComConfig weComConfig = JSONObject.parseObject(info.getPullConfig(), WeComConfig.class);
        String nonestr = RandomStringUtils.randomAlphanumeric(16);
        long time = new Date().getTime() / 1000;
        String signature =
                WeComCallbackCrypto.generateSignature(getAppJsApiTicket(UserUtils.getUser().getCompanyId()), nonestr,
                        time, weComSignatureRequest.getUrl());
        WeComSignatureVO weComSignatureVO = new WeComSignatureVO();
        weComSignatureVO.setSignature(signature);
        weComSignatureVO.setTimestamp(time);
        weComSignatureVO.setNoncestr(nonestr);
        weComSignatureVO.setAgentId(weComConfig.getClientId());
        weComSignatureVO.setCorpId(weComConfig.getCorpId());
        return weComSignatureVO;
    }

}
