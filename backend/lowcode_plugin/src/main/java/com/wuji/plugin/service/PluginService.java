package com.wuji.plugin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.plugin.model.entity.PluginEntity;
import com.wuji.plugin.model.request.PluginListRequest;
import com.wuji.plugin.model.request.PluginRequest;
import com.wuji.plugin.model.vo.PluginVO;

import java.util.List;

/**
 * <p>
 * 插件表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-07-10
 */
public interface PluginService extends IService<PluginEntity> {

    String create(PluginRequest pluginRequest);

    void update(PluginRequest pluginRequest);

    PluginVO info(String id);

    QueryPageVO<PluginVO> queryList(PluginListRequest pluginListRequest);

    List<PluginVO> selectList();

    void delete(String id);

    List<PluginVO> getDefaultInstall();
}
