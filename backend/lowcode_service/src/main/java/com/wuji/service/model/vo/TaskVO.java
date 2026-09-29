package com.wuji.service.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wuji.service.model.info.ImportProgress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TaskVO {
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

    private ImportProgress progress;
}
