package com.wuji.admin.service.pull;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.lark.LarkClient;
import com.wuji.admin.client.lark.LarkResult;
import com.wuji.admin.client.lark.model.JssdkTicketVO;
import com.wuji.admin.client.lark.model.LarkDataVO;
import com.wuji.admin.client.lark.model.LarkDepartmentInfoVO;
import com.wuji.admin.client.lark.model.LarkDepartmentLeaderInfoVO;
import com.wuji.admin.client.lark.model.LarkDepartmentLoginRequest;
import com.wuji.admin.client.lark.model.LarkUserAssessTokenRequest;
import com.wuji.admin.client.lark.model.LarkUserVO;
import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.LarkEventType;
import com.wuji.admin.model.info.LarkEvent;
import com.wuji.admin.model.info.LarkEventHeader;
import com.wuji.admin.model.info.LarkEventObject;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.JsapiAuthVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.LarkService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.admin.utils.Decrypt;
import com.wuji.admin.utils.LarkCallbackCrypto;
import com.wuji.common.model.info.LarkConfig;
import com.wuji.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("larkService")
@Slf4j
public class LarkServiceImpl extends ThirdCommonServiceImpl implements LarkService {

    @Autowired
    private LarkClient larkClient;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public String dataSource() {
        return CompanyDataSourceEnum.LARK.name();
    }

