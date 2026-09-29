package com.wuji.admin.service.pull;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.CompanyPullConfigCache;
import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.admin.client.wecom.model.AuthInfoRequest;
import com.wuji.admin.client.wecom.model.AuthInfoVO;
import com.wuji.admin.client.wecom.model.CorpTokenRequest;
import com.wuji.admin.client.wecom.model.CorpTokenVO;
import com.wuji.admin.client.wecom.model.JsApiTicketVO;
import com.wuji.admin.client.wecom.model.OrderInfoRequest;
import com.wuji.admin.client.wecom.model.OrderInfoVO;
import com.wuji.admin.client.wecom.model.PermanentInfoVO;
import com.wuji.admin.client.wecom.model.SuiteTokenRequest;
import com.wuji.admin.client.wecom.model.SuiteTokenVO;
import com.wuji.admin.client.wecom.model.UserInfoThirdVO;
import com.wuji.admin.client.wecom.model.WeComCreateOrderRequest;
import com.wuji.admin.client.wecom.model.WeComUserIdRequest;
import com.wuji.admin.client.wecom.model.WeComUserIdVO;
import com.wuji.admin.enums.CompanyChannelTypeEnum;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.model.entity.OrderServiceEntity;
import com.wuji.admin.model.entity.UserCompanyEntity;
import com.wuji.admin.model.request.CompanyRequest;
import com.wuji.admin.model.request.OrdersCreateRequest;
import com.wuji.admin.model.request.WeComSignatureRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.WeComSignatureVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.OrderServiceService;
import com.wuji.admin.service.OrdersService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.admin.utils.WXBizMsgCrypt;
import com.wuji.admin.utils.WeComCallbackCrypto;
import com.wuji.common.constant.Constants;
import com.wuji.common.constant.RedisKey;
import com.wuji.common.model.info.WeComConfig;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.properties.WeComProperties;
import com.wuji.common.utils.UserUtils;
import com.wuji.common.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("weComThirdServiceImpl")
public class WeComThirdServiceImpl extends WeComCommonServiceImpl implements WeComThirdService {

    @Autowired
    private WeComProperties weComProperties;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private WeComClient weComClient;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private OrderServiceService orderServiceService;

    @Override
    public String dataSource() {
        return CompanyDataSourceEnum.WECOM_THIRD.name();
    }

