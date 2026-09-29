package com.wuji.service.converter;

import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.info.plugin.HttpPluginRequestMapping;
import com.wuji.service.model.info.stream.DataStreamCalculate;
import com.wuji.service.model.info.stream.DataStreamCalculateNode;
import com.wuji.service.model.info.stream.DataStreamFieldTrans;
import com.wuji.service.model.request.FormDataStreamCreateRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import com.wuji.service.model.vo.FormDataStreamVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormDataStreamConverter {
    public static final AbstractFormDataStreamConverter INSTANCE =
            Mappers.getMapper(AbstractFormDataStreamConverter.class);

    public abstract FormDataStreamEntity toEntity(FormDataStreamCreateRequest formDataStreamCreateRequest);

    public abstract FormDataStreamEntity toEntity(FormDataStreamUpdateRequest formDataStreamUpdateRequest);

    public abstract FormDataStreamVO toVO(FormDataStreamEntity formDataStreamEntity);

    public abstract FormDataStreamEntity toEntity(TemplateFormDataStreamVO templateFormDataStreamVO);

    public abstract DataStreamFieldTrans toTrans(HttpPluginRequestMapping httpPluginRequestMapping);

    public abstract DataStreamCalculateNode toNode(DataStreamCalculate calculate);

    public abstract FormDataStreamEntity toEntity(FormDataStreamPublishVO formDataStreamPublishVO);

}