    @Override
    public Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                         String secretId) {
        CompanyVO companyVO = companyService.getBySecretId(secretId);
        LarkConfig larkConfig = JSONObject.parseObject(companyVO.getPullConfig(), LarkConfig.class);
        Decrypt decrypt = new Decrypt(larkConfig.getAesKey());
        String encrypt = decrypt.decrypt(json.getString("encrypt"));
        log.info("encrypt" + encrypt);
        JSONObject jsonObject = JSONObject.parseObject(encrypt);
        Object header = jsonObject.get("header");
        cacheUser(companyVO);
        if (header != null) {
            LarkEventHeader larkEventHeader =
                    JSONObject.parseObject(JSONObject.toJSONString(header), LarkEventHeader.class);
            if (LarkEventType.DEPT_CREATE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                LarkEventObject larkEventObject =
                        JSONObject.parseObject(JSONObject.toJSONString(larkEvent.getObject()), LarkEventObject.class);
                departmentService.saveOrUpdate(Collections.singletonList(larkEventObject.getOpen_department_id()),
                        companyVO.getCompanyId());
            } else if (LarkEventType.DEPT_UPDATE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                LarkEventObject larkEventObject =
                        JSONObject.parseObject(JSONObject.toJSONString(larkEvent.getObject()), LarkEventObject.class);
                if (!larkEventObject.getStatus().getIs_deleted()) {
                    departmentService.saveOrUpdate(Collections.singletonList(larkEventObject.getOpen_department_id()),
                            companyVO.getCompanyId());
                }
            } else if (LarkEventType.DEPT_DELETE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                LarkEventObject larkEventObject =
                        JSONObject.parseObject(JSONObject.toJSONString(larkEvent.getObject()), LarkEventObject.class);
                departmentService.deleteByThirdDeptId(
                        Collections.singletonList(larkEventObject.getOpen_department_id()));
            } else if (LarkEventType.USER_UPDATE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                UserPullVO userPullVO = getUserPullVO(larkEvent.getObject());
                userService.saveDingUserInfo(Collections.singletonList(userPullVO), UserUtils.getUser(), null, null);
            } else if (LarkEventType.USER_CREATE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                UserPullVO userPullVO = getUserPullVO(larkEvent.getObject());
                userService.saveDingUserInfo(Collections.singletonList(userPullVO), UserUtils.getUser(), null, null);
            } else if (LarkEventType.USER_DELETE.getType().equals(larkEventHeader.getEvent_type())) {
                LarkEvent larkEvent = JSONObject.parseObject(encrypt, LarkEvent.class);
                UserPullVO userPullVO = getUserPullVO(larkEvent.getObject());
                userCompanyService.deleteByLarkUserId(companyVO.getCompanyId(),
                        Collections.singletonList(userPullVO.getDingThirdId()));
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("challenge", jsonObject.getString("challenge"));
        return map;
    }

    @Override
    public String getAccessToken(Long companyId) {
        CompanyVO info = companyService.info(companyId);
        LarkDepartmentLoginRequest larkDepartmentLoginRequest = new LarkDepartmentLoginRequest();
        LarkConfig larkConfig = JSONObject.parseObject(info.getPullConfig(), LarkConfig.class);
        larkDepartmentLoginRequest.setApp_id(larkConfig.getClientId());
        larkDepartmentLoginRequest.setApp_secret(larkConfig.getClientSecret());
        return "Bearer " + larkClient.login(larkDepartmentLoginRequest).getApp_access_token();
    }

    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        LarkResult<LarkDataVO<List<JSONObject>>> userLarkResult = larkClient.getUserByIdList(ids, "user_id");
        List<UserPullVO> userPullVOList = new ArrayList<>();
        buildUserPullList(userLarkResult, userPullVOList);
        return userPullVOList;
    }

    @Override
    public DepartmentPullVO getDeptById(String deptIdList, CompanyVO companyVO) {
        LarkResult<LarkDataVO<List<JSONObject>>> larkDataVOLarkResult =
                larkClient.getDeptByIdList(Collections.singletonList(deptIdList), "open_department_id");
        List<DepartmentPullVO> departmentPullVOList = new ArrayList<>();
        buildDeptPullList(larkDataVOLarkResult, departmentPullVOList);
        return departmentPullVOList.get(0);
    }

    @Override
    public List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO) {
        List<DepartmentPullVO> departmentPullVOList = new ArrayList<>();
        String pageToken = null;
        LarkResult<LarkDataVO<List<JSONObject>>> larkDepartmentDataVOLarkResult =
                larkClient.getDeptList(deptId, "open_department_id", 50, Boolean.FALSE, pageToken, "user_id");
        buildDeptPullList(larkDepartmentDataVOLarkResult, departmentPullVOList);

        while (larkDepartmentDataVOLarkResult.getData().getHas_more()) {
            pageToken = larkDepartmentDataVOLarkResult.getData().getPage_token();
            larkDepartmentDataVOLarkResult =
                    larkClient.getDeptList(deptId, "open_department_id", 50, Boolean.FALSE, pageToken, "user_id");
            buildDeptPullList(larkDepartmentDataVOLarkResult, departmentPullVOList);
        }
        return departmentPullVOList;
    }

    private static void buildDeptPullList(LarkResult<LarkDataVO<List<JSONObject>>> larkDepartmentDataVOLarkResult,
                                          List<DepartmentPullVO> departmentPullVOList) {
        if (larkDepartmentDataVOLarkResult.getData() == null ||
                larkDepartmentDataVOLarkResult.getData().getItems() == null) {
            return;
        }
        for (JSONObject jsonObject : larkDepartmentDataVOLarkResult.getData().getItems()) {
            LarkDepartmentInfoVO larkDepartmentInfoVO =
                    JSONObject.parseObject(JSONObject.toJSONString(jsonObject), LarkDepartmentInfoVO.class);
            DepartmentPullVO departmentDingVO = new DepartmentPullVO();
            departmentDingVO.setDeptId(larkDepartmentInfoVO.getOpen_department_id());
            departmentDingVO.setName(larkDepartmentInfoVO.getName());
            departmentDingVO.setParentId(larkDepartmentInfoVO.getParent_department_id());
            departmentDingVO.setLeaderList(
                    larkDepartmentInfoVO.getLeaders().stream().map(LarkDepartmentLeaderInfoVO::getLeaderID)
                            .collect(Collectors.toList()));
            departmentPullVOList.add(departmentDingVO);
        }
    }

    @Override
    public List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO) {
        String pageToken = null;
        LarkResult<LarkDataVO<List<JSONObject>>> larkDataVOLarkResult =
                larkClient.getUserList(deptId, "open_department_id", 50, null);
        List<UserPullVO> userPullVOList = new ArrayList<>();
        buildUserPullList(larkDataVOLarkResult, userPullVOList);
        while (larkDataVOLarkResult.getData().getHas_more()) {
            pageToken = larkDataVOLarkResult.getData().getPage_token();
            larkDataVOLarkResult = larkClient.getUserList(deptId, "open_department_id", 50, pageToken);
            buildUserPullList(larkDataVOLarkResult, userPullVOList);
        }
        return userPullVOList;
    }

    private void buildUserPullList(LarkResult<LarkDataVO<List<JSONObject>>> larkDataVOLarkResult,
                                   List<UserPullVO> userPullVOList) {
        LarkDataVO<List<JSONObject>> larkResultData = larkDataVOLarkResult.getData();
        List<JSONObject> items = larkResultData.getItems();
        if (items == null) {
            return;
        }
        for (JSONObject jsonObject : items) {
            UserPullVO userPullVO = getUserPullVO(jsonObject);
            userPullVOList.add(userPullVO);
        }
    }

    private static UserPullVO getUserPullVO(JSONObject jsonObject) {
        LarkUserVO larkUserVO = JSONObject.parseObject(JSONObject.toJSONString(jsonObject), LarkUserVO.class);
        UserPullVO userPullVO = new UserPullVO();
        userPullVO.setDeptIdList(larkUserVO.getDepartment_ids());
        userPullVO.setPosition(larkUserVO.getJob_title());
        userPullVO.setLarkUnionId(larkUserVO.getUnion_id());
        userPullVO.setNickName(larkUserVO.getName());
        userPullVO.setThirdId(larkUserVO.getUser_id());
        if (larkUserVO.getMobile().contains("+86")) {
            userPullVO.setPhonenumber(larkUserVO.getMobile().replace("+86", ""));
        } else {
            userPullVO.setPhonenumber(larkUserVO.getMobile());
        }
        userPullVO.setLarkUserId(larkUserVO.getUser_id());
        userPullVO.setLarkOpenId(larkUserVO.getOpen_id());
        return userPullVO;
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        LarkConfig larkConfig = JSONObject.parseObject(companyVO.getPullConfig(), LarkConfig.class);
        LarkUserAssessTokenRequest larkUserAssessTokenRequest = new LarkUserAssessTokenRequest();
        larkUserAssessTokenRequest.setCode(code);
        larkUserAssessTokenRequest.setClient_id(larkConfig.getClientId());
        larkUserAssessTokenRequest.setClient_secret(larkConfig.getClientSecret());
        larkUserAssessTokenRequest.setRedirect_uri(url);
        LarkResult userAssess = larkClient.getUserAssess(larkUserAssessTokenRequest);
        return "Bearer " + userAssess.getAccess_token();
    }

    @Override
    public UserLoginVO getUserInfoByAssessToken(String assessToken) {
        LarkResult<JSONObject> loginUserInfo = larkClient.getLoginUserInfo(assessToken);
        return JSONObject.parseObject(JSONObject.toJSONString(loginUserInfo.getData()), UserLoginVO.class);
    }

    @Override
    public JsapiAuthVO jsapiAuth(String url, String secretId) {
        CompanyVO companyVO = companyService.getBySecretId(secretId);
        cacheUser(companyVO);
        LarkResult<JssdkTicketVO> jssdkTicketVOLarkResult = larkClient.jsapiAuth();
        long time = new Date().getTime();
        String nonestr = RandomStringUtils.randomAlphanumeric(16);
        String encrypt = LarkCallbackCrypto.encrypt(time, jssdkTicketVOLarkResult.getData().getTicket(), url, nonestr);
        JsapiAuthVO jsapiAuthVO = new JsapiAuthVO();
        jsapiAuthVO.setAppId(secretId);
        jsapiAuthVO.setSignature(encrypt);
        jsapiAuthVO.setTimestamp(time);
        jsapiAuthVO.setTicket(jssdkTicketVOLarkResult.getData().getTicket());
        jsapiAuthVO.setNoncestr(nonestr);
        return jsapiAuthVO;
    }

}