    @Override
    public Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                         String suiteId) {
        String echostr = json.getString("echostr").replaceAll(" ", "+");
        Map<String, Object> map = new HashMap<>();
        try {
            WeComProperties.WeComConfig weComConfig = weComProperties.getBySuiteId(suiteId);
            WXBizMsgCrypt wxBizMsgCrypt =
                    new WXBizMsgCrypt(weComConfig.getToken(), weComConfig.getAseKey(), weComConfig.getCorpId());
            String decryptMsg = wxBizMsgCrypt.VerifyURL(msg_signature, timeStamp, nonce, echostr);
            map.put("success", Long.valueOf(decryptMsg));
        } catch (Exception e) {
            log.error("suiteTicket失败", e);
        }
        return map;
    }

    @Override
    public String getAccessToken(Long companyId) {
        return null;
    }


    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        String accessToken = getAccessTokenBySuitId(companyVO.getCompanyId(), UserUtils.getUser().getSuiteId());
        return getUserPullVOS(ids, companyVO, accessToken);
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        String suiteToken = getSuiteToken(companyVO.getSuiteId());
        UserInfoThirdVO userInfoThirdVO = weComClient.userInfoThird(suiteToken, code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userInfoThirdVO.getUserid());
        jsonObject.put("corpId", userInfoThirdVO.getCorpid());
        return jsonObject.toJSONString();
    }

    @Override
    public void receive(String msg_signature, String timeStamp, String nonce, JSONObject json, String clientId) {
        String echoStr = json.getString("echostr").replaceAll(" ", "+");
        try {
            WeComProperties.WeComConfig weComConfig = weComProperties.getBySuiteId(clientId);
            WXBizMsgCrypt wxBizMsgCrypt =
                    new WXBizMsgCrypt(weComConfig.getToken(), weComConfig.getAseKey(), weComConfig.getSuiteId());
            Document xml = XmlUtil.parseXml(echoStr);
            String encrypt = xml.getElementsByTagName("Encrypt").item(0).getTextContent();
            String decryptMsg = wxBizMsgCrypt.VerifyURL(msg_signature, timeStamp, nonce, encrypt);
            if (decryptMsg == null) {
                return;
            }
            Document decryptXml = XmlUtil.parseXml(decryptMsg);
            String infoType = decryptXml.getElementsByTagName("InfoType").item(0).getTextContent();
            String suiteId = decryptXml.getElementsByTagName("SuiteId").item(0).getTextContent();
            if ("suite_ticket".equals(infoType)) {
                String suiteTicket = decryptXml.getElementsByTagName("SuiteTicket").item(0).getTextContent();
                redisCache.setCacheObject(RedisKey.getSuiteTicketKey(suiteId), suiteTicket);
            } else if ("create_auth".equals(infoType)) {
                createAuth(decryptXml, suiteId);
            } else if ("change_auth".equals(infoType)) {

            } else if ("change_contact".equals(infoType)) {
                String changeType = decryptXml.getElementsByTagName("ChangeType").item(0).getTextContent();
                String authCorpId = decryptXml.getElementsByTagName("AuthCorpId").item(0).getTextContent();
                CompanyVO companyVO = companyService.getBySecretId(Constants.getThirdWeComSecret(authCorpId, suiteId));
                cacheUser(companyVO);
                // changeContact(changeType, xml, companyVO, userDomain, dataSource());
            } else if ("pay_for_app_success".equals(infoType)) {
                String orderId = decryptXml.getElementsByTagName("OrderId").item(0).getTextContent();
                String corpId = decryptXml.getElementsByTagName("PaidCorpId").item(0).getTextContent();
                OrderInfoRequest orderInfoRequest = new OrderInfoRequest();
                orderInfoRequest.setOrderid(orderId);
                OrderInfoVO orderInfo = weComClient.getOrderInfo(orderInfoRequest, getSuiteToken(suiteId));
                CompanyRequest companyRequest = new CompanyRequest();
                companyRequest.setDataSource(CompanyDataSourceEnum.WECOM_THIRD.name());
                companyRequest.setCompanyType((short) 1);
                companyRequest.setSecretId(Constants.getThirdWeComSecret(corpId, suiteId));
                companyRequest.setChannelType(CompanyChannelTypeEnum.WECOM_THIRD.name());
                companyRequest.setSuiteId(suiteId);
                companyRequest.setCorpId(corpId);
                companyService.createCompany(companyRequest);
                CompanyVO companyVO = companyService.getBySecretId(companyRequest.getSecretId());
                cacheUser(companyVO);
                OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest();
                ordersCreateRequest.setCreator(orderInfo.getOperator_id());
                ordersCreateRequest.setItemCode(orderInfo.getEdition_id());
                ordersCreateRequest.setTotalPrice(
                        orderInfo.getService_share_amount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                ordersCreateRequest.setOrderId(orderInfo.getOrderid());
                ordersCreateRequest.setUserCount(orderInfo.getUser_count());
                ordersCreateRequest.setOrderPeriod(orderInfo.getOrder_period());
                Long order = ordersService.generateOrder(ordersCreateRequest);
                WeComCreateOrderRequest weComCreateOrderRequest = new WeComCreateOrderRequest();
                weComCreateOrderRequest.setCorpid(orderInfo.getPaid_corpid());
                weComCreateOrderRequest.setBuyer_userid(orderInfo.getOperator_id());
                try {
                    WeComCreateOrderRequest.AccountCount accountCount = new WeComCreateOrderRequest.AccountCount();
                    accountCount.setBase_count(orderInfo.getUser_count());
                    accountCount.setExternal_contact_count(0);
                    weComCreateOrderRequest.setAccount_count(accountCount);
                    WeComCreateOrderRequest.AccountDuration accountDuration =
                            new WeComCreateOrderRequest.AccountDuration();
                    if ("wpRh6fBgAAPKsRVMonX9H7YZc-9mu73A".equals(weComCreateOrderRequest.getCorpid())) {
                        accountDuration.setMonths(1);
                        accountDuration.setDays(0);
                    } else {
                        accountDuration.setMonths(1);
                        accountDuration.setDays(orderInfo.getOrder_period() - 31);
                    }
                    weComCreateOrderRequest.setAccount_duration(accountDuration);
                    weComClient.crateNewOrder(weComCreateOrderRequest, getSuiteToken(suiteId));
                    OrderServiceEntity orderServiceEntity = new OrderServiceEntity();
                    orderServiceEntity.setOrderId(order);
                    orderServiceEntity.setServiceCode("WE_CHAT_PASS");
                    orderServiceEntity.setDays(orderInfo.getOrder_period());
                    orderServiceService.save(orderServiceEntity);
                } catch (Exception e) {
                    log.error("创建订单失败", e);
                }
            }
        } catch (Exception e) {
            log.error("suiteTicket失败");
        }
    }

    private void createAuth(Document decryptXml, String suiteId) {
        String authCode = decryptXml.getElementsByTagName("AuthCode").item(0).getTextContent();
        String suiteToken = getSuiteToken(suiteId);
        Map<String, String> permanentMap = new HashMap<>();
        permanentMap.put("auth_code", authCode);
        PermanentInfoVO permanentInfo = weComClient.getPermanentCode(suiteToken, permanentMap);
        log.info("permanentInfo:" + JSONObject.toJSONString(permanentInfo));

        PermanentInfoVO.Corp authCorpInfo = permanentInfo.getAuth_corp_info();
        AuthInfoRequest authInfoRequest = new AuthInfoRequest();
        authInfoRequest.setAuth_corpid(authCorpInfo.getCorpid());
        authInfoRequest.setPermanent_code(permanentInfo.getPermanent_code());
        AuthInfoVO authInfo = weComClient.getAuthInfo(suiteToken, authInfoRequest);
        log.info("authInfo:" + JSONObject.toJSONString(authInfo));
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyName(authCorpInfo.getCorp_name());
        WeComConfig weComConfig = new WeComConfig();
        weComConfig.setCorpId(authCorpInfo.getCorpid());
        weComConfig.setPermanentCode(permanentInfo.getPermanent_code());
        weComConfig.setClientId(authInfo.getAuth_info().getAgent().get(0).getAgentid());
        companyRequest.setDataSource(CompanyDataSourceEnum.WECOM_THIRD.name());
        companyRequest.setPullConfig(JSONObject.toJSONString(weComConfig));
        companyRequest.setCompanyType((short) 1);
        companyRequest.setSecretId(Constants.getThirdWeComSecret(weComConfig.getCorpId(), suiteId));
        companyRequest.setChannelType(CompanyChannelTypeEnum.WECOM_THIRD.name());
        companyRequest.setSuiteId(suiteId);
        companyRequest.setCorpId(weComConfig.getCorpId());
        companyService.createCompany(companyRequest);
        String key = RedisKey.getSuiteCorpTokenKey(suiteId, weComConfig.getCorpId());
        redisCache.deleteObject(key);
    }

    public String getSuiteToken(String suiteId) {
        String suiteTokenKey = RedisKey.getSuiteTokenKey(suiteId);
        Object suiteToken = redisCache.getCacheObject(suiteTokenKey);
        if (suiteToken == null) {
            Object cacheObject = redisCache.getCacheObject(RedisKey.getSuiteTicketKey(suiteId));
            WeComProperties.WeComConfig weComConfig = weComProperties.getBySuiteId(suiteId);
            SuiteTokenRequest suiteTokenRequest = new SuiteTokenRequest();
            suiteTokenRequest.setSuite_ticket(cacheObject.toString());
            suiteTokenRequest.setSuite_id(weComConfig.getSuiteId());
            suiteTokenRequest.setSuite_secret(weComConfig.getSuiteSecret());
            SuiteTokenVO suiteTokenVO = weComClient.getSuiteToken(suiteTokenRequest);
            redisCache.setCacheObject(suiteTokenKey, suiteTokenVO.getSuite_access_token(), 90, TimeUnit.MINUTES);
            return suiteTokenVO.getSuite_access_token();
        } else {
            return suiteToken.toString();
        }
    }

    @Override
    public String getThirdId(String phoneNumber) {
        try {
            String accessToken =
                    getAccessTokenBySuitId(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getSuiteId());
            WeComUserIdRequest weComUserIdRequest = new WeComUserIdRequest();
            weComUserIdRequest.setMobile(phoneNumber);
            WeComUserIdVO weComUserIdVO = weComClient.getUserId(weComUserIdRequest, accessToken);
            return weComUserIdVO.getUserid();
        } catch (Exception e) {
            log.error("获取不到企业微信id", e);
            return null;
        }
    }

    @Override
    public String getAccessTokenBySuitId(Long companyId, String suiteId) {
        CompanyPullConfigVO companyPullConfigVO = CompanyPullConfigCache.getValue(companyId, suiteId);
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        String key = RedisKey.getSuiteCorpTokenKey(suiteId, weComConfig.getCorpId());
        Object cacheObject = redisCache.getCacheObject(key);
        if (cacheObject == null) {
            CorpTokenRequest corpTokenRequest = new CorpTokenRequest();
            corpTokenRequest.setAuth_corpid(weComConfig.getCorpId());
            corpTokenRequest.setPermanent_code(weComConfig.getPermanentCode());
            String suiteToken = getSuiteToken(suiteId);
            CorpTokenVO corpToken = weComClient.getCorpToken(suiteToken, corpTokenRequest);
            redisCache.setCacheObject(key, corpToken.getAccess_token(), 90, TimeUnit.MINUTES);
            return corpToken.getAccess_token();
        } else {
            return cacheObject.toString();
        }
    }

    @Override
    public WeComSignatureVO geAppSignature(WeComSignatureRequest weComSignatureRequest) {
        CompanyPullConfigVO companyPullConfigVO =
                CompanyPullConfigCache.getValue(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getSuiteId());
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        String nonestr = RandomStringUtils.randomAlphanumeric(16);
        long time = new Date().getTime() / 1000;
        String signature =
                WeComCallbackCrypto.generateSignature(getAppJsApiTicket(UserUtils.getUser().getSuiteId()), nonestr,
                        time, weComSignatureRequest.getUrl());
        WeComSignatureVO weComSignatureVO = new WeComSignatureVO();
        weComSignatureVO.setSignature(signature);
        weComSignatureVO.setTimestamp(time);
        weComSignatureVO.setNoncestr(nonestr);
        weComSignatureVO.setAgentId(weComConfig.getClientId());
        weComSignatureVO.setCorpId(weComConfig.getCorpId());
        return weComSignatureVO;
    }

    @Override
    public WeComSignatureVO getCompanySignature(WeComSignatureRequest weComSignatureRequest) {
        CompanyPullConfigVO companyPullConfigVO =
                CompanyPullConfigCache.getValue(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getSuiteId());
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        String nonestr = RandomStringUtils.randomAlphanumeric(16);
        long time = new Date().getTime() / 1000;
        String companyJsApiTicket = getCompanyJsApiTicket(companyPullConfigVO.getSourceAppId());
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

    private String getCompanyJsApiTicket(String suiteId) {
        CompanyPullConfigVO companyPullConfigVO =
                CompanyPullConfigCache.getValue(UserUtils.getUser().getCompanyId(), suiteId);
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        String ticketKey = "wecom_third_ticket_company_" + weComConfig.getCorpId() + "_" + suiteId;
        String ticket = redisCache.getCacheObject(ticketKey);
        if (ticket == null) {
            String accessToken = getAccessTokenBySuitId(UserUtils.getUser().getCompanyId(), suiteId);
            JsApiTicketVO jsApiTicket = weComClient.getCompanyJsApiTicket(accessToken);
            ticket = jsApiTicket.getTicket();
            redisCache.setCacheObject(ticketKey, ticket, 90, TimeUnit.MINUTES);
        }
        return ticket;
    }

    private String getAppJsApiTicket(String suiteId) {
        log.info("suiteId=" + suiteId);
        CompanyPullConfigVO companyPullConfigVO =
                CompanyPullConfigCache.getValue(UserUtils.getUser().getCompanyId(), suiteId);
        WeComConfig weComConfig = JSONObject.parseObject(companyPullConfigVO.getPullConfig(), WeComConfig.class);
        String ticketKey = "wecom_third_ticket_app_" + weComConfig.getCorpId() + "_" + suiteId;
        String ticket = redisCache.getCacheObject(ticketKey);
        if (ticket == null) {
            String accessToken = getAccessTokenBySuitId(UserUtils.getUser().getCompanyId(), suiteId);
            JsApiTicketVO agentConfig = weComClient.getTicket(accessToken, "agent_config");
            ticket = agentConfig.getTicket();
            redisCache.setCacheObject(ticketKey, ticket, 90, TimeUnit.MINUTES);
        }
        return ticket;
    }

    @Override
    public void pullThirdId() {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        List<UserCompanyVO> allUser = userCompanyService.getAllUser();
        Map<Long, UserCompanyVO> userIdToMap =
                allUser.stream().collect(Collectors.toMap(UserCompanyVO::getUserId, c -> c));
        List<Long> userIdList = allUser.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userService.infoOnly(userIdList);
        String accessToken = getAccessTokenBySuitId(info.getCompanyId(), UserUtils.getUser().getSuiteId());
        for (UserVO userVO : userVOS) {
            UserCompanyVO userCompanyVO = userIdToMap.get(userVO.getUserId());
            if (userCompanyVO != null) {
                if (UserTypeEnum.INTERNAL.getCode().equals(userCompanyVO.getUserType()) &&
                        userCompanyVO.getWeComUserId() == null) {
                    WeComUserIdRequest weComUserIdRequest = new WeComUserIdRequest();
                    weComUserIdRequest.setMobile(userVO.getPhonenumber());
                    UserCompanyEntity userCompanyEntity = new UserCompanyEntity();
                    try {
                        WeComUserIdVO weComUserIdVO = weComClient.getUserId(weComUserIdRequest, accessToken);
                        userCompanyEntity.setWeComUserId(weComUserIdVO.getUserid());
                        userCompanyEntity.setThirdId(weComUserIdVO.getUserid());
                    } catch (Exception e) {
                        log.info("用户不存在" + userVO.getPhonenumber(), e);
                        userCompanyEntity.setWeComUserId("");
                    }
                    userCompanyEntity.setId(userCompanyVO.getId());
                    userCompanyEntity.setThirdType(info.getDataSource());
                    userCompanyService.updateById(userCompanyEntity);
                }
            }
        }
    }
}
