package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompanyRelationSaveRequest {
    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("关联公司")
    private Long relatedCompany;

    private String relatedType;
}
