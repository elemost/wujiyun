package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 应用目录
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Getter
@Setter
@TableName("lc_template_application_category")
@ApiModel(value = "TemplateApplicationCategoryEntity对象", description = "应用目录")
public class TemplateApplicationCategoryEntity extends BaseUuidEntity {

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("公司id")
    private Long companyId;

    private String parentId;

    private String sourceId;

    @ApiModelProperty("目录名称")
    private String categoryName;

    @ApiModelProperty("类目名称")
    private String categoryType;

    @ApiModelProperty("显示类型")
    private String showType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("是否发布")
    private Boolean published;

    @ApiModelProperty("icon")
    private String icon;

    private Integer sortNum;
}
