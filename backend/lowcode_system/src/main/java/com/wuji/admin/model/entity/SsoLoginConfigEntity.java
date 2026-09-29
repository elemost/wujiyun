package com.wuji.admin.model.entity;

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
 * @since 2026-09-16
 */
@Getter
@Setter
@TableName("sys_sso_login_config")
@ApiModel(value = "SsoLoginConfigEntity对象", description = "")
public class SsoLoginConfigEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long companyId;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private Boolean status;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("配置类型")
    private String configType;
}
