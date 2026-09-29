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
 * @since 2024-08-19
 */
@Getter
@Setter
@TableName("lc_application_category")
@ApiModel(value = "ApplicationCategoryEntity对象", description = "应用目录")
public class ApplicationCategoryEntity extends BaseUuidEntity {

    private String applicationId;

    @ApiModelProperty("公司id")
    private Long companyId;

    private String parentId;

    private String sourceId;

    @ApiModelProperty("是否发布过表单")
    private Boolean published;

    @ApiModelProperty("目录名称")
    private String categoryName;

    @ApiModelProperty("目录类型")
    private String categoryType;

    @ApiModelProperty("显示类型")
    private String showType;

    private String icon;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private Integer sortNum;
}
