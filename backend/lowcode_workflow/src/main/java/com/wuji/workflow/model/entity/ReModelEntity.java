package com.wuji.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hzm
 * @since 2026-05-18
 */
@Getter
@Setter
@TableName("act_re_model")
@ApiModel(value = "ReModelEntity对象", description = "")
public class ReModelEntity {

    @TableId(type = IdType.AUTO, value = "ID_")
    // @TableField(value = "ID_")
    private String id;

    @TableField(value = "REV_")
    private Integer rev;

    @TableField(value = "NAME_")
    private String name;

    @TableField(value = "KEY_")
    private String key;

    @TableField(value = "CATEGORY_")
    private String category;

    @TableField(value = "CREATE_TIME_")
    private Date createTime;

    @TableField(value = "LAST_UPDATE_TIME_")
    private Date lastUpdateTime;

    @TableField(value = "VERSION_")
    private Integer version;

    @TableField(value = "META_INFO_")
    private String metaInfo;

    @TableField(value = "DEPLOYMENT_ID_")
    private String deploymentId;

    @TableField(value = "EDITOR_SOURCE_VALUE_ID_")
    private String editorSourceValueId;

    @TableField(value = "EDITOR_SOURCE_EXTRA_VALUE_ID_")
    private String editorSourceExtraValueId;

    @TableField(value = "TENANT_ID_")
    private String tenantId;
}
