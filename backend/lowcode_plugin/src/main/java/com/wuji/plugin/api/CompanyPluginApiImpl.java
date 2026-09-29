package com.wuji.plugin.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.common.api.CompanyPluginApi;
import com.wuji.plugin.converter.AbstractCompanyPluginConverter;
import com.wuji.plugin.mapper.CompanyPluginMapper;
import com.wuji.plugin.model.entity.CompanyPluginEntity;
import com.wuji.plugin.model.vo.PluginVO;
import com.wuji.plugin.service.CompanyPluginService;
import com.wuji.plugin.service.PluginService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyPluginApiImpl implements CompanyPluginApi {

    @Autowired
    private CompanyPluginMapper companyPluginMapper;

    @Autowired
    private CompanyPluginService companyPluginService;

    @Autowired
    private PluginService pluginService;

    @Override
    public void init(Long companyId) {
        LambdaQueryWrapper<CompanyPluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPluginEntity::getCompanyId, companyId);
        List<CompanyPluginEntity> companyPluginEntities = companyPluginMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(companyPluginEntities)) {
            return;
        }
        List<PluginVO> defaultInstall = pluginService.getDefaultInstall();
        if (CollectionUtils.isEmpty(defaultInstall)) {
            return;
        }
        List<CompanyPluginEntity> companyPluginEntityList = new ArrayList<>();
        for (PluginVO pluginVO : defaultInstall) {
            CompanyPluginEntity companyPluginEntity = AbstractCompanyPluginConverter.INSTANCE.toEntity(pluginVO);
            companyPluginEntity.setCompanyId(companyId);
            companyPluginEntityList.add(companyPluginEntity);
        }
        companyPluginService.saveBatch(companyPluginEntityList);
    }
}
