package com.wuji.admin.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserUpdateRequest {
    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

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


    @ApiModelProperty("最后登录时间")
    private Date loginDate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("行业")
    private String industry;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("职位")
    private String position;

    @ApiModelProperty("需求")
    private String demand;

    private String interests;

    @ApiModelProperty("汇报对象")
    private String reportLeader;

    @ApiModelProperty("入职时间")
    private Date entryTime;

    private List<Long> roleIdList;

    private List<Long> deptIdList;

    private List<Long> postList;

    private List<UserInfoSaveRequest> userInfoList;
}
