package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TemplateApplicationVO {

    private String id;


    private String sourceApplicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("访问地址")
    private String visitUrl;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("应用类型")
    private String applicationType;

    @ApiModelProperty("图标")
    private String icon;

    private String introduce;

    private Integer downloadCount;

    private String logo;

    private Date createTime;

    private String applicationNature;

    private List<TemplateApplicationImgVO> imgList;

    private List<TemplateApplicationTagVO> tags;

    private  List<TemplateApplicationTagVO> hotTags;
}
