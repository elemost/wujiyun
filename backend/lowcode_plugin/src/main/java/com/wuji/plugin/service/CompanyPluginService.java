package com.wuji.plugin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.plugin.model.entity.CompanyPluginEntity;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.request.CompanyPluginCreateRequest;
import com.wuji.plugin.model.request.CompanyPluginPageRequest;
import com.wuji.plugin.model.request.CompanyPluginUpdateRequest;
import com.wuji.plugin.model.vo.CompanyPluginVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-09-28
 */
public interface CompanyPluginService extends IService<CompanyPluginEntity> {
    void create(CompanyPluginCreateRequest companyPluginCreateRequest);

    void update(CompanyPluginUpdateRequest companyPluginUpdateRequest);

    void delete(String id);

    QueryPageVO<CompanyPluginVO> queryList(CompanyPluginPageRequest companyPluginPageRequest);

    CompanyPluginVO info(String id);

    List<CompanyPluginVO> selectList();

    Object usePlugin(String id, List<PluginParamMapping> pluginMapping);
}
