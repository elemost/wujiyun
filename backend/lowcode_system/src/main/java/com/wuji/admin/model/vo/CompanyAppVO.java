package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CompanyAppVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("机构id")
    private Long orgId;

    @ApiModelProperty("app代码id")
    private String clientId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("应用版本标记")
    private String appVersion;

    private String equityId;

    private String equityName;

    private String remark;

    private Boolean buyOut;
}
