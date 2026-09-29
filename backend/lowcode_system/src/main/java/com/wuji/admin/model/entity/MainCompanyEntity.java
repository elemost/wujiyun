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
 * @since 2025-11-10
 */
@Getter
@Setter
@TableName("wj_main_company")
@ApiModel(value = "MainCompanyEntity对象", description = "")
public class MainCompanyEntity {

    @ApiModelProperty("公司id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("公司")
    private String companyName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
