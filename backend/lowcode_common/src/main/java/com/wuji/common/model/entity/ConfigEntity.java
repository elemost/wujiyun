package com.wuji.common.model.entity;

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
 * @since 2024-05-09
 */
@Getter
@Setter
@TableName("sys_config")
@ApiModel(value = "ConfigEntity对象", description = "")
public class ConfigEntity {

    @ApiModelProperty("参数主键")
    @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    @ApiModelProperty("参数名称")
    private String configName;

    @ApiModelProperty("参数键名")
    private String configKey;

    @ApiModelProperty("参数键值")
    private String configValue;

    @ApiModelProperty("系统内置（Y是 N否）")
    private String configType;

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
}
