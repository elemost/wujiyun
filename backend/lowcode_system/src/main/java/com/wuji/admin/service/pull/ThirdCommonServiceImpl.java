package com.wuji.admin.service.pull;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.lark.model.UserLoginVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.OrganizePullDataService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ThirdCommonServiceImpl implements OrganizePullDataService {
    public void cacheUser(CompanyVO companyVO) {
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyId(companyVO.getCompanyId());
        UserUtils.setUser(userDomain);
    }


    @Override
    public String dataSource() {
        return null;
    }

    @Override
    public Map<String, Object> subscribe(String msg_signature, String timeStamp, String nonce, JSONObject json,
                                         String clientId) {
        return null;
    }

    @Override
    public String getAccessToken(Long companyId) {
        return null;
    }

    @Override
    public List<DepartmentPullVO> getDeptChildList(String deptId, CompanyVO companyVO) {
        return null;
    }

    @Override
    public List<UserPullVO> getAllUserList(String deptId, CompanyVO companyVO) {
        return null;
    }

    @Override
    public List<UserPullVO> getUserDetailById(List<String> ids, CompanyVO companyVO) {
        return null;
    }

    @Override
    public DepartmentPullVO getDeptById(String deptIdList, CompanyVO companyVO) {
        return null;
    }

    @Override
    public String getUserAssessToken(String code, String url, CompanyVO companyVO) {
        return null;
    }

    @Override
    public UserLoginVO getUserInfoByAssessToken(String assessToken) {
        return null;
    }

    @Override
    public void pullThirdId() {

    }
}
