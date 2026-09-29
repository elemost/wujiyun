package com.wuji.service.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.common.model.request.BasePageRequest;
import com.wuji.service.enums.ApplicationDataScopeEnum;
import com.wuji.service.enums.ApplicationSortTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationQueryRequest extends BasePageRequest {
    /**
     * @see ApplicationDataScopeEnum
     */
    private String dataScope;

    /**
     * @see ApplicationSortTypeEnum
     */
    private String sortType;

    private String applicationName;

    private String state;

    @CorpCoop
    private String companyUuid;

}
