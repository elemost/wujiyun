package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SsoLoginConfigInfoVO {
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
