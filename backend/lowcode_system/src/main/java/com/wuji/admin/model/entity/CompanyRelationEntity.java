package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
@Getter
@Setter
@TableName("wj_company_relation")
@ApiModel(value = "CompanyRelationEntity对象", description = "")
public class CompanyRelationEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("关联公司")
    private Long relatedCompany;

    private String relatedType;

    private Long userId;
}
