package com.wuji.service.model.entity;

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
 * @since 2024-11-29
 */
@Getter
@Setter
@TableName("lc_form_use_time")
@ApiModel(value = "FormUseTimeEntity对象", description = "")
public class FormUseTimeEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;


    @ApiModelProperty("应用id")
    private String formId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("使用时间")
    private Date useTime;

    private String applicationId;

    private Long companyId;
}
