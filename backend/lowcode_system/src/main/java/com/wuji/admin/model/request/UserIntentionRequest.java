package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserIntentionRequest implements Serializable {
    private Long id;

    /**
     * 是否使用
     */
    @ApiModelProperty(value="电话")
    private String mobile;

    /**
     * 名字
     */
    @ApiModelProperty(value="名字")
    private String nickName;

    /**
     * 需求
     */
    @ApiModelProperty(value="需求")
    private String message;

    /**
     * 来源
     */
    @ApiModelProperty(value="来源 ")
    private String source;

    /**
     * 职位
     */
    @ApiModelProperty(value="职位")
    private String position;

    /**
     * 公司名称
     */
    @ApiModelProperty(value="公司名称")
    private String companyName;

    private String city;

    /**
     * 合作形式
     */
    @ApiModelProperty(value="合作形式")
    private String cooperationForm;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    @ApiModelProperty("管理需求")
    private String manageDemand;

    @ApiModelProperty("了解途径")
    private String understandWay;

    @ApiModelProperty("是否使用过低代码")
    private String useLowcode;

    private Long companyId;

    private Long userId;

    private Integer intentionType;

    private String industry;

    private static final long serialVersionUID = 1L;
}
