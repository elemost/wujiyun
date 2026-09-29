package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.UserService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.UserVO;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.service.FormDataService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormUserUsedServiceImpl extends FormCommonServiceImpl implements FormDataService {
    @Autowired
    private UserService userService;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType();
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        List<Long> userList = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Long userId = instValue.getLong(mongodbSearchField.getName());
            if (userId != null) {
                userList.add(userId);
            }
        }
        List<UserVO> userVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userList)) {
            userVOS = userService.queryByIds(userList);
        }
        Map<Long, String> userNameMap =
                userVOS.stream().collect(Collectors.toMap(UserVO::getUserId, UserVO::getNickName));
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Long userId = instValue.getLong(mongodbSearchField.getName());
            if (userId != null) {
                FormUser formUser = new FormUser();
                formUser.setAssigneeId(userId);
                formUser.setAssigneeName(userNameMap.getOrDefault(Long.valueOf(userId.toString()), ""));
                instValue.put(mongodbSearchField.getName(), Collections.singletonList(formUser));
            } else {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("assignedId", null);
                instValue.put(mongodbSearchField.getName(), Collections.singletonList(jsonObject1));
            }
            jsonObject.put("instValue", instValue);
        }
    }
}
