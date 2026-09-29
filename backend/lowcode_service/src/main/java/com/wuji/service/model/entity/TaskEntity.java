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
 * @since 2025-11-28
 */
@Getter
@Setter
@TableName("lc_task")
@ApiModel(value = "TaskEntity对象", description = "")
public class TaskEntity {

    @TableId(type = IdType.AUTO)
    protected String id;

    private String taskType;

    private String result;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("页面id")
    private String formId;

    protected Date createTime;

    private String status;

    private String input;
}
