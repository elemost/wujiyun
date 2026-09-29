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
 * @since 2025-02-26
 */
@Getter
@Setter
@TableName("lc_template_application_img")
@ApiModel(value = "TemplateApplicationImgEntity对象", description = "")
public class TemplateApplicationImgEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String applicationId;

    private String imgUrl;
}
