package com.wuji.service.service.plugin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.GuidUtils;
import com.wuji.common.utils.HttpUtils;
import com.wuji.common.utils.SHA1;
import com.wuji.common.utils.UserUtils;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.platform.model.vo.SyncMappingVO;
import com.wuji.platform.service.SecretService;
import com.wuji.platform.service.SyncMappingService;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.DataStreamPluginTypeEnum;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.plugin.PluginCommonConfig;
import com.wuji.service.model.info.plugin.SyncDataPlugin;
import com.wuji.service.model.request.FormSendDataRequest;
import com.wuji.service.model.vo.FormDataStreamPluginVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamPluginService;
import com.wuji.service.service.FormMongoDbService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataStreamSyncDataPluginImpl implements FormDataStreamPluginService {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private UserService userService;

    @Autowired
    private SyncMappingService syncMappingService;

    @Autowired
    private SecretService secretService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public String pluginType() {
        return DataStreamPluginTypeEnum.SYNC_DATA.name();
    }

    @Override
    public FormDataStreamPluginVO execute(PluginCommonConfig pluginCommonConfig,
                                          FormDataStreamTrigger formDataStreamTrigger,
                                          List<PluginParamMapping> pluginParamMappings) {
        FormDataStreamPluginVO formDataStreamPluginVO = new FormDataStreamPluginVO();
        JSONObject mappingValueMap = new JSONObject();
        for (PluginParamMapping pluginParamMapping : pluginParamMappings) {
            mappingValueMap.put(pluginParamMapping.getFieldId(), pluginParamMapping.getValue());
        }
        SyncDataPlugin syncDataPlugin =
                JSONObject.parseObject(JSONObject.toJSONString(mappingValueMap), SyncDataPlugin.class);
        DataStreamTriggerLogDomain dataStreamTrigger = formDataStreamTrigger.getDataStreamTrigger();
        SyncMappingVO syncMappingVO =
                syncMappingService.info(dataStreamTrigger.getApplicationId(), dataStreamTrigger.getFormId());
        if (syncMappingVO == null) {
            formDataStreamPluginVO.setResult("触发节点参数管理未配置");
            return formDataStreamPluginVO;
        }
        SecretVO secret = secretService.getSecret(syncDataPlugin.getAppKey());
        if (secret == null) {
            formDataStreamPluginVO.setResult("密钥不存在");
            return formDataStreamPluginVO;
        }
        if ("CLOSE".equals(secret.getState())) {
            formDataStreamPluginVO.setResult("密钥已被停用");
            return formDataStreamPluginVO;
        }
        List<FormExtraFunctionSync> syncList =
                JSONArray.parseArray(syncMappingVO.getMappingConfig(), FormExtraFunctionSync.class);
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        LowcodeDataDomain info =
                formMongoDbService.info(dataStreamTrigger.getTitle().getUuid(), dataStreamTrigger.getFormId(),
                        dataStreamTrigger.getApplicationId());
        JSONObject instValue = info.getInstValue();
        JSONObject sendJson = new JSONObject();
        for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
            if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                continue;
            }
            FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
            if (formDataService != null) {
                formDataService.dealWhileSend(formExtraFunctionSync, instValue,  systemAllData, sendJson);
            } else {
                sendJson.put(formExtraFunctionSync.getMappingField(), instValue.get(formExtraFunctionSync.getName()));
            }
        }
        FormSendDataRequest formSendDataRequest = new FormSendDataRequest();
        String encrypt = AESUtils.encrypt(secret.getAppSecret(), JSONObject.toJSONString(sendJson));
        formSendDataRequest.setDataJson(sendJson);
        String randNum = GuidUtils.getRandNum();
        formSendDataRequest.setNonce(randNum);
        String sha1 = SHA1.getSHA1(secret.getAppSecret(), String.valueOf(new Date().getTime() / 1000),
                formSendDataRequest.getNonce(), encrypt);
        formSendDataRequest.setMsgSignature(sha1);
        formSendDataRequest.setProcessInstanceId(info.getProcessInstanceId());
        formSendDataRequest.setAction(dataStreamTrigger.getAction());
        formSendDataRequest.setAppKey(secret.getAppKey());
        formSendDataRequest.setUuid(info.getUuid());
        UserVO user = userService.info(UserUtils.getUser().getUserIdLongValue());
        formSendDataRequest.setCreator(user.getPhonenumber());
        String result = HttpUtils.post(syncDataPlugin.getUrl(), formSendDataRequest);
        formDataStreamPluginVO.setResult(result);
        return new FormDataStreamPluginVO();
    }
}
