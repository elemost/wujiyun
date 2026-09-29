package com.wuji.plugin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.plugin.converter.AbstractPluginConverter;
import com.wuji.plugin.mapper.PluginMapper;
import com.wuji.plugin.model.entity.PluginEntity;
import com.wuji.plugin.model.request.PluginListRequest;
import com.wuji.plugin.model.request.PluginRequest;
import com.wuji.plugin.model.vo.PluginVO;
import com.wuji.plugin.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 插件表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-07-10
 */
@Service
public class PluginServiceImpl extends ServiceImpl<PluginMapper, PluginEntity> implements PluginService {

    @Autowired
    private PluginMapper pluginMapper;

    @Override
    public String create(PluginRequest pluginRequest) {
        PluginEntity pluginEntity = AbstractPluginConverter.INSTANCE.toEntity(pluginRequest);
        pluginEntity.setId(ObjectId.getGuid());
        pluginEntity.setCreator(UserUtils.getUser().getNickName());
        pluginEntity.setModifier(UserUtils.getUser().getNickName());
        pluginMapper.insert(pluginEntity);
        return pluginEntity.getId();
    }

    @Override
    public void update(PluginRequest pluginRequest) {
        PluginEntity pluginEntity = AbstractPluginConverter.INSTANCE.toEntity(pluginRequest);
        pluginEntity.setModifier(UserUtils.getUser().getNickName());
        pluginMapper.updateById(pluginEntity);
    }

    @Override
    public PluginVO info(String id) {
        PluginEntity pluginEntity = pluginMapper.selectById(id);
        return AbstractPluginConverter.INSTANCE.toVO(pluginEntity);
    }

    @Override
    public QueryPageVO<PluginVO> queryList(PluginListRequest pluginListRequest) {
        LambdaQueryWrapper<PluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        IPage<PluginEntity> pluginEntityPage =
                pluginMapper.selectPage(new Page<>(pluginListRequest.getPageNum(), pluginListRequest.getPageSize()),
                        queryWrapper);
        List<PluginEntity> records = pluginEntityPage.getRecords();
        List<PluginVO> pluginList = new ArrayList<>();
        for (PluginEntity pluginEntity : records) {
            PluginVO pluginVO = AbstractPluginConverter.INSTANCE.toVO(pluginEntity);
            pluginList.add(pluginVO);
        }
        return PageUtils.toQueryPage(pluginEntityPage, pluginList);
    }

    @Override
    public List<PluginVO> selectList() {
        LambdaQueryWrapper<PluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PluginEntity::getDeleted, Boolean.FALSE);
        List<PluginEntity> pluginEntities = pluginMapper.selectList(queryWrapper);
        return pluginEntities.stream().map(AbstractPluginConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        LambdaQueryWrapper<PluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PluginEntity::getId, id);
        PluginEntity pluginEntity = new PluginEntity();
        pluginEntity.setDeleted(Boolean.TRUE);
        pluginEntity.setModifier(UserUtils.getUser().getNickName());
        pluginMapper.update(pluginEntity, queryWrapper);
    }

    @Override
    public List<PluginVO> getDefaultInstall() {
        LambdaQueryWrapper<PluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PluginEntity::getDefaultInstall, Boolean.TRUE);
        List<PluginEntity> pluginEntities = pluginMapper.selectList(queryWrapper);
        return pluginEntities.stream().map(AbstractPluginConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
