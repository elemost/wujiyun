package com.wuji.admin.converter;

import com.wuji.admin.model.entity.SsoLoginConfigEntity;
import com.wuji.admin.model.request.SsoLoginConfigSaveRequest;
import com.wuji.admin.model.vo.SsoLoginConfigInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractSsoLoginConverter {

    public static final AbstractSsoLoginConverter INSTANCE = Mappers.getMapper(AbstractSsoLoginConverter.class);

    public abstract SsoLoginConfigEntity convert(SsoLoginConfigSaveRequest ssoLoginConfigSaveRequest);

    public abstract SsoLoginConfigInfoVO convert(SsoLoginConfigEntity ssoLoginConfigEntity);
}
