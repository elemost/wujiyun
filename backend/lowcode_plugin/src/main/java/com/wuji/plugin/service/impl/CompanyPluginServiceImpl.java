package com.wuji.plugin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.plugin.context.HttpResultContext;
import com.wuji.plugin.context.PluginContext;
import com.wuji.plugin.converter.AbstractCompanyPluginConverter;
import com.wuji.plugin.mapper.CompanyPluginMapper;
import com.wuji.plugin.model.entity.CompanyPluginEntity;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.request.CompanyPluginCreateRequest;
import com.wuji.plugin.model.request.CompanyPluginPageRequest;
import com.wuji.plugin.model.request.CompanyPluginUpdateRequest;
import com.wuji.plugin.model.vo.CompanyPluginVO;
import com.wuji.plugin.service.CompanyPluginService;
import com.wuji.plugin.service.HttpResultService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-09-28
 */
@Service
public class CompanyPluginServiceImpl extends ServiceImpl<CompanyPluginMapper, CompanyPluginEntity>
        implements CompanyPluginService {

    @Autowired
    private CompanyPluginMapper companyPluginMapper;

    @Autowired
    private HttpResultContext httpResultContext;

    @Autowired
    private PluginContext pluginContext;



    @Override
    public void create(CompanyPluginCreateRequest companyPluginCreateRequest) {
        UserDomain user = UserUtils.getUser();
        CompanyPluginEntity companyPluginEntity =
                AbstractCompanyPluginConverter.INSTANCE.toEntity(companyPluginCreateRequest);
        companyPluginEntity.setCompanyId(user.getCompanyId());
        companyPluginEntity.setCreator(user.getNickName());
        companyPluginEntity.setModifier(user.getNickName());
        companyPluginEntity.setId(ObjectId.getGuid());
        companyPluginMapper.insert(companyPluginEntity);
    }

    @Override
    public void update(CompanyPluginUpdateRequest companyPluginUpdateRequest) {
        UserDomain user = UserUtils.getUser();
        CompanyPluginEntity companyPluginEntity =
                AbstractCompanyPluginConverter.INSTANCE.toEntity(companyPluginUpdateRequest);
        companyPluginEntity.setModifier(user.getNickName());
        companyPluginMapper.updateById(companyPluginEntity);
    }

    @Override
    public void delete(String id) {
        UserDomain user = UserUtils.getUser();
        CompanyPluginEntity companyPluginEntity = new CompanyPluginEntity();
        companyPluginEntity.setDeleted(Boolean.TRUE);
        companyPluginEntity.setId(id);
        companyPluginEntity.setModifier(user.getNickName());
        companyPluginMapper.updateById(companyPluginEntity);
    }

    @Override
    public QueryPageVO<CompanyPluginVO> queryList(CompanyPluginPageRequest companyPluginPageRequest) {
        LambdaQueryWrapper<CompanyPluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(companyPluginPageRequest.getPluginName()),
                CompanyPluginEntity::getPluginName, companyPluginPageRequest.getPluginName());
        queryWrapper.eq(CompanyPluginEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(CompanyPluginEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        Page<CompanyPluginEntity> companyPluginEntityPage = companyPluginMapper.selectPage(
                new Page<>(companyPluginPageRequest.getPageNum(), companyPluginPageRequest.getPageSize()),
                queryWrapper);
        List<CompanyPluginVO> companyPluginVOS = new ArrayList<>();
        for (CompanyPluginEntity companyPluginEntity : companyPluginEntityPage.getRecords()) {
            CompanyPluginVO companyPluginVO = AbstractCompanyPluginConverter.INSTANCE.toVO(companyPluginEntity);
            companyPluginVOS.add(companyPluginVO);
        }
        return PageUtils.toQueryPage(companyPluginEntityPage, companyPluginVOS);
    }

    @Override
    public CompanyPluginVO info(String id) {
        LambdaQueryWrapper<CompanyPluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPluginEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(CompanyPluginEntity::getId, id);
        CompanyPluginEntity companyPluginEntity = companyPluginMapper.selectOne(queryWrapper);
        return AbstractCompanyPluginConverter.INSTANCE.toVO(companyPluginEntity);
    }

    @Override
    public List<CompanyPluginVO> selectList() {
        LambdaQueryWrapper<CompanyPluginEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPluginEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(CompanyPluginEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<CompanyPluginVO> companyPluginVOS = new ArrayList<>();
        List<CompanyPluginEntity> companyPluginEntities = companyPluginMapper.selectList(queryWrapper);
        for (CompanyPluginEntity companyPluginEntity : companyPluginEntities) {
            CompanyPluginVO companyPluginVO = AbstractCompanyPluginConverter.INSTANCE.toVO(companyPluginEntity);
            companyPluginVOS.add(companyPluginVO);
        }
        return companyPluginVOS;
    }

    @Override
    public Object usePlugin(String id, List<PluginParamMapping> pluginMapping) {
        JSONObject jsonObject = new JSONObject();
        for (PluginParamMapping pluginParamMapping : pluginMapping) {
            HttpResultService handler = httpResultContext.getHandler(pluginParamMapping.getFieldType());
            if (handler == null) {
                jsonObject.put(pluginParamMapping.getFieldId(), pluginParamMapping.getValue());
            } else {
                handler.buildRequest(jsonObject, pluginParamMapping);
            }
        }
        CompanyPluginVO companyPluginVO = info(id);
        return pluginContext.getHandler(companyPluginVO.getPluginType()).execute(jsonObject);
    }

}
