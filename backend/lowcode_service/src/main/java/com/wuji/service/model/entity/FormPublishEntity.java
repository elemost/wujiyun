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
 * 表单发布表
 * </p>
 *
 * @author hzm
 * @since 2024-08-20
 */
@Getter
@Setter
@TableName("lc_form_publish")
@ApiModel(value = "FormPublishEntity对象", description = "表单发布表")
public class FormPublishEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;


    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("来源id")
    private String sourceId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("最后一个版本")
    private Boolean lastVersion;

    @ApiModelProperty("对应表名")
    private String tableName;

    private String formType;

    @ApiModelProperty("创建人")
    private String creator;
    /**
     * 创建时间
     */
    protected Date createTime;

    private String applicationId;

    private String formConfig;
}
