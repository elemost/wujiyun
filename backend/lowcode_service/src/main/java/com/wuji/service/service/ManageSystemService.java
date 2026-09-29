package com.wuji.service.service;

import com.wuji.admin.model.request.UserRequest;
import com.wuji.admin.model.request.UserSelectRequest;
import com.wuji.admin.model.vo.UserReturnVO;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.service.model.request.ManageDeptSystemRequest;
import com.wuji.service.model.request.ManagePostSystemRequest;
import com.wuji.service.model.request.ManageUserSystemRequest;

import java.util.List;

public interface ManageSystemService {
    QueryPageVO<UserVO> getUserByGroupId(ManageUserSystemRequest manageUserSystemRequest);

    List<DepartmentVO> getDeptSelectList(ManageDeptSystemRequest manageDeptSystemRequest);

    QueryPageVO<PostVO> getPostSelectList(ManagePostSystemRequest managePostSystemRequest);

    QueryPageVO<UserReturnVO> getUserManageList(UserRequest userRequest);

    QueryPageVO<UserVO> getUseList(UserSelectRequest userSelectRequest);

    List<DepartmentVO> getDeptManageList(String deptType);
}
