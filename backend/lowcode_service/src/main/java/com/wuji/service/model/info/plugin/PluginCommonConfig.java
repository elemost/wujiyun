package com.wuji.service.model.info.plugin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wuji.service.enums.DataStreamPluginTypeEnum;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "pluginType", visible = true,
        include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = WeComAppMessagePlugin.class, name = "WE_COM_APP"),
        @JsonSubTypes.Type(value = WeComAppMessagePlugin.class, name = "WE_COM_THIRD_APP"),
        @JsonSubTypes.Type(value = InMailPlugin.class, name = "IN_MAIL"),
        @JsonSubTypes.Type(value = SyncDataPlugin.class, name = "SYNC_DATA"),
        @JsonSubTypes.Type(value = HttpPlugin.class, name = "HTTP"),
})

public class PluginCommonConfig {
    /**
     * @see DataStreamPluginTypeEnum
     */
    private String pluginType;

    private String source;
}
