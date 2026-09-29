package com.wuji.admin.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SsoLoginConfigSaveRequest {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("状态")
    private Boolean status;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("配置类型")
    private String configType;


}
