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
 * @since 2026-05-09
 */
@Getter
@Setter
@TableName("lc_application_share")
@ApiModel(value = "ApplicationShareEntity对象", description = "")
public class ApplicationShareEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    private String applicationId;

    private Integer expireDay;

    private Date expireTime;

    private Boolean needData;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    /**
     * 创建时间
     */
    private Date createTime;
}
