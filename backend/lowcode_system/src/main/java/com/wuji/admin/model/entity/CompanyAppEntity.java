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
 * 企业使用产品限制
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@Getter
@Setter
@TableName("wj_company_app")
@ApiModel(value = "CompanyAppEntity对象", description = "企业使用产品限制")
public class CompanyAppEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("机构id")
    private Long orgId;

    @ApiModelProperty("app代码id")
    private String clientId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("应用版本标记")
    private String appVersion;

    private String equityId;
}
