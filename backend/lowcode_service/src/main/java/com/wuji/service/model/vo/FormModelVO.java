package com.wuji.service.model.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormModelVO {
    /**
     * 主键自增id
     */
    private Long id;

    @ApiModelProperty("表单id")
    private String formId;

    @ApiModelProperty("流程模块id")
    private String modelId;

    private String businessType;

    private String status;

    private String applicationId;

}
