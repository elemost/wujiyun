package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractDictDataConverter;
import com.wuji.admin.mapper.DictDataMapper;
import com.wuji.admin.model.entity.DictDataEntity;
import com.wuji.admin.model.vo.DictDataVO;
import com.wuji.admin.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@Service
@DS("slave")
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictDataEntity> implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Override
    public List<DictDataVO> getByType(String type) {
        LambdaQueryWrapper<DictDataEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDataEntity::getDictType, type);
        List<DictDataEntity> dictDataEntities = dictDataMapper.selectList(queryWrapper);
        return dictDataEntities.stream().map(AbstractDictDataConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
