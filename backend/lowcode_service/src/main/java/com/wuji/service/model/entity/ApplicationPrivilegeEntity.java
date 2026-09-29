package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hzm
 * @since 2024-10-14
 */
@Getter
@Setter
@TableName("lc_application_privilege")
@ApiModel(value = "ApplicationPrivilegeEntity对象", description = "")
public class ApplicationPrivilegeEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("权限业务类型：用户，部门")
    private String businessType;

    @ApiModelProperty("权限业务id")
    private String businessId;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    private String applicationId;
    /**
     * 创建时间
     */
    protected Date createTime;

    private String privilegeType;
}
