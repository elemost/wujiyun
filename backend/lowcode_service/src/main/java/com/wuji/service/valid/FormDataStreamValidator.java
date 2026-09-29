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
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.utils.FunctionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FormDataStreamValidator extends AbstractFunctionValidator {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Override
    protected boolean validateResources(String applicationId, String clientCode, String clientName) {
        Boolean checkValue = FunctionUtils.checkApplicationFunction(applicationId);
        if (checkValue != null) {
            return checkValue;
        }
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        if (CollectionUtils.isEmpty(allApplication)) {
            return true;
        }
        Long count = formDataStreamService.getCountByApplication(
                allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList()));
        CompanyAppDetailVO companyAppDetailVO = CompanyAppCache.getCompanyApp(UserUtils.getUser().getCompanyId());
        if (companyAppDetailVO.getExpire()) {
            throw new BizException(ResultCode.NO_AUTH_1, Constants.freeErrorMessage);
        }
        Map<String, ClientFunctionVO> clientFunctionMap = companyAppDetailVO.getClientFunctionMap();
        ClientFunctionVO clientFunctionVO = clientFunctionMap.get(clientCode);
        if (clientFunctionVO == null) {
            throw new ServiceException(ResultCode.NO_AUTH_1);
        }
        if (clientFunctionVO.getLimitCount() == null) {
            return true;
        }
        if (clientFunctionVO.getLimitCount() <= count) {
            CompanyAppVO companyAppVO = companyAppDetailVO.getCompanyAppVO();
            String errorMessage = Constants.getErrorMessage("数智助手", companyAppVO);
            throw new BizException(ResultCode.NO_AUTH_1, errorMessage);
        } else {
            return true;
        }
    }
}
