package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.cache.ApplicationCache;
import com.wuji.service.converter.AbstractApplicationInfoConverter;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.mapper.ApplicationInfoMapper;
import com.wuji.service.model.entity.ApplicationInfoEntity;
import com.wuji.service.model.request.ApplicationInfoRequest;
import com.wuji.service.model.vo.ApplicationInfoVO;
import com.wuji.service.service.ApplicationInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-06-03
 */
@Service
public class ApplicationInfoServiceImpl extends ServiceImpl<ApplicationInfoMapper, ApplicationInfoEntity>
        implements ApplicationInfoService {

    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;


    @Override
    public void batchSave(String applicationId, List<ApplicationInfoRequest> applicationInfoList) {
        List<ApplicationInfoEntity> saveList = new ArrayList<>();
        for (ApplicationInfoRequest applicationInfoRequest : applicationInfoList) {
            ApplicationInfoEntity applicationInfoEntity =
                    AbstractApplicationInfoConverter.INSTANCE.toEntity(applicationInfoRequest);
            applicationInfoEntity.setApplicationId(applicationId);
            saveList.add(applicationInfoEntity);
        }
        saveBatch(saveList);
    }

    @Override
    public void insert(String applicationId, String infoKey, String infoValue) {
        ApplicationInfoEntity applicationInfoEntity = new ApplicationInfoEntity();
        applicationInfoEntity.setInfoKey(infoKey);
        applicationInfoEntity.setInfoValue(infoValue);
        applicationInfoEntity.setApplicationId(applicationId);
        applicationInfoMapper.insert(applicationInfoEntity);
        ApplicationCache.clear(applicationId);
    }

    @Override
    public void saveByKey(String applicationId, String infoKey, String infoValue) {
        LambdaQueryWrapper<ApplicationInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationInfoEntity::getInfoKey, infoKey);
        ApplicationInfoEntity applicationInfoEntity = applicationInfoMapper.selectOne(queryWrapper);
        if (applicationInfoEntity == null) {
            applicationInfoEntity = new ApplicationInfoEntity();
            applicationInfoEntity.setApplicationId(applicationId);
            applicationInfoEntity.setInfoKey(infoKey);
        }
        applicationInfoEntity.setInfoValue(infoValue);
        saveOrUpdate(applicationInfoEntity);
    }

    @Override
    public ApplicationInfoVO getByApplicationAndKey(String applicationId, String infoKey) {
        LambdaQueryWrapper<ApplicationInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationInfoEntity::getInfoKey, infoKey);
        ApplicationInfoEntity applicationInfoEntity = applicationInfoMapper.selectOne(queryWrapper);
        return AbstractApplicationInfoConverter.INSTANCE.toVO(applicationInfoEntity);
    }

    @Override
    public List<ApplicationInfoVO> getByApplicationIdList(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationInfoEntity::getApplicationId, applicationIdList);
        return applicationInfoMapper.selectList(queryWrapper).stream()
                .map(AbstractApplicationInfoConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationInfoVO> expire(Integer day) {
        List<ApplicationInfoEntity> applicationInfoEntityList =
                applicationInfoMapper.expire(ApplicationInfoKeyEnum.EXPIRE_TIME.name(), day);
        return applicationInfoEntityList.stream().map(AbstractApplicationInfoConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
