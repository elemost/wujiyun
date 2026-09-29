package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("lc_application_info")
@ApiModel(value = "ApplicationInfoEntity对象", description = "")
public class ApplicationInfoEntity {

    private Long id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("key")
    private String infoKey;

    @ApiModelProperty("value")
    private String infoValue;
}
