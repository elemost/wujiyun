package com.wuji.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.platform.converter.AbstractDataApiConfigConverter;
import com.wuji.platform.mapper.DataApiConfigMapper;
import com.wuji.platform.model.entity.DataApiConfigEntity;
import com.wuji.platform.model.request.DataApiConfigCreateRequest;
import com.wuji.platform.model.request.DataApiConfigRequest;
import com.wuji.platform.model.request.DataApiConfigUpdateRequest;
import com.wuji.platform.model.vo.DataApiConfigVO;
import com.wuji.platform.service.DataApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
@Service
public class DataApiConfigServiceImpl extends ServiceImpl<DataApiConfigMapper, DataApiConfigEntity>
        implements DataApiConfigService {

    @Autowired
    private DataApiConfigMapper dataApiConfigMapper;


    @Override
    public String create(DataApiConfigCreateRequest dataApiConfigCreateRequest) {
        DataApiConfigEntity dataApiConfigEntity =
                AbstractDataApiConfigConverter.INSTANCE.toEntity(dataApiConfigCreateRequest);
        dataApiConfigEntity.setCreator(UserUtils.getUser().getNickName());
        dataApiConfigEntity.setModifier(UserUtils.getUser().getNickName());
        dataApiConfigEntity.setId(ObjectId.getGuid());
        dataApiConfigEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        dataApiConfigMapper.insert(dataApiConfigEntity);
        return dataApiConfigEntity.getId();
    }

    @Override
    public void update(DataApiConfigUpdateRequest dataApiConfigUpdateRequest) {
        DataApiConfigEntity dataApiConfigEntity =
                AbstractDataApiConfigConverter.INSTANCE.toEntity(dataApiConfigUpdateRequest);
        dataApiConfigEntity.setModifier(UserUtils.getUser().getNickName());
        dataApiConfigMapper.updateById(dataApiConfigEntity);
    }

    @Override
    public DataApiConfigVO info(String id) {
        DataApiConfigEntity dataApiConfigEntity = dataApiConfigMapper.selectById(id);
        return AbstractDataApiConfigConverter.INSTANCE.toVO(dataApiConfigEntity);
    }

    @Override
    public List<DataApiConfigVO> queryList(DataApiConfigRequest dataApiConfigRequest) {
        LambdaQueryWrapper<DataApiConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataApiConfigEntity::getApplicationId, dataApiConfigRequest.getApplicationId());
        queryWrapper.eq(DataApiConfigEntity::getDeleted, Boolean.FALSE);
        return dataApiConfigMapper.selectList(queryWrapper).stream().map(AbstractDataApiConfigConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        DataApiConfigEntity dataApiConfigEntity = new DataApiConfigEntity();
        dataApiConfigEntity.setId(id);
        dataApiConfigEntity.setDeleted(Boolean.TRUE);
        dataApiConfigEntity.setModifier(UserUtils.getUser().getNickName());
        dataApiConfigMapper.updateById(dataApiConfigEntity);
    }
}
