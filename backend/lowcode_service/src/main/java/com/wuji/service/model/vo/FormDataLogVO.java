package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class FormDataLogVO {
    @ApiModelProperty("数据操作类型：新建，更新")
    private String logAction;
    @ApiModelProperty("数据操作用户")
    private String creator;
    @ApiModelProperty("数据操作时间")
    private String createTime;
    @ApiModelProperty("数据操作记录")
    private List<FormDataLogContentVO> list;
}
