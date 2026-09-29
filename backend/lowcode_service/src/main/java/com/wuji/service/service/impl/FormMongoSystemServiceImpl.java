package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.model.request.UserCreateRequest;
import com.wuji.admin.model.request.UserInfoSaveRequest;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.utils.StringUtil;
import com.wuji.service.enums.FormSyncUserEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormExtraFunctionButtonAction;
import com.wuji.service.model.request.FormMongoSyncUserRequest;
import com.wuji.service.model.vo.FormMongoUserFieldVO;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormMongoSystemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormMongoSystemServiceImpl implements FormMongoSystemService {

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private UserService userService;

    @Override
    public List<FormMongoUserFieldVO> getUserField() {
        List<FormMongoUserFieldVO> formMongoUserFieldVOS = new ArrayList<>();
        for (FormSyncUserEnum formSyncUserEnum : FormSyncUserEnum.values()) {
            FormMongoUserFieldVO formMongoUserFieldVO = new FormMongoUserFieldVO();
            formMongoUserFieldVO.setName(formSyncUserEnum.getKey());
            formMongoUserFieldVO.setLabel(formSyncUserEnum.getMsg());
            formMongoUserFieldVOS.add(formMongoUserFieldVO);
        }
        return formMongoUserFieldVOS;
    }

    @Override
    public void syncUser(FormMongoSyncUserRequest formMongoSyncUserRequest) {
        List<FormExtraFunctionButtonAction> actions =
                formExtraFunctionServiceImpl.getActions(formMongoSyncUserRequest.getButtonId(),
                        formMongoSyncUserRequest.getApplicationId(), formMongoSyncUserRequest.getFormId());
        if (actions == null) {
            return;
        }
        LowcodeDataDomain info =
                formMongoDbService.info(formMongoSyncUserRequest.getUuid(), formMongoSyncUserRequest.getFormId(),
                        formMongoSyncUserRequest.getApplicationId());
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setStatus("0");
        userCreateRequest.setUserType("00");
        Map<String, FormSyncUserEnum> keyToEnumMap =
                Arrays.stream(FormSyncUserEnum.values()).collect(Collectors.toMap(FormSyncUserEnum::getKey, c -> c));
        JSONObject instValue = FormSystemFieldEnum.putSystemValue(info);
        List<UserInfoSaveRequest> userInfoSaveRequestList = new ArrayList<>();
        for (FormExtraFunctionButtonAction action : actions) {
            FormSyncUserEnum formSyncUserEnum = keyToEnumMap.get(action.getCurrentName());
            if (formSyncUserEnum == null) {
                continue;
            }
            Object value = instValue.get(action.getValue());
            if (value == null) {
                continue;
            }
            UserInfoSaveRequest userInfoSaveRequest = new UserInfoSaveRequest();
            switch (formSyncUserEnum) {
                case PHONE:
                    userCreateRequest.setPhonenumber(value.toString());
                    userCreateRequest.setUserName(value.toString());
                    userCreateRequest.setPassword(value.toString());
                    break;
                case NICK_NAME:
                    userCreateRequest.setNickName(value.toString());
                    break;
                case EMAIL:
                    userCreateRequest.setEmail(value.toString());
                    break;
                case DEPT:
                    List<FormDept> formDeptList = JSONArray.parseArray(JSONObject.toJSONString(value), FormDept.class);
                    userCreateRequest.setDeptIdList(
                            formDeptList.stream().map(FormDept::getValue).collect(Collectors.toList()));
                    break;
                case SEX:
                    String sex = value.toString();
                    sex = sex.equals("男") ? "0" : "1";
                    userCreateRequest.setSex(sex);
                    userInfoSaveRequest.setInfoValue(sex);
                    userInfoSaveRequest.setInfoKey(formSyncUserEnum.getKey());
                    userInfoSaveRequestList.add(userInfoSaveRequest);
                    break;
                default:
                    userInfoSaveRequest.setInfoValue(value.toString());
                    userInfoSaveRequest.setInfoKey(formSyncUserEnum.getKey());
                    userInfoSaveRequestList.add(userInfoSaveRequest);
                    break;
            }
        }
        userCreateRequest.setUserInfoList(userInfoSaveRequestList);
        if (StringUtils.isEmpty(userCreateRequest.getPhonenumber())) {
            throw new ServiceException(ServiceResultCode.PARAM_ERROR, "手机号码不能为空");
        }
        boolean validPhoneNumber = StringUtil.isValidPhoneNumber(userCreateRequest.getPhonenumber());
        if (!validPhoneNumber) {
            throw new ServiceException(ServiceResultCode.PARAM_ERROR, AdminResultCode.PHONE_NUMBER_ERROR.getMessage());
        }
        if (StringUtils.isEmpty(userCreateRequest.getNickName())) {
            throw new ServiceException(ServiceResultCode.PARAM_ERROR, "用户昵称不能为空");
        }
        userService.create(userCreateRequest);
    }
}
