package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractItemConverter;
import com.wuji.admin.mapper.ItemMapper;
import com.wuji.admin.model.entity.ItemEntity;
import com.wuji.admin.model.vo.ItemVO;
import com.wuji.admin.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 五极产品 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@Service
@DS("slave")
public class ItemServiceImpl extends ServiceImpl<ItemMapper, ItemEntity> implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public ItemVO info(String itemCode) {
        LambdaQueryWrapper<ItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemEntity::getItemCode, itemCode);
        return AbstractItemConverter.INSTANCE.toVO(itemMapper.selectOne(queryWrapper));
    }

    @Override
    public List<ItemVO> itemList() {
        LambdaQueryWrapper<ItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ItemEntity::getItemCode, "lowcode");
        List<ItemEntity> itemEntities = itemMapper.selectList(queryWrapper);
        return itemEntities.stream().map(AbstractItemConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
