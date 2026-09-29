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
 * @since 2024-04-15
 */
@Getter
@Setter
@TableName("wj_company")
@ApiModel(value = "CompanyEntity对象", description = "")
public class CompanyEntity {

    @ApiModelProperty("公司名称")
    private String companyName;

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String channelType;

    private Long mainId;

    private String companyUuid;

    private String pullConfig;

    private String secretId;

    private String dataSource;

    private String createBy;

    private String updateBy;

    private Date updateTime;

    private Date createTime;

    private Short status;

    private Short companyType;

}
