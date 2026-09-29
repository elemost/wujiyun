package com.wuji.service.service.plugin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.enums.SuiteApplicationEnum;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.context.MessageContext;
import com.wuji.message.model.request.SendMessageRequest;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.plugin.PluginCommonConfig;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FormDataStreamPluginVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormDataStreamPluginService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataStreamThirdWeComPluginImpl implements FormDataStreamPluginService {

    @Autowired
    private MessageContext messageContext;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public String pluginType() {
        return "WECOM_THIRD_APP_MESSAGE";
    }

    @Override
    public FormDataStreamPluginVO execute(PluginCommonConfig pluginCommonConfig,
                                          FormDataStreamTrigger formDataStreamTrigger,
                                          List<PluginParamMapping> pluginParamMappings) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        JSONObject jsonObject = new JSONObject();
        for (PluginParamMapping pluginParamMapping : pluginParamMappings) {
            jsonObject.put(pluginParamMapping.getFieldId(), pluginParamMapping.getValue());
        }
        Object object = jsonObject.get("users");
        if (object == null) {
            return null;
        }
        ApplicationVO applicationVO = applicationService.detail(formDataStreamTrigger.getApplicationId());
        String suitId = SuiteApplicationEnum.getSuiteIdByApp(applicationVO.getTemplateId());
        SendMessageRequest messageRequest = new SendMessageRequest();
        List<Long> formUserList = JSONArray.parseArray(JSONArray.toJSONString(object), Long.class);
        if (CollectionUtils.isEmpty(formUserList)) {
            return null;
        }
        messageRequest.setUserIdList(formUserList);
        messageRequest.setMessageType("markdown");
        messageRequest.setCompanyId(UserUtils.getUser().getCompanyId());
        messageRequest.setMessage(jsonObject.getString("markdowns"));
        messageRequest.setSuiteId(suitId);
        messageContext.getHandler(info.getDataSource()).sendMessage(messageRequest);
        return null;
    }
}
