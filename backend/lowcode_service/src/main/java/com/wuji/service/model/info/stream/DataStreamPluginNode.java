package com.wuji.service.model.info.stream;

import com.wuji.service.model.info.plugin.DataStreamParamMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamPluginNode extends DataStreamCommon {

    private String pluginId;

    private Map<String, Object> mappingValue;

    private List<DataStreamParamMapping> paramMappings = new ArrayList<>();
}
