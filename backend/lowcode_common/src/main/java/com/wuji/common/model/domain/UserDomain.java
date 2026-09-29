package com.wuji.common.model.domain;

import com.wuji.common.model.vo.UserDeptVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserDomain {
    private String userId;

    private String uuid;

    private Long userIdLongValue;

    private String userName;

    private String nickName;

    private String ip;

    private Long companyId;

    private String companyUuid;

    @ApiModelProperty("真实姓名")
    private String realName;

    private Long deptId;

    private List<Long> deptIdList;

    private List<Long> dataScopeDeptIdList;

    private List<Long> postIdList;
    private String startTime;
    private String endTime;

    private List<UserDeptVO> userDeptList;

    private Set<String> permissions;

    private Boolean adminUser;

    private String userType;

    private String suiteId;
}
