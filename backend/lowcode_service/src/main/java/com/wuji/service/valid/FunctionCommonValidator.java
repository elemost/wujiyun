package com.wuji.service.valid;

import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.privilege.resource.AbstractFunctionValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.utils.FunctionUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FunctionCommonValidator extends AbstractFunctionValidator {

    @Override
    protected boolean validateResources(String applicationId, String clientCode, String clientName) {
        Boolean checkValue = FunctionUtils.checkApplicationFunction(applicationId);
        if (checkValue != null) {
            return checkValue;
        }
        CompanyAppDetailVO companyAppDetailVO = CompanyAppCache.getCompanyApp(UserUtils.getUser().getCompanyId());
        Map<String, ClientFunctionVO> clientFunctionMap = companyAppDetailVO.getClientFunctionMap();
        ClientFunctionVO clientFunctionVO = clientFunctionMap.get(clientCode);
        if (clientFunctionVO == null) {
            throw new BizException(ResultCode.NO_AUTH_1, "当前版本不存在" + clientName + "权益，请升级版本！");
        }
        return true;
    }
}
