package com.wuji.service.model.request;

import com.wuji.admin.model.vo.RoleVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.UserVO;
import lombok.Data;

import java.util.Map;

@Data
public class FormExportCheckRequest {
    private Map<Long, UserVO> userIdMap;

    private Map<Long, DepartmentVO> deptIdMap;

    private Map<Long, UserCompanyVO> userIdCompanyMap;

    private Map<Long, RoleVO> roleIdMap;
}
