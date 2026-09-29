package com.wuji.service.converter;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.request.FormMongoDbSummaryRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormViewCalendarRequest;
import com.wuji.service.model.request.FormViewFieldGroupRequest;
import com.wuji.service.model.request.FormViewLevelRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.systemapi.client.user.model.LowcodeDataOpenDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public abstract class AbstractFormMongoDbConverter {
    public static final AbstractFormMongoDbConverter INSTANCE = Mappers.getMapper(AbstractFormMongoDbConverter.class);

    @Mapping(source = "createTime", target = "createTime", qualifiedByName = "timestampToDateTime")
    @Mapping(source = "modifyTime", target = "modifyTime", qualifiedByName = "timestampToDateTime")
    @Mapping(source = "modifier", target = "modifier", qualifiedByName = "buildName")
    @Mapping(source = "creator", target = "creator", qualifiedByName = "buildName")
    public abstract LowcodeDataVO toVO(LowcodeDataDomain lowcodeDataDomain);

    public abstract FormSearchDataRequest toRequest(FormViewMongoDbRequest formViewMongoDbRequest);

    public abstract FormSearchDataRequest toRequest(FormViewCalendarRequest formViewCalendarRequest);

    public abstract FormSearchDataRequest toRequest(FormViewFieldGroupRequest formViewFieldGroupRequest);

    public abstract FormSearchDataRequest toRequest(FormViewLevelRequest formViewLevelRequest);

    @Mapping(source = "name", target = "fieldId")
    @Mapping(source = "type", target = "fieldType")
    public abstract MongoSort toMongoSort(MongodbSearchField formFieldGroupRequest);

    @Named("timestampToDateTime")
    Date timestampToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }

    @Named("buildName")
    String buildName(Object formUserObject) {
        if (formUserObject == null) {
            return null;
        }
        if (formUserObject instanceof String) {
            return formUserObject.toString();
        }

        FormUser formUser = JSONObject.parseObject(JSONObject.toJSONString(formUserObject), FormUser.class);
        return formUser.getAssigneeId().toString();
    }

    public abstract FormSearchDataRequest toRequest(FormMongoDbSummaryRequest formMongoDbSummaryRequest);


    public abstract LowcodeDataDomain toDomain(LowcodeDataOpenDomain lowcodeDataOpenDomain);
}
