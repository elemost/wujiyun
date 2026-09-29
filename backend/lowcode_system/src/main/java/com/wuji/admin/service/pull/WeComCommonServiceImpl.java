package com.wuji.admin.service.pull;

import com.wuji.admin.client.wecom.WeComClient;
import com.wuji.admin.client.wecom.model.DepartmentListResult;
import com.wuji.admin.client.wecom.model.UserListResult;
import com.wuji.admin.client.wecom.model.WeComDepartmentVO;
import com.wuji.admin.client.wecom.model.WeComUserInfoVO;
import com.wuji.admin.handler.PullDataContext;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.OrganizePullDataService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeComCommonServiceImpl extends ThirdCommonServiceImpl implements OrganizePullDataService {
    @Autowired
    private WeComClient weComClient;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PullDataContext pullDataContext;

    public List<DepartmentPullVO> getDepartmentPullVOS(String deptId, String accessToken) {
        DepartmentListResult weComResult = weComClient.departmentList(accessToken, deptId);
        List<WeComDepartmentVO> weComDepartmentVOS =
                weComResult.getDepartment().stream().filter(c -> deptId.equals(c.getParentid().toString()))
                        .collect(Collectors.toList());
        List<DepartmentPullVO> departmentPullVOList = new ArrayList<>();
        for (WeComDepartmentVO weComDepartmentVO : weComDepartmentVOS) {
            DepartmentPullVO departmentDingVO = buildDeptPull(weComDepartmentVO);
            departmentPullVOList.add(departmentDingVO);
        }
        return departmentPullVOList;
    }

    public DepartmentPullVO buildDeptPull(WeComDepartmentVO weComDepartmentVO) {
        DepartmentPullVO departmentDingVO = new DepartmentPullVO();
        departmentDingVO.setDeptId(weComDepartmentVO.getId().toString());
        departmentDingVO.setName(weComDepartmentVO.getName());
        departmentDingVO.setParentId(weComDepartmentVO.getParentid().toString());
        return departmentDingVO;
    }

    public List<UserPullVO> getUserPullVOS(List<String> ids, CompanyVO companyVO, String accessToken) {
        List<UserPullVO> userPullVOList = new ArrayList<>();
        for (String userId : ids) {
            WeComUserInfoVO weComUserInfoVO = weComClient.userInfo(accessToken, userId);
            UserPullVO userPullVO = getUserPullVO(weComUserInfoVO, companyVO);
            userPullVOList.add(userPullVO);
        }
        return userPullVOList;
    }

    public List<UserPullVO> getAllUser(String deptId, CompanyVO companyVO, String accessToken) {
        UserListResult userListResult = weComClient.userList(accessToken, deptId);
        List<UserPullVO> userPullVOList = new ArrayList<>();
        for (WeComUserInfoVO weComUserInfoVO : userListResult.getUserlist()) {
            UserPullVO userPullVO = getUserPullVO(weComUserInfoVO, companyVO);
            userPullVOList.add(userPullVO);
        }
        return userPullVOList;
    }

    private static UserPullVO getUserPullVO(WeComUserInfoVO weComUserInfoVO, CompanyVO companyVO) {
        UserPullVO userPullVO = new UserPullVO();
        userPullVO.setDeptIdList(weComUserInfoVO.getDepartment());
        userPullVO.setPosition(weComUserInfoVO.getPosition());
        userPullVO.setNickName(weComUserInfoVO.getName());
        userPullVO.setThirdId(weComUserInfoVO.getUserid());
        userPullVO.setWeComUserId(weComUserInfoVO.getUserid());
        userPullVO.setUserName(weComUserInfoVO.getUserid() + "_" + companyVO.getCompanyId());
        return userPullVO;
    }

    public void changeContact(String changeType, Document xml, CompanyVO companyVO, UserDomain userDomain,
                              String dataSource) {
        if ("create_user".equals(changeType)) {
            String userId = xml.getElementsByTagName("UserID").item(0).getTextContent();
            List<UserPullVO> userDingVOS = pullDataContext.getHandler(dataSource)
                    .getUserDetailById(Collections.singletonList(userId), companyVO);
            userService.saveDingUserInfo(userDingVOS, userDomain, null, null);
        } else if ("update_user".equals(changeType)) {
            String userId = xml.getElementsByTagName("UserID").item(0).getTextContent();
            List<UserPullVO> userDingVOS = pullDataContext.getHandler(dataSource)
                    .getUserDetailById(Collections.singletonList(userId), companyVO);
            userService.saveDingUserInfo(userDingVOS, userDomain, null, null);
        } else if ("delete_user".equals(changeType)) {
            String userId = xml.getElementsByTagName("UserID").item(0).getTextContent();
            userCompanyService.deleteByThirdId(companyVO.getCompanyId(), dataSource, Collections.singletonList(userId));
        } else if ("create_party".equals(changeType)) {
            String deptId = xml.getElementsByTagName("Id").item(0).getTextContent();
            departmentService.saveOrUpdate(Collections.singletonList(deptId), userDomain.getCompanyId());
        } else if ("update_party".equals(changeType)) {
            String deptId = xml.getElementsByTagName("Id").item(0).getTextContent();
            departmentService.saveOrUpdate(Collections.singletonList(deptId), userDomain.getCompanyId());
        } else if ("delete_party".equals(changeType)) {
            String deptId = xml.getElementsByTagName("Id").item(0).getTextContent();
            departmentService.deleteByThirdDeptId(Collections.singletonList(deptId));
        }
    }
}
