package com.wuji.service.valid;

import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.privilege.resource.AbstractFunctionValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class AggregateTableValidator extends AbstractFunctionValidator {

    @Autowired
    private FormAggregateService formAggregateService;

    @Override
    protected boolean validateResources(String applicationId, String clientCode, String clientName) {
        Boolean checkValue = FunctionUtils.checkApplicationFunction(applicationId);
        if (checkValue != null) {
            return checkValue;
        }
        CompanyAppDetailVO companyAppDetailVO = CompanyAppCache.getCompanyApp(UserUtils.getUser().getCompanyId());
        Map<String, ClientFunctionVO> clientFunctionMap = companyAppDetailVO.getClientFunctionMap();
        Long count = formAggregateService.getCountByApplication(Collections.singletonList(applicationId));
        ClientFunctionVO clientFunctionVO = clientFunctionMap.get(clientCode);
        if (companyAppDetailVO.getExpire()) {
            throw new BizException(ResultCode.NO_AUTH_1, Constants.freeErrorMessage);
        }
        if (clientFunctionVO == null) {
            throw new ServiceException(ResultCode.NO_AUTH_1);
        }
        if (clientFunctionVO.getLimitCount() == null) {
            return true;
        }
        if (clientFunctionVO.getLimitCount() <= count) {
            CompanyAppVO companyAppVO = companyAppDetailVO.getCompanyAppVO();
            String errorMessage = Constants.getErrorMessageApp(companyAppVO, "应用的聚合表数量");
            throw new BizException(ResultCode.NO_AUTH_1, errorMessage);
        } else {
            return true;
        }
    }
}
