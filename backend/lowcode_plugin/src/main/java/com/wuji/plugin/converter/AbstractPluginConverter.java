package com.wuji.plugin.converter;

import com.wuji.plugin.model.entity.PluginEntity;
import com.wuji.plugin.model.request.PluginRequest;
import com.wuji.plugin.model.vo.PluginVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractPluginConverter {

    public static final AbstractPluginConverter INSTANCE = Mappers.getMapper(AbstractPluginConverter.class);

    public abstract PluginVO toVO(PluginEntity pluginEntity);

    public abstract PluginEntity toEntity(PluginRequest pluginRequest);
}
