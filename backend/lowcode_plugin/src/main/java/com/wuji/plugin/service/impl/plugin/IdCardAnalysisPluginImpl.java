package com.wuji.plugin.service.impl.plugin;

import com.alibaba.fastjson.JSONObject;
import com.wuji.plugin.service.PluginUseService;
import com.wuji.plugin.utils.IdCardAnalysisUtils;
import com.wuji.plugin.utils.OcrUtils;
import org.springframework.stereotype.Service;

@Service
public class IdCardAnalysisPluginImpl implements PluginUseService {
    @Override
    public String pluginType() {
        return "ID_CARD_ANALYSIS";
    }

    @Override
    public Object execute(Object object) {
        JSONObject ocrResult = OcrUtils.getOcrResult(object);
        String content = ocrResult.getString("content").replaceAll(" ", "");
        return IdCardAnalysisUtils.parseIdCard(content);
    }
}
