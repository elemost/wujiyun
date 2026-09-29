package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractAcrossAppConverter;
import com.wuji.service.mapper.AcrossAppMapper;
import com.wuji.service.model.entity.AcrossAppEntity;
import com.wuji.service.model.request.AcrossAppSaveRequest;
import com.wuji.service.model.vo.AcrossAppVO;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.AcrossAppService;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-22
 */
@Service
public class AcrossAppServiceImpl extends ServiceImpl<AcrossAppMapper, AcrossAppEntity> implements AcrossAppService {

    @Autowired
    private AcrossAppMapper acrossAppMapper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FormService formService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<AcrossAppSaveRequest> acrossAppSaveRequests, String configAppId) {
        LambdaQueryWrapper<AcrossAppEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(AcrossAppEntity::getConfigAppId, configAppId);
        acrossAppMapper.delete(deleteWrapper);
        if (CollectionUtils.isEmpty(acrossAppSaveRequests)) {
            return;
        }
        List<AcrossAppEntity> acrossAppEntityList =
                acrossAppSaveRequests.stream().map(AbstractAcrossAppConverter.INSTANCE::toEntity)
                        .collect(Collectors.toList());
        for (AcrossAppEntity acrossAppEntity : acrossAppEntityList) {
            acrossAppEntity.setCreatorName(UserUtils.getUser().getNickName());
            acrossAppEntity.setModifierName(UserUtils.getUser().getNickName());
            acrossAppEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            acrossAppEntity.setConfigAppId(configAppId);
        }
        saveBatch(acrossAppEntityList);
    }

    @Override
    public List<AcrossAppVO> getByAppId(String configAppId) {
        LambdaQueryWrapper<AcrossAppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AcrossAppEntity::getConfigAppId, configAppId);
        List<AcrossAppEntity> acrossAppEntityList = acrossAppMapper.selectList(queryWrapper);
        List<String> applicationIdList =
                acrossAppEntityList.stream().map(AcrossAppEntity::getApplicationId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(acrossAppEntityList)) {
            return new ArrayList<>();
        }
        List<ApplicationVO> applicationVOS = applicationService.getApplicationByIdList(applicationIdList);
        Map<String, String> appIdToNameMap = applicationVOS.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<ApplicationCategoryVO> applicationCategoryVOS =
                applicationCategoryService.getByAppIdList(applicationIdList);
        Map<String, String> idToNameMap = applicationCategoryVOS.stream().collect(
                Collectors.toMap(c -> c.getId() + "_" + c.getApplicationId(), ApplicationCategoryVO::getCategoryName));
        List<AcrossAppVO> acrossAppList = new ArrayList<>();
        for (AcrossAppEntity acrossAppEntity : acrossAppEntityList) {
            AcrossAppVO acrossAppVO = AbstractAcrossAppConverter.INSTANCE.toVO(acrossAppEntity);
            acrossAppVO.setApplicationName(appIdToNameMap.get(acrossAppEntity.getApplicationId()));
            acrossAppVO.setFormName(
                    idToNameMap.get(acrossAppEntity.getFormId() + "_" + acrossAppEntity.getApplicationId()));
            acrossAppList.add(acrossAppVO);
        }
        return acrossAppList;
    }
}
