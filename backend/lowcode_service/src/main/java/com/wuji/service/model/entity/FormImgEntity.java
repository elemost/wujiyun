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
 * @since 2025-03-24
 */
@Getter
@Setter
@TableName("lc_form_img")
@ApiModel(value = "FormImgEntity对象", description = "")
public class FormImgEntity extends BaseUuidEntity {

    private String imgUrl;

    private String imgType;

    private String imgKey;

    private String fileName;

    private Boolean secret;

    private String bucket;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private Long companyId;
}
