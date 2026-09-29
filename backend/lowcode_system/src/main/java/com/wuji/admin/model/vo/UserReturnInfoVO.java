package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.model.vo.UserInfoVO;
import com.wuji.common.model.vo.UserPostVO;
import com.wuji.common.model.vo.UserRoleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserReturnInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("用户编码")
    private String userCode;

    @ApiModelProperty("用户对外唯一值")
    private String uuid;

    @ApiModelProperty("企业id")
    private Long companyId;

    private String companyUuid;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("用户账号")
    private String userName;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("用户类型（00系统用户）")
    private String userType;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String phonenumber;

    @ApiModelProperty("用户性别（0男 1女 2未知）")
    private String sex;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("帐号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty("最后登录IP")
    private String loginIp;

    @ApiModelProperty("最后登录时间")
    private Date loginDate;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("行业")
    private String industry;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("职位")
    private String position;

    @ApiModelProperty("大屏数量")
    private Integer screenCount;

    @ApiModelProperty("已使用大屏数量")
    private Integer screenUseCount;

    @ApiModelProperty("需求")
    private String demand;

    private String interests;

    private String deptName;

    private String dataSource;

    private List<Long> roleIdList;

    private List<Long> deptIdList;

    private List<Long> postIdList;

    private List<UserRoleVO> roles;

    private List<DepartmentVO> departmentList;

    private List<UserDeptVO> userDeptList;

    private List<UserPostVO> userPostList;

    private List<UserInfoVO> userInfoList;

    private String startTime;
    private String endTime;

    private Boolean adminUser;

    private List<CorpCoopVO> corpCompanyList;

    private String encryptPhone;
}
