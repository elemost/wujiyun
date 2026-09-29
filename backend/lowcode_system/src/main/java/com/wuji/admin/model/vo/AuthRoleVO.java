package com.wuji.admin.model.vo;

import com.wuji.common.model.vo.UserVO;
import lombok.Data;

import java.util.List;

@Data
public class AuthRoleVO {
    private List<RoleVO> roles;
    
    private UserVO user;
}
