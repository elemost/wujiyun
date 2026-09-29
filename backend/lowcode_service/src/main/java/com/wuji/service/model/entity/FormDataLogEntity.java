package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 表单数据日志
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
@Getter
@Setter
@TableName("lc_form_data_log")
@ApiModel(value = "FormDataLogEntity对象", description = "表单数据日志")
public class FormDataLogEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    /**
     * 创建时间
     */
    protected Date createTime;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("记录行id")
    private String recordId;

    @ApiModelProperty("新建修改")
    private String logAction;

    @ApiModelProperty("记录详情")
    private String logContent;

    @ApiModelProperty("创建人")
    private String creator;
}
