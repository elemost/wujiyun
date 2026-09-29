package com.wuji.admin.service.pull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.CreateJsapiTicketHeaders;
import com.aliyun.dingtalkoauth2_1_0.models.CreateJsapiTicketResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetbyunionidRequest;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.request.OapiV2UserGetuserinfoRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiUserGetbyunionidResponse;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.taobao.api.ApiException;
import com.wuji.admin.cache.DingTalkCache;
import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.JsapiAuthVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.DingTalkService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.utils.DingCallbackCrypto;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("dingTalkService")
@Slf4j
public class DingTalkServiceImpl extends ThirdCommonServiceImpl implements DingTalkService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;


    @Override
    public String dataSource() {
        return CompanyDataSourceEnum.DING_TALK.name();
    }

    @Override
    public Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                         String clientId) {
        try {


            // 1. 从http请求中获取加解密参数

            // 2. 使用加解密类型
            // Constant.OWNER_KEY 说明：
            // 1、开发者后台配置的订阅事件为应用级事件推送，此时OWNER_KEY为应用的APP_KEY。
            // 2、调用订阅事件接口订阅的事件为企业级事件推送，
            //      此时OWNER_KEY为：企业的appkey（企业内部应用）或SUITE_KEY（三方应用）

            String encryptMsg = json.getString("encrypt");
            CompanyVO companyVO = companyService.getBySecretId(clientId);
            if (!CompanyDataSourceEnum.DING_TALK.name().equals(companyVO.getDataSource())) {
                return null;
            }
            DingTalkConfig dingTalkConfig = JSONObject.parseObject(companyVO.getPullConfig(), DingTalkConfig.class);
            String AES_TOKEN = dingTalkConfig.getAesToken();
            String AES_KEY = dingTalkConfig.getAesKey();
            String OWNER_KEY = dingTalkConfig.getClientId();
            DingCallbackCrypto callbackCrypto = new DingCallbackCrypto(AES_TOKEN, AES_KEY, OWNER_KEY);

            String decryptMsg = callbackCrypto.getDecryptMsg(msg_signature, timeStamp, nonce, encryptMsg);
            JSONObject eventJson = JSON.parseObject(decryptMsg);
            // 3. 反序列化回调事件json数据

            String eventType = eventJson.getString("EventType");

            // 4. 根据EventType分类处理
            if ("check_url".equals(eventType)) {
                // 测试回调url的正确性
                log.info("测试回调url的正确性");
            } else {
                cacheUser(companyVO);
                UserDomain userDomain = UserUtils.getUser();
                log.info("发生了：" + eventType + "事件");
                if ("user_add_org".equals(eventType)) {
                    List<String> userIdList = JSONArray.parseArray(eventJson.getString("UserId"), String.class);
                    List<UserPullVO> userDingVOS = this.getUserDetailById(userIdList, companyVO);
                    userService.saveDingUserInfo(userDingVOS, userDomain, null, null);
                } else if ("user_modify_org".equals(eventType)) {
                    List<String> userIdList = JSONArray.parseArray(eventJson.getString("UserId"), String.class);
                    List<UserPullVO> userDingVOS = this.getUserDetailById(userIdList, companyVO);
                    userService.saveDingUserInfo(userDingVOS, userDomain, null, null);
                } else if ("user_leave_org".equals(eventType)) {
                    List<String> userIdList = JSONArray.parseArray(eventJson.getString("UserId"), String.class);
                    userCompanyService.delete(userDomain.getCompanyId(), userIdList);
                } else if ("org_dept_create".equals(eventType)) {
                    List<String> deptIdList = JSONArray.parseArray(eventJson.getString("DeptId"), String.class);
                    departmentService.saveOrUpdate(deptIdList, userDomain.getCompanyId());
                } else if ("org_dept_modify".equals(eventType)) {
                    List<String> deptIdList = JSONArray.parseArray(eventJson.getString("DeptId"), String.class);
                    departmentService.saveOrUpdate(deptIdList, userDomain.getCompanyId());
                } else if ("org_dept_remove".equals(eventType)) {
                    List<String> deptIdList = JSONArray.parseArray(eventJson.getString("DeptId"), String.class);
                    departmentService.deleteByThirdDeptId(deptIdList);
                }
            }

            // 5. 返回success的加密数据
            Map<String, String> success = callbackCrypto.getEncryptedMap("success");
            return new HashMap<>(success);
        } catch (Exception e) {
            log.error("获取钉钉推送失败", e);
        }
        return new HashMap<>();
    }

    @Override
    public String getAccessToken(Long companyId) {
        CompanyVO info = companyService.info(companyId);
        DingTalkConfig dingTalkConfig = JSONObject.parseObject(info.getPullConfig(), DingTalkConfig.class);
        Client client = createClient();
        GetAccessTokenRequest getAccessTokenRequest =
                new GetAccessTokenRequest().setAppKey(dingTalkConfig.getClientId())
                        .setAppSecret(dingTalkConfig.getClientSecret());
        try {
            GetAccessTokenResponse accessToken = client.getAccessToken(getAccessTokenRequest);
            return accessToken.getBody().getAccessToken();
        } catch (Exception err) {
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }

    private static Client createClient() {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            return new Client(config);
        } catch (Exception e) {
            log.error("获取钉钉client失败", e);
            throw new AdminException(AdminResultCode.DING_TALK_CLIENT_ERROR);
        }
    }

    public static com.aliyun.dingtalkcontact_1_0.Client contactClient() {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            return new com.aliyun.dingtalkcontact_1_0.Client(config);
        } catch (Exception e) {
            log.error("获取钉钉client失败", e);
            throw new AdminException(AdminResultCode.DING_TALK_CLIENT_ERROR);
        }
    }

    @Override
    public List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO) {
        List<DepartmentPullVO> departmentDingVOS = new ArrayList<>();
        try {
            String accessToken = DingTalkCache.getAccessToken(companyVO.getCompanyId());
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
            OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
            req.setDeptId(Long.valueOf(deptId));
            OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);
            List<OapiV2DepartmentListsubResponse.DeptBaseResponse> result = rsp.getResult();
            for (OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse : result) {
                DepartmentPullVO departmentDingVO = new DepartmentPullVO();
                departmentDingVO.setDeptId(String.valueOf(deptBaseResponse.getDeptId()));
                departmentDingVO.setName(deptBaseResponse.getName());
                departmentDingVO.setParentId(String.valueOf(deptBaseResponse.getParentId()));
                departmentDingVOS.add(departmentDingVO);
            }
        } catch (Exception e) {
            log.error("获取部门失败", e);
        }
        return departmentDingVOS;
    }

    @Override
    public List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO) {
        List<UserPullVO> userDingVOS = new ArrayList<>();
        try {
            Long pageSize = 100L;
            Long pageNum = 0L;
            boolean hasMore = true;
            while (hasMore) {
                String accessToken = DingTalkCache.getAccessToken(companyVO.getCompanyId());
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
                OapiV2UserListRequest req = new OapiV2UserListRequest();
                req.setDeptId(Long.valueOf(deptId));
                req.setCursor(pageNum * pageSize);
                req.setSize(pageSize);
                OapiV2UserListResponse rsp = client.execute(req, accessToken);
                List<OapiV2UserListResponse.ListUserResponse> list = rsp.getResult().getList();
                for (OapiV2UserListResponse.ListUserResponse listUserResponse : list) {
                    UserPullVO userDingVO = new UserPullVO();
                    userDingVO.setDeptIdList(listUserResponse.getDeptIdList().stream().map(String::valueOf)
                            .collect(Collectors.toList()));
                    userDingVO.setPosition(listUserResponse.getTitle());
                    userDingVO.setUnionid(listUserResponse.getUnionid());
                    userDingVO.setNickName(listUserResponse.getName());
                    userDingVO.setPhonenumber(listUserResponse.getMobile());
                    userDingVO.setDingThirdId(listUserResponse.getUserid());
                    userDingVO.setThirdId(listUserResponse.getUserid());
                    userDingVO.setThirdType(companyVO.getDataSource());
                    userDingVOS.add(userDingVO);
                }
                hasMore = rsp.getResult().getHasMore();
                pageNum++;
            }

        } catch (Exception e) {
            log.error("获取用户失败", e);
        }
        return userDingVOS;
    }

    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        if (companyVO == null) {
            companyVO = companyService.info(UserUtils.getUser().getCompanyId());
        }
        String accessToken = DingTalkCache.getAccessToken(companyVO.getCompanyId());
        List<UserPullVO> userDingVOList = new ArrayList<>();
        for (String userId : ids) {
            try {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
                OapiV2UserGetRequest req = new OapiV2UserGetRequest();
                req.setUserid(userId);
                OapiV2UserGetResponse rsp = client.execute(req, accessToken);
                OapiV2UserGetResponse.UserGetResponse result = rsp.getResult();
                UserPullVO userDingVO = new UserPullVO();
                userDingVO.setDeptIdList(
                        result.getDeptIdList().stream().map(String::valueOf).collect(Collectors.toList()));
                userDingVO.setPosition(result.getTitle());
                userDingVO.setUnionid(result.getUnionid());
                userDingVO.setNickName(result.getName());
                userDingVO.setPhonenumber(result.getMobile());
                userDingVO.setDingThirdId(result.getUserid());
                userDingVO.setThirdId(result.getUserid());
                userDingVOList.add(userDingVO);
            } catch (Exception e) {
                log.error("获取用户失败", e);
            }
        }
        return userDingVOList;
    }

    @Override
    public DepartmentPullVO getDeptById(String deptId, CompanyVO companyVO) {
        if (companyVO == null) {
            companyVO = companyService.info(UserUtils.getUser().getCompanyId());
        }
        String accessToken = DingTalkCache.getAccessToken(companyVO.getCompanyId());
        try {
            return getDepartmentDingVO(deptId, accessToken);
        } catch (ApiException e) {
            log.error("获取部门失败", e);
        }
        return null;
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getuserinfo");
            OapiV2UserGetuserinfoRequest req = new OapiV2UserGetuserinfoRequest();
            req.setCode(code);
            OapiV2UserGetuserinfoResponse rsp =
                    client.execute(req, DingTalkCache.getAccessToken(companyVO.getCompanyId()));
            return rsp.getResult().getUserid();
        } catch (Exception e) {
            log.error("单点登录失败", e);
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }

    @Override
    public UserLoginVO getUserInfoByAssessToken(String assessToken) {
        try {
            com.aliyun.dingtalkcontact_1_0.Client client = contactClient();
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = assessToken;
            //获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
            GetUserResponseBody me = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions()).getBody();
            UserLoginVO userLoginVO = new UserLoginVO();
            userLoginVO.setUnionId(me.getUnionId());
            userLoginVO.setUser_id(getUserIdByUnionId(userLoginVO.getUnionId(), assessToken));
            return userLoginVO;
        } catch (Exception e) {
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }


    public String getUserIdByUnionId(String unionId, String accessToken) {
        try {
            DingTalkClient clientDingTalkClient =
                    new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
            OapiUserGetbyunionidRequest reqGetbyunionidRequest = new OapiUserGetbyunionidRequest();
            reqGetbyunionidRequest.setUnionid(unionId);
            OapiUserGetbyunionidResponse oapiUserGetbyunionidResponse =
                    clientDingTalkClient.execute(reqGetbyunionidRequest, accessToken);
            // 根据userId获取用户信息
            return oapiUserGetbyunionidResponse.getResult().getUserid();
        } catch (Exception e) {
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }

    private static DepartmentPullVO getDepartmentDingVO(String deptId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
        OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
        req.setDeptId(Long.valueOf(deptId));
        OapiV2DepartmentGetResponse rsp = client.execute(req, accessToken);
        OapiV2DepartmentGetResponse.DeptGetResponse result = rsp.getResult();
        DepartmentPullVO departmentDingVO = new DepartmentPullVO();
        departmentDingVO.setDeptId(String.valueOf(result.getDeptId()));
        departmentDingVO.setName(result.getName());
        departmentDingVO.setParentId(String.valueOf(result.getParentId()));
        departmentDingVO.setLeaderList(result.getDeptManagerUseridList());
        return departmentDingVO;
    }

    @Override
    public JsapiAuthVO jsapiAuth(String secretId) {
        JsapiAuthVO jsapiAuthVO = new JsapiAuthVO();
        CompanyVO companyVO = companyService.getBySecretId(secretId);
        Client client = createClient();
        CreateJsapiTicketHeaders createJsapiTicketHeaders = new CreateJsapiTicketHeaders();
        createJsapiTicketHeaders.xAcsDingtalkAccessToken = DingTalkCache.getAccessToken(companyVO.getCompanyId());
        try {
            CreateJsapiTicketResponse jsapiTicketWithOptions =
                    client.createJsapiTicketWithOptions(createJsapiTicketHeaders, new RuntimeOptions());
            jsapiAuthVO.setTicket(jsapiTicketWithOptions.getBody().getJsapiTicket());
            jsapiAuthVO.setTimestamp(new Date().getTime());
            return jsapiAuthVO;
        } catch (Exception e) {
            log.error("jsapiAuth", e);
            throw new AdminException(AdminResultCode.DING_TALK_TOKEN_ERROR);
        }
    }
}
