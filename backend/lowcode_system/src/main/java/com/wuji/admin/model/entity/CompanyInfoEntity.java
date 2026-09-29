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
 * @since 2025-04-07
 */
@Getter
@Setter
@TableName("wj_company_info")
@ApiModel(value = "CompanyInfoEntity对象", description = "")
public class CompanyInfoEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long companyId;

    @ApiModelProperty("配置key")
    private String configKey;

    @ApiModelProperty("配置的值")
    private String configValue;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
