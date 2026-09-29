package com.wuji.service.model.entity;

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
 * @since 2025-08-08
 */
@Getter
@Setter
@TableName("lc_manage")
@ApiModel(value = "ManageEntity对象", description = "")
public class ManageEntity extends BaseUuidEntity {

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("管理组名称")
    private String manageName;

    @ApiModelProperty("部门管理")
    private String deptScope;

    @ApiModelProperty("职位管理范围")
    private String postScope;

    @ApiModelProperty("内部角色可见")
    private Boolean roleRead;

    private String deptScopeType;

    private String postScopeType;

    @ApiModelProperty("内部角色可管理")
    private Boolean roleWrite;

    @ApiModelProperty("内部部门")
    private Boolean deptManage;

    @ApiModelProperty("互联组织")
    private Boolean corpCoopManage;

    @ApiModelProperty("是否可操作应用")
    private Boolean appUpdate;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;
}
