package com.wuji.admin.service.impl;

import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.RoleService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.enums.DeptDefaultEnum;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminCommonServiceImpl implements AdminCommonService {

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PostService postService;

    @Override
    public List<FormUser> getSystemUser() {
        List<FormUser> formUserList = new ArrayList<>();
        for (UserDefaultEnum userDefaultEnum : UserDefaultEnum.systemUser()) {
            FormUser formUser = new FormUser();
            formUser.setAssigneeName(userDefaultEnum.getName());
            formUser.setAssigneeId(userDefaultEnum.getId());
            formUserList.add(formUser);
        }
        return formUserList;
    }

    @Override
    public List<FormDept> getSystemDept() {
        List<FormDept> formDeptList = new ArrayList<>();
        for (DeptDefaultEnum deptDefaultEnum : DeptDefaultEnum.values()) {
            FormDept formDept = new FormDept();
            formDept.setLabel(deptDefaultEnum.getName());
            formDept.setValue(deptDefaultEnum.getId());
            formDeptList.add(formDept);
        }
        return formDeptList;
    }

    @Override
    public SystemAllDataVO getSystemAllData() {
        SystemAllDataVO importCheck = new SystemAllDataVO();
        List<UserCompanyVO> allUser = userCompanyService.getAllUser();
        Map<Long, String> userNameMap = new HashMap<>();
        for (UserCompanyVO userCompanyVO : allUser) {
            userNameMap.put(userCompanyVO.getUserId(), userCompanyVO.getNickName());
        }
        importCheck.setUserIdToNameMap(userNameMap);
        Map<Long, UserCompanyVO> userIdCompanyMap = new HashMap<>();
        for (UserCompanyVO userCompanyVO : allUser) {
            userIdCompanyMap.put(userCompanyVO.getUserId(), userCompanyVO);
        }
        importCheck.setUserIdCompanyMap(userIdCompanyMap);
        List<Long> userIdList = allUser.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userService.queryByIds(userIdList);
        Map<Long, UserVO> userIdToVOMap = userVOS.stream().collect(Collectors.toMap(UserVO::getUserId, c -> c));
        List<PostVO> allRole = postService.getAllPost();
        importCheck.setUserIdMap(userIdToVOMap);
        importCheck.setRoleIdMap(allRole.stream().collect(Collectors.toMap(PostVO::getPostId, c -> c)));
        List<DepartmentVO> departmentVOList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(),
                DepartmentTypeEnum.INTERNAL_DEPT.getCode());
        Map<String, List<DepartmentVO>> deptNameMap =
                departmentVOList.stream().collect(Collectors.groupingBy(DepartmentVO::getDeptName));
        importCheck.setDeptNameMap(deptNameMap);
        importCheck.setDeptIdMap(departmentVOList.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, c -> c)));
        List<DepartmentVO> allDeptList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), null);
        importCheck.setDeptIdToNameMap(
                allDeptList.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, DepartmentVO::getDeptName)));
        return importCheck;
    }

    @Override
    public SystemAllDataNameVO getSystemAllDataName() {
        List<UserCompanyVO> allUser = userCompanyService.getAllUser();
        List<Long> userIdList = allUser.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userService.queryByIds(userIdList);
        userVOS = userVOS.stream().filter(c -> StringUtils.isNotEmpty(c.getPhonenumber())).collect(Collectors.toList());
        Map<String, UserVO> phoneNumberMap = userVOS.stream().collect(Collectors.toMap(UserVO::getPhonenumber, c -> c));
        List<DepartmentVO> departmentVOList = departmentService.queryAllList(UserUtils.getUser().getCompanyId(),
                DepartmentTypeEnum.INTERNAL_DEPT.getCode(), Boolean.TRUE);
        Map<String, List<DepartmentVO>> deptNameMap =
                departmentVOList.stream().collect(Collectors.groupingBy(DepartmentVO::getDeptName));
        SystemAllDataNameVO importCheck = new SystemAllDataNameVO();
        Map<String, List<UserCompanyVO>> nickNameMap =
                allUser.stream().collect(Collectors.groupingBy(UserCompanyVO::getNickName));
        importCheck.setNickNameMap(nickNameMap);
        importCheck.setDeptNameMap(deptNameMap);
        importCheck.setPhonenumberMap(phoneNumberMap);
        List<PostVO> allRole = postService.getAllPost();
        Map<String, PostVO> roleMap = new HashMap<>();
        for (PostVO roleVO : allRole) {
            roleMap.put(roleVO.getPostName(), roleVO);
        }
        importCheck.setRoleMap(roleMap);
        return importCheck;
    }
}
