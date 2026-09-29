package com.wuji.admin.model.vo;

import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.UserVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SystemAllDataVO {

    private Map<Long, UserVO> userIdMap;

    private Map<Long, DepartmentVO> deptIdMap;

    private Map<Long, UserCompanyVO> userIdCompanyMap;

    private Map<Long, PostVO> roleIdMap;

    private Map<String, List<DepartmentVO>> deptNameMap;

    private Map<Long, String> deptIdToNameMap;

    private Map<Long, String> userIdToNameMap;

}
