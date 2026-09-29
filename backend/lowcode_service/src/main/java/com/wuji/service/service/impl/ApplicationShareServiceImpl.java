package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.service.ConfigService;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationShareConverter;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.mapper.ApplicationShareMapper;
import com.wuji.service.model.entity.ApplicationShareEntity;
import com.wuji.service.model.request.ApplicationShareCreateRequest;
import com.wuji.service.model.vo.ApplicationShareVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.ApplicationShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-05-09
 */
@Service
public class ApplicationShareServiceImpl extends ServiceImpl<ApplicationShareMapper, ApplicationShareEntity>
        implements ApplicationShareService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompanyService companyService;

    @Override
    public String createShare(ApplicationShareCreateRequest applicationShareCreateRequest) {
        ApplicationShareEntity applicationShareEntity = new ApplicationShareEntity();
        applicationShareEntity.setId(ObjectId.getGuid());
        applicationShareEntity.setApplicationId(applicationShareCreateRequest.getApplicationId());
        applicationShareEntity.setCreatorName(UserUtils.getUser().getNickName());
        applicationShareEntity.setNeedData(applicationShareCreateRequest.getNeedData());
        Date dataZero = TimeUtils.getDataZero(new Date(), applicationShareCreateRequest.getExpireDay());
        applicationShareEntity.setExpireTime(dataZero);
        applicationShareEntity.setExpireDay(applicationShareCreateRequest.getExpireDay());
        save(applicationShareEntity);
        return applicationShareEntity.getId();
    }

    @Override
    public ApplicationShareVO info(String shareId) {
        ApplicationShareEntity applicationShareEntity = getById(shareId);
        if (applicationShareEntity == null) {
            throw new ServiceException(ServiceResultCode.APPLICATION_SHARE_NOT_EXIST);
        }
        ApplicationShareVO applicationShareVO = AbstractApplicationShareConverter.INSTANCE.toVO(applicationShareEntity);
        if (!applicationShareVO.getExpireTime().after(new Date())) {
            throw new ServiceException(ServiceResultCode.APPLICATION_SHARE_EXPIRE);
        }
        ApplicationVO applicationVO = applicationService.detail(applicationShareVO.getApplicationId());
        applicationShareVO.setApplicationName(applicationVO.getApplicationName());
        applicationShareVO.setDescription(applicationVO.getDescription());
        applicationShareVO.setIcon(applicationVO.getIcon());
        applicationShareVO.setCompanyId(applicationVO.getCompanyId());
        CompanyVO info = companyService.info(applicationVO.getCompanyId());
        applicationShareVO.setCompanyUuid(info.getCompanyUuid());
        return applicationShareVO;
    }
}