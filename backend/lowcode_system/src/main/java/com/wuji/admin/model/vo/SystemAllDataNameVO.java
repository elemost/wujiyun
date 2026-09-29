package com.wuji.admin.model.vo;

import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.UserVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SystemAllDataNameVO {
    private Map<String, UserVO> phonenumberMap;

    private Map<String, List<UserCompanyVO>> nickNameMap;

    private Map<String, List<DepartmentVO>> deptNameMap;

    private Map<String, PostVO> roleMap;
}
