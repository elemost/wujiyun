package com.wuji.admin.service;

import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;

import java.util.List;

public interface AdminCommonService {
    List<FormUser> getSystemUser();

    List<FormDept> getSystemDept();

    SystemAllDataVO getSystemAllData();

    SystemAllDataNameVO getSystemAllDataName();
}
