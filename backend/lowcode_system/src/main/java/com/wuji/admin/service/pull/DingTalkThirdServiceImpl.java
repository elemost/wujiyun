package com.wuji.admin.service.pull;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetCorpAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetCorpAccessTokenResponse;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiServiceGetSuiteTokenRequest;
import com.dingtalk.api.response.OapiServiceGetSuiteTokenResponse;
import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DingTalkThirdService;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.properties.DingTalkProperties;
import com.wuji.common.utils.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("dingTalkThirdService")
@Slf4j
public class DingTalkThirdServiceImpl extends ThirdCommonServiceImpl implements DingTalkThirdService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RedisCache redisCache;

    private DingTalkProperties dingTalkProperties;

    private static String TICKET_KEY = "DING_SUITEID_";


    @Override
    public String dataSource() {
        return CompanyDataSourceEnum.DING_TALK_THIRD.name();
    }

    @Override
    public String getAccessToken(Long companyId) {
        CompanyVO companyVO = companyService.info(companyId);
        String pullConfig = companyVO.getPullConfig();
        DingTalkConfig dingTalkConfig = JSONObject.parseObject(pullConfig, DingTalkConfig.class);
        String key = "DING_AUTH_CORP_" + dingTalkConfig.getCorpId();
        Object cacheObject = redisCache.getCacheObject(key);
        if (cacheObject == null) {
            try {
                Client client = createClient();
                GetCorpAccessTokenRequest getCorpAccessTokenRequest =
                        new GetCorpAccessTokenRequest().setSuiteKey(dingTalkProperties.getSuiteId())
                                .setSuiteSecret(dingTalkProperties.getSuiteSecret())
                                .setAuthCorpId(dingTalkConfig.getCorpId()).setSuiteTicket(getTicket());
                GetCorpAccessTokenResponse corpAccessToken = client.getCorpAccessToken(getCorpAccessTokenRequest);
                String accessToken = corpAccessToken.getBody().getAccessToken();
                redisCache.setCacheObject(key, accessToken, 90, TimeUnit.MINUTES);
                return accessToken;
            } catch (Exception e) {
                log.error("获取钉钉企业token失败", e);
            }
            return null;
        } else {
            return cacheObject.toString();
        }
    }

    @Override
    public List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO) {
        return null;
    }

    @Override
    public List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO) {
        return null;
    }

    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        return null;
    }

    @Override
    public DepartmentPullVO getDeptById(String deptIdList, CompanyVO companyVO) {
        return null;
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        return null;
    }

    @Override
    public UserLoginVO getUserInfoByAssessToken(String assessToken) {
        return null;
    }


    // @Override
    // public JsapiAuthVO jsapiAuth(String secretId) {
    //     return null;
    // }

    @Override
    public void receive(String msg_signature, String timeStamp, String nonce, JSONObject json, String clientId) {

    }

    public String getSuiteToken() {
        Object suiteToken = redisCache.getCacheObject("DING_SUITE_TOKEN");
        String cacheObject = getTicket();
        if (suiteToken == null) {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_suite_token");
            OapiServiceGetSuiteTokenRequest req = new OapiServiceGetSuiteTokenRequest();
            req.setSuiteKey(dingTalkProperties.getSuiteId());
            req.setSuiteSecret(dingTalkProperties.getSuiteSecret());
            req.setSuiteTicket(cacheObject);
            try {
                OapiServiceGetSuiteTokenResponse rsp = client.execute(req);
                return rsp.getSuiteAccessToken();
            } catch (Exception e) {
                log.info("获取钉钉suiteToken失败", e);
            }
        }
        return null;
    }

    private String getTicket() {
        return redisCache.getCacheObject(TICKET_KEY + dingTalkProperties.getSuiteId()).toString();
    }

    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public static Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        return new Client(config);
    }
}
