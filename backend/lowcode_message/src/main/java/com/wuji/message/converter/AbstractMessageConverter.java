package com.wuji.message.converter;

import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.message.model.domain.MessageDomain;
import com.wuji.message.model.entity.MessageEntity;
import com.wuji.message.model.request.MessageCreateRequest;
import com.wuji.message.model.vo.MessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractMessageConverter {
    public static final AbstractMessageConverter INSTANCE = Mappers.getMapper(AbstractMessageConverter.class);

    public abstract MessageEntity toEntity(MessageInsertRequest messageInsertRequest);

    public abstract MessageVO toVO(MessageDomain messageDomain);

    public abstract MessageEntity toEntity(MessageCreateRequest messageCreateRequest);
}
