package com.wuji.service.converter;

import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.InstrumentPanelViewListRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractInstrumentPanelConverter {
    public static final AbstractInstrumentPanelConverter INSTANCE =
            Mappers.getMapper(AbstractInstrumentPanelConverter.class);


    public abstract FormSearchDataRequest toRequest(InstrumentPanelViewListRequest instrumentPanelViewListRequest);
}
