package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-03-03
 */
@Getter
@Setter
@TableName("lc_template_application_tag")
@ApiModel(value = "TemplateApplicationTagEntity对象", description = "")
public class TemplateApplicationTagEntity {

    @TableId(type = IdType.AUTO)
    protected Long id;

    private String applicationId;

    private String tagType;

    private String tagValue;
}
