package com.wuji.plugin.converter;

import com.wuji.plugin.model.entity.CompanyPluginEntity;
import com.wuji.plugin.model.request.CompanyPluginCreateRequest;
import com.wuji.plugin.model.request.CompanyPluginUpdateRequest;
import com.wuji.plugin.model.vo.CompanyPluginVO;
import com.wuji.plugin.model.vo.PluginVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractCompanyPluginConverter {

    public static final AbstractCompanyPluginConverter INSTANCE =
            Mappers.getMapper(AbstractCompanyPluginConverter.class);

    public abstract CompanyPluginEntity toEntity(CompanyPluginCreateRequest companyPluginCreateRequest);

    public abstract CompanyPluginEntity toEntity(CompanyPluginUpdateRequest companyPluginUpdateRequest);

    public abstract CompanyPluginVO toVO(CompanyPluginEntity companyPluginEntity);

    public abstract CompanyPluginEntity toEntity(PluginVO pluginVO);

}
