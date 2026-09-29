package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 五极短信模板
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Getter
@Setter
@TableName("wj_sms_template")
@ApiModel(value = "SmsTemplateEntity对象", description = "五极短信模板")
public class SmsTemplateEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("短信服务商:alibaba,tencent")
    private String provider;

    @ApiModelProperty("短信使用场景描述")
    private String smsScene;

    @ApiModelProperty("服务商短信模板id")
    private String templateId;

    @ApiModelProperty("短信签名")
    private String signName;

    @ApiModelProperty("短信参数名集合:[\"name\",\"code\"]")
    private String paramName;

    @ApiModelProperty("短信服务商appid")
    private String appId;
}
