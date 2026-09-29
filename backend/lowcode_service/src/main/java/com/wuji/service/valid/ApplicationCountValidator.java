package com.wuji.service.valid;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.privilege.resource.AbstractFunctionValidator;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.mapper.ApplicationMapper;
import com.wuji.service.model.entity.ApplicationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApplicationCountValidator extends AbstractFunctionValidator {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    protected boolean validateResources(String applicationId, String clientCode, String clientName) {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        Long count = applicationMapper.selectCount(queryWrapper);
        CompanyAppDetailVO companyAppDetailVO = CompanyAppCache.getCompanyApp(UserUtils.getUser().getCompanyId());
        if (companyAppDetailVO.getExpire()) {
            if (count > 0) {
                throw new BizException(ResultCode.NO_AUTH_1, Constants.freeErrorMessage);
            } else {
                return true;
            }
        }
        Map<String, ClientFunctionVO> clientFunctionMap = companyAppDetailVO.getClientFunctionMap();
        ClientFunctionVO clientFunctionVO = clientFunctionMap.get(clientCode);
        if (clientFunctionVO == null) {
            throw new BizException(ResultCode.NO_AUTH_1, "当前版本无法安装应用，请升级版本!");
        }
        if (clientFunctionVO.getLimitCount() == null) {
            return true;
        }
        if (clientFunctionVO.getLimitCount() <= count) {
            CompanyAppVO companyAppVO = companyAppDetailVO.getCompanyAppVO();
            String errorMessage = Constants.getErrorMessage("应用数量", companyAppVO);
            throw new BizException(ResultCode.NO_AUTH_1, errorMessage);
        } else {
            return true;
        }
    }
}
