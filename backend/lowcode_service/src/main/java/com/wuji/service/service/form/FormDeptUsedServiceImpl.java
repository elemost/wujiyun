package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.DepartmentService;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.vo.DepartmentVO;
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
public class FormDeptUsedServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Autowired
    private DepartmentService departmentService;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType();
    }

    @Override
    public void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,
                                   SystemAllDataVO systemAllDataVO) {
        List<Long> deptList = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Long deptId = instValue.getLong(mongodbSearchField.getName());
            if (deptId != null) {
                deptList.add(deptId);
            }
        }
        List<DepartmentVO> departmentVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deptList)) {
            departmentVOList = departmentService.queryListByIdList(deptList);
        }
        Map<Long, String> deptNameMap = departmentVOList.stream()
                .collect(Collectors.toMap(DepartmentVO::getDeptId, DepartmentVO::getDeptName));
        for (JSONObject jsonObject : jsonObjects) {
            JSONObject instValue = jsonObject.getJSONObject("instValue");
            Long deptId = instValue.getLong(mongodbSearchField.getName());
            if (deptId != null) {
                FormDept formDept = new FormDept();
                formDept.setValue(deptId);
                formDept.setLabel(deptNameMap.getOrDefault(Long.valueOf(deptId.toString()), ""));
                instValue.put(mongodbSearchField.getName(), Collections.singletonList(formDept));
            } else {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("value", null);
                instValue.put(mongodbSearchField.getName(), Collections.singletonList(jsonObject1));
            }
            jsonObject.put("instValue", instValue);
        }
    }
}
