package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractClientFunctionConverter;
import com.wuji.admin.mapper.ClientFunctionMapper;
import com.wuji.admin.model.entity.ClientFunctionEntity;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.service.ClientFunctionService;
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
 * @since 2025-03-19
 */
@Service
@DS("slave")
public class ClientFunctionServiceImpl extends ServiceImpl<ClientFunctionMapper, ClientFunctionEntity>
        implements ClientFunctionService {

    @Autowired
    private ClientFunctionMapper clientFunctionMapper;

    @Override
    public List<ClientFunctionVO> getByClientId(String clientId, List<String> equityIdList) {
        if (CollectionUtils.isEmpty(equityIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ClientFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientFunctionEntity::getClientId, clientId);
        queryWrapper.in(ClientFunctionEntity::getEquityId, equityIdList);
        List<ClientFunctionEntity> clientFunctionEntities = clientFunctionMapper.selectList(queryWrapper);
        return clientFunctionEntities.stream().map(AbstractClientFunctionConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientFunctionVO> getExistLimit(String clientId, String equityId) {
        LambdaQueryWrapper<ClientFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientFunctionEntity::getClientId, clientId);
        queryWrapper.eq(ClientFunctionEntity::getEquityId, equityId);
        queryWrapper.isNotNull(ClientFunctionEntity::getLimitCount);
        List<ClientFunctionEntity> clientFunctionEntities = clientFunctionMapper.selectList(queryWrapper);
        return clientFunctionEntities.stream().map(AbstractClientFunctionConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
