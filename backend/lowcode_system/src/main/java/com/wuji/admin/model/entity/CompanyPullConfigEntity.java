package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
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
 * @since 2024-11-29
 */
@Getter
@Setter
@TableName("sys_company_pull_config")
@ApiModel(value = "CompanyPullConfigEntity对象", description = "")
public class CompanyPullConfigEntity extends BaseUuidEntity {

    @ApiModelProperty("应用id")
    private String appId;

    @ApiModelProperty("拉取配置")
    private String pullConfig;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("创建人名字")
    private String creator;

    @ApiModelProperty("修改人名字")
    private String modifier;

    private String sourceAppId;
}
