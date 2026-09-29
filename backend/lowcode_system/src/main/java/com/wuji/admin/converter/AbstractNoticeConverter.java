package com.wuji.admin.converter;

import com.wuji.admin.model.entity.NoticeEntity;
import com.wuji.admin.model.vo.NoticeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractNoticeConverter {

    public static final AbstractNoticeConverter INSTANCE = Mappers.getMapper(AbstractNoticeConverter.class);

    public abstract NoticeVO toVO(NoticeEntity noticeEntity);
}
