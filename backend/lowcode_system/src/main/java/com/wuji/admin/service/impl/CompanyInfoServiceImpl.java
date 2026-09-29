package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractCompanyInfoConverter;
import com.wuji.admin.mapper.CompanyInfoMapper;
import com.wuji.admin.model.entity.CompanyInfoEntity;
import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyInfoVO;
import com.wuji.admin.service.ClientFunctionService;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-07
 */
@Service
@DS("slave")
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoMapper, CompanyInfoEntity>
        implements CompanyInfoService {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;

    @Autowired
    private ClientFunctionService clientFunctionService;


    @Override
    public void save(Long companyId, CompanyInfoSaveRequest companyInfoSaveRequest) {
        LambdaQueryWrapper<CompanyInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyInfoEntity::getCompanyId, companyId);
        queryWrapper.eq(CompanyInfoEntity::getConfigKey, companyInfoSaveRequest.getConfigKey());
        CompanyInfoEntity companyInfoEntity = companyInfoMapper.selectOne(queryWrapper);
        if (companyInfoEntity != null) {
            companyInfoEntity.setConfigValue(companyInfoSaveRequest.getConfigValue());
            companyInfoEntity.setUpdateBy(UserUtils.getUser().getNickName());
            companyInfoEntity.setUpdateTime(new Date());
            companyInfoMapper.updateById(companyInfoEntity);
        } else {
            companyInfoEntity = AbstractCompanyInfoConverter.INSTANCE.toEntity(companyInfoSaveRequest);
            if (companyId != null) {
                companyInfoEntity.setCompanyId(companyId);
            }
            companyInfoEntity.setUpdateBy(UserUtils.getUser().getNickName());
            companyInfoEntity.setCreateBy(UserUtils.getUser().getNickName());
            companyInfoEntity.setCreateTime(new Date());
            companyInfoEntity.setUpdateTime(new Date());
            companyInfoMapper.insert(companyInfoEntity);
        }
    }

    @Override
    public void save(List<CompanyInfoSaveRequest> companyInfoSaveRequestList, List<String> keyList) {
        if (CollectionUtils.isEmpty(companyInfoSaveRequestList)) {
            return;
        }
        List<Long> companyIdList = companyInfoSaveRequestList.stream().map(CompanyInfoSaveRequest::getCompanyId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<CompanyInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CompanyInfoEntity::getCompanyId, companyIdList);
        queryWrapper.in(CompanyInfoEntity::getConfigKey, keyList);
        companyInfoMapper.delete(queryWrapper);
        if (CollectionUtils.isNotEmpty(companyInfoSaveRequestList)) {
            List<CompanyInfoEntity> companyInfoEntities =
                    companyInfoSaveRequestList.stream().map(AbstractCompanyInfoConverter.INSTANCE::toEntity)
                            .collect(Collectors.toList());
            saveBatch(companyInfoEntities);
        }
    }

    @Override
    public List<CompanyInfoVO> getByCompanyId(Long companyId) {
        LambdaQueryWrapper<CompanyInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyInfoEntity::getCompanyId, companyId);
        queryWrapper.isNotNull(CompanyInfoEntity::getConfigKey);
        List<CompanyInfoEntity> companyInfoEntities = companyInfoMapper.selectList(queryWrapper);
        return companyInfoEntities.stream().map(AbstractCompanyInfoConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyInfoVO getByKey(Long companyId, String key) {
        LambdaQueryWrapper<CompanyInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyInfoEntity::getCompanyId, companyId);
        queryWrapper.eq(CompanyInfoEntity::getConfigKey, key);
        CompanyInfoEntity companyInfoEntity = companyInfoMapper.selectOne(queryWrapper);
        return AbstractCompanyInfoConverter.INSTANCE.toVO(companyInfoEntity);
    }

    @Override
    public CompanyInfoVO getByKeyAndValue(String key, String value) {
        LambdaQueryWrapper<CompanyInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyInfoEntity::getConfigValue, value);
        queryWrapper.eq(CompanyInfoEntity::getConfigKey, key);
        CompanyInfoEntity companyInfoEntity = companyInfoMapper.selectOne(queryWrapper);
        return AbstractCompanyInfoConverter.INSTANCE.toVO(companyInfoEntity);
    }

    @Override
    public void saveDefaultKey(Long companyId, String clientId, String equityId) {
        List<ClientFunctionVO> existLimit = clientFunctionService.getExistLimit(clientId, equityId);
        List<CompanyInfoEntity> companyInfoEntities = new ArrayList<>();
        for (ClientFunctionVO clientFunctionVO : existLimit) {
            CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
            companyInfoEntity.setCompanyId(companyId);
            companyInfoEntity.setConfigKey(clientFunctionVO.getPermissionKey());
            companyInfoEntity.setConfigValue(clientFunctionVO.getLimitCount().toString());
            companyInfoEntities.add(companyInfoEntity);
        }
        saveBatch(companyInfoEntities);
    }

    @Override
    public void saveCompanyInfoWhileOrder(Map<String, Integer> buyMap, String clientId, String equityId,
                                          Long companyId) {
        List<ClientFunctionVO> clientFunctionEntityList = clientFunctionService.getExistLimit(clientId, equityId);
        List<CompanyInfoEntity> companyInfoEntities = new ArrayList<>();
        for (ClientFunctionVO clientFunction : clientFunctionEntityList) {
            CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
            companyInfoEntity.setCompanyId(companyId);
            companyInfoEntity.setConfigKey(clientFunction.getPermissionKey());
            Integer count = buyMap.get(clientFunction.getPermissionKey());
            if (count == null) {
                companyInfoEntity.setConfigValue(clientFunction.getLimitCount().toString());
            } else {
                companyInfoEntity.setConfigValue(count.toString());
            }
            companyInfoEntities.add(companyInfoEntity);
        }
        LambdaQueryWrapper<CompanyInfoEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(CompanyInfoEntity::getCompanyId, companyId);
        deleteWrapper.in(CompanyInfoEntity::getConfigKey,
                clientFunctionEntityList.stream().map(ClientFunctionVO::getEquityId)
                        .collect(Collectors.toList()));
        companyInfoMapper.delete(deleteWrapper);
        saveBatch(companyInfoEntities);
    }
}
