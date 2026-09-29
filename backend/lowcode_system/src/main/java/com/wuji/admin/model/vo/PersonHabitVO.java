package com.wuji.admin.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PersonHabitVO {

    private String id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("爱好")
    private String appetite;

    private String position;

    private String company;

    private String mobile;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("key")
    private String internalKey;

    @ApiModelProperty("是否最后一个版本")
    private Boolean lastVersion;

    protected Date createTime;
}
