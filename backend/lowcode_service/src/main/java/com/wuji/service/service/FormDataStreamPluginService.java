package com.wuji.service.service;

import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.plugin.PluginCommonConfig;
import com.wuji.service.model.vo.FormDataStreamPluginVO;

import java.util.List;

public interface FormDataStreamPluginService {
    String pluginType();

    FormDataStreamPluginVO execute(PluginCommonConfig pluginCommonConfig, FormDataStreamTrigger formDataStreamTrigger,
                                   List<PluginParamMapping> pluginParamMappings);
}
