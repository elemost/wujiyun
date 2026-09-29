package com.wuji.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;

import java.util.List;
import java.util.Map;

public interface OrganizePullDataService {

    String dataSource();

    Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                  String clientId);

    String getAccessToken(Long companyId);

    List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO);

    List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO);

    List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO);

    DepartmentPullVO getDeptById(String deptIdList, CompanyVO companyVO);

    /**
     * 飞书返回的是token 钉钉返回userid
     *
     * @param code
     * @param url
     * @param companyVO
     * @return
     */
    String getUserAssessToken(String code, String url, CompanyVO companyVO);

    UserLoginVO getUserInfoByAssessToken(String assessToken);

    void pullThirdId();

}
