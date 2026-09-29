package com.wuji.admin.model.entity;

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
 * 用户意向
 * </p>
 *
 * @author hzm
 * @since 2025-05-14
 */
@Getter
@Setter
@TableName("wj_user_intention")
@ApiModel(value = "UserIntentionEntity对象", description = "用户意向")
public class UserIntentionEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("是否使用")
    private String mobile;

    @ApiModelProperty("名字")
    private String nickName;

    @ApiModelProperty("需求")
    private String message;

    @ApiModelProperty("来源 ")
    private String source;

    @ApiModelProperty("职位")
    private String position;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("合作形式")
    private String cooperationForm;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("行业")
    private String industry;

    @ApiModelProperty("管理需求")
    private String manageDemand;

    @ApiModelProperty("了解途径")
    private String understandWay;

    @ApiModelProperty("是否接触低代码")
    private String useLowcode;

    @ApiModelProperty("1填写 2注册")
    private Integer intentionType;
}
