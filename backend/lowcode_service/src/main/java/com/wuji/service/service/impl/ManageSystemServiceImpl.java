package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.admin.service.CompanyRelationService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.model.request.ManageDeptSystemRequest;
import com.wuji.service.model.request.ManagePostSystemRequest;
import com.wuji.service.model.request.ManageUserSystemRequest;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ManageSystemService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageSystemServiceImpl implements ManageSystemService {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PostService postService;

    @Autowired
    private CompanyRelationService companyRelationService;

    @Override
    public QueryPageVO<UserVO> getUserByGroupId(ManageUserSystemRequest manageUserSystemRequest) {
        UserSelectRequest userSelectRequest = new UserSelectRequest();
        userSelectRequest.setPageSize(1000);
        userSelectRequest.setPageNum(1);
        userSelectRequest.setUserType(manageUserSystemRequest.getUserType());
        userSelectRequest.setPostId(manageUserSystemRequest.getPostId());
        userSelectRequest.setDepartmentId(manageUserSystemRequest.getDepartmentId());
        userSelectRequest.setIdList(manageUserSystemRequest.getIdList());
        userSelectRequest.setStatus("0");
        if (UserTypeEnum.EXTERNAL.getCode().equals(manageUserSystemRequest.getUserType())) {
            ManageVO manageVO =
                    ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
            if (manageVO != null && !manageVO.getSuperManage()) {
                List<Long> currentUserRelationCompany = companyRelationService.getCurrentUserRelationCompany();
                if (CollectionUtils.isEmpty(currentUserRelationCompany)) {
                    return new QueryPageVO<>();
                }
                userSelectRequest.setSourceCompanyId(currentUserRelationCompany);
            }
            return userService.querySelectList(userSelectRequest);
        }
        UserDomain user = UserUtils.getUser();
        ManageVO manageVO = ManageCache.getConfig(user.getCompanyId(), user.getUserIdLongValue());
        if (manageVO == null) {
            return userService.querySelectList(userSelectRequest);
        }
        if (manageVO.getSuperManage()) {
            return userService.querySelectList(userSelectRequest);
        }
        if (!"ALL".equals(manageVO.getDeptScopeType())) {
            List<Long> deptList = JSONArray.parseArray(manageVO.getDeptScope(), Long.class);
            if (CollectionUtils.isNotEmpty(deptList)) {
                userSelectRequest.setDepartmentIdManageList(deptList);
            }
        }

        if (!"ALL".equals(manageVO.getPostScope())) {
            List<Long> postList = JSONArray.parseArray(manageVO.getPostScope(), Long.class);
            if (CollectionUtils.isNotEmpty(postList)) {
                userSelectRequest.setPostIdManageList(postList);
            }
        }
        return userService.querySelectList(userSelectRequest);
    }

    @Override
    public List<DepartmentVO> getDeptSelectList(ManageDeptSystemRequest manageDeptSystemRequest) {
        if (DepartmentTypeEnum.EXTERNAL_DEPT.getCode().equals(manageDeptSystemRequest.getDeptType())) {
            List<DepartmentVO> deptManageList = getDeptManageList(manageDeptSystemRequest.getDeptType());
            if (CollectionUtils.isEmpty(manageDeptSystemRequest.getDepartmentIdList())) {
                return deptManageList;
            }
            deptManageList = deptManageList.stream()
                    .filter(c -> manageDeptSystemRequest.getDepartmentIdList().contains(c.getDeptId()))
                    .collect(Collectors.toList());
            return deptManageList;
        }
        UserDomain user = UserUtils.getUser();
        ManageVO manageVO = ManageCache.getConfig(user.getCompanyId(), user.getUserIdLongValue());
        if (manageVO == null) {
            List<DepartmentVO> departmentVOList =
                    DepartmentCache.getValue(user.getCompanyId(), manageDeptSystemRequest.getDeptType());
            return departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                    .collect(Collectors.toList());
        }
        if (manageVO.getSuperManage() || "ALL".equals(manageVO.getDeptScopeType())) {
            List<DepartmentVO> departmentVOList =
                    DepartmentCache.getValue(user.getCompanyId(), manageDeptSystemRequest.getDeptType());
            return departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                    .collect(Collectors.toList());
        }
        List<Long> deptList = JSONArray.parseArray(manageVO.getDeptScope(), Long.class);
        List<DepartmentVO> departmentVOList =
                departmentService.querySelectList(deptList, manageDeptSystemRequest.getDeptType());
        return departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0")).collect(Collectors.toList());
    }

    @Override
    public QueryPageVO<PostVO> getPostSelectList(ManagePostSystemRequest managePostSystemRequest) {
        UserDomain user = UserUtils.getUser();
        PostSelectRequest postSelectRequest = new PostSelectRequest();
        postSelectRequest.setPageNum(managePostSystemRequest.getPageNum());
        postSelectRequest.setPageSize(managePostSystemRequest.getPageSize());
        ManageVO manageVO = ManageCache.getConfig(user.getCompanyId(), user.getUserIdLongValue());
        if (manageVO == null) {
            return new QueryPageVO<>();
        }
        if (manageVO.getSuperManage() || "ALL".equals(manageVO.getDeptScopeType())) {
            return postService.selectList(postSelectRequest);
        }
        postSelectRequest.setPostIdList(JSONObject.parseArray(manageVO.getPostScope(), Long.class));
        return postService.selectList(postSelectRequest);
    }

    @Override
    public QueryPageVO<UserReturnVO> getUserManageList(UserRequest userRequest) {
        ManageVO manageVO =
                ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
        if (manageVO != null && !manageVO.getSuperManage() &&
                UserTypeEnum.EXTERNAL.getCode().equals(userRequest.getUserType())) {
            List<Long> currentUserRelationCompany = companyRelationService.getCurrentUserRelationCompany();
            if (CollectionUtils.isEmpty(currentUserRelationCompany)) {
                return new QueryPageVO<>();
            }
            userRequest.setSourceCompanyId(currentUserRelationCompany);
        }
        return userService.queryList(userRequest);
    }

    @Override
    public QueryPageVO<UserVO> getUseList(UserSelectRequest userSelectRequest) {
        ManageVO manageVO =
                ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
        if (manageVO != null && !manageVO.getSuperManage() &&
                UserTypeEnum.EXTERNAL.getCode().equals(userSelectRequest.getUserType())) {
            List<Long> currentUserRelationCompany = companyRelationService.getCurrentUserRelationCompany();
            if (CollectionUtils.isEmpty(currentUserRelationCompany)) {
                return new QueryPageVO<>();
            }
            userSelectRequest.setSourceCompanyId(currentUserRelationCompany);
        }
        return userService.querySelectList(userSelectRequest);
    }

    @Override
    public List<DepartmentVO> getDeptManageList(String deptType) {
        List<DepartmentVO> departmentVOList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), deptType);
        if (DepartmentTypeEnum.EXTERNAL_DEPT.getCode().equals(deptType)) {
            departmentVOList = departmentVOList.stream().filter(c -> c.getParentId().toString().equals("0"))
                    .collect(Collectors.toList());
        } else {
            departmentVOList = departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                    .collect(Collectors.toList());
        }
        ManageVO manageVO =
                ManageCache.getConfig(UserUtils.getUser().getCompanyId(), UserUtils.getUser().getUserIdLongValue());
        if (manageVO != null && !manageVO.getSuperManage() && UserTypeEnum.EXTERNAL.getCode().equals(deptType)) {
            List<Long> currentUserRelationCompany = companyRelationService.getCurrentUserRelationCompany();
            return departmentVOList.stream().filter(c -> c.getSourceCompanyId() != null &&
                    currentUserRelationCompany.contains(c.getSourceCompanyId())).collect(Collectors.toList());
        }
        return departmentVOList;
    }
}
