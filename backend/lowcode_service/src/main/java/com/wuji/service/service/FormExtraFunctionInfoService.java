package com.wuji.service.service;

import com.wuji.service.model.vo.FormExtraFunctionInfoPrivilegeVO;

import java.util.List;

public interface FormExtraFunctionInfoService extends FormExtraFunctionService {

    List<FormExtraFunctionInfoPrivilegeVO> privilegeList(String id);

}
