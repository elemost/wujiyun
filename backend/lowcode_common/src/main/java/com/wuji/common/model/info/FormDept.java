package com.wuji.common.model.info;

import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.utils.UserUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FormDept implements Serializable {
    private static final long serialVersionUID = 3670079982654483072L;

    private String label;

    private Long value;

    public static List<FormDept> getCurrentDept(UserDomain user) {

        if (user == null) {
            user = UserUtils.getUser();
        }
        if (CollectionUtils.isEmpty(user.getUserDeptList())) {
            return new ArrayList<>();
        }

        List<FormDept> formDeptList = new ArrayList<>();
        for (UserDeptVO userDeptVO : user.getUserDeptList()) {
            FormDept formDept = new FormDept();
            formDept.setLabel(userDeptVO.getDeptName());
            formDept.setValue(userDeptVO.getDeptId());
            formDeptList.add(formDept);
        }
        return formDeptList;
    }

    public static List<FormDept> getDefaultDept() {
        List<FormDept> formDeptList = new ArrayList<>();
        FormDept formDept = new FormDept();
        formDept.setLabel("当前用户所处所有部门");
        formDept.setValue(9999L);
        formDeptList.add(formDept);
        return formDeptList;
    }


    public static List<Object> getDefaultDeptObject() {
        List<Object> formDeptList = new ArrayList<>();
        FormDept formDept = new FormDept();
        formDept.setLabel("当前用户所处所有部门");
        formDept.setValue(9999L);
        formDeptList.add(formDept);
        return formDeptList;
    }
}
