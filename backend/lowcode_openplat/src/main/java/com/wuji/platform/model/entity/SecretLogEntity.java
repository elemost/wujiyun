package com.wuji.platform.model.entity;

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
 * @since 2025-08-07
 */
@Getter
@Setter
@TableName("op_secret_log")
@ApiModel(value = "SecretLogEntity对象", description = "")
public class SecretLogEntity {

    @TableId(type = IdType.AUTO)
    protected String id;

    private String secretId;

    @ApiModelProperty("请求参数")
    private String requestParam;

    @ApiModelProperty("请求结果")
    private String result;

    private String apiName;

    private String errorMessage;

    /**
     * 创建时间
     */
    protected Date createTime;
}
