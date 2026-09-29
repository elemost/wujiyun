package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.info.UserScope;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.JsonObjectUtils;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.UserUtils;
import com.wuji.plugin.model.info.Markdown;
import com.wuji.plugin.model.info.PluginParamMapping;
import com.wuji.plugin.model.request.PluginUseRequest;
import com.wuji.plugin.model.vo.PluginVO;
import com.wuji.plugin.service.PluginService;
import com.wuji.plugin.utils.FormMarkdownUtils;
import com.wuji.service.client.PluginClient;
import com.wuji.service.constant.Constants;
import com.wuji.service.context.DataStreamPluginContext;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.SystemDefaultFieldEnum;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormMessageMarkdown;
import com.wuji.service.model.info.plugin.DataStreamParamMapping;
import com.wuji.service.model.info.plugin.PluginCommonConfig;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamPluginNode;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.vo.FormDataStreamPluginVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormDataStreamPluginService;
import com.wuji.service.utils.MessageUtils;
import com.wuji.service.utils.MongoSearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FromDataStreamPluginServiceImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private DataStreamPluginContext dataStreamPluginContext;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private PluginClient pluginClient;

    @Autowired
    private UserService userService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private ThreadPoolExecutor dataStreamExecutor;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public String nodeType() {
        return "plugin";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamPluginNode dataStreamPluginNode = (DataStreamPluginNode) dataStreamCommon;
        PluginVO info = pluginService.info(dataStreamPluginNode.getPluginId());
        PluginCommonConfig pluginCommonConfig = new PluginCommonConfig();
        pluginCommonConfig.setSource("DATA_STREAM");
        FormDataStreamPluginService formDataStreamPluginService =
                dataStreamPluginContext.getHandler(info.getPluginType());
        FormDataStreamPluginVO execute = null;
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        List<PluginParamMapping> pluginMapping =
                getPluginMapping(nodeIdMap, dataStreamPluginNode.getParamMappings(), formDataStreamTrigger, info,
                        systemAllData);
        if (formDataStreamPluginService != null) {
            dataStreamExecutor.execute(() -> {
                formDataStreamPluginService.execute(pluginCommonConfig, formDataStreamTrigger, pluginMapping);
            });
        } else {
            PluginUseRequest pluginUseRequest =
                    new PluginUseRequest(dataStreamPluginNode.getPluginId(), pluginMapping, UserUtils.getUser());
            Object object = pluginClient.pluginUse(pluginUseRequest).getData();
            if (object != null) {
                String jsonString = JSONObject.toJSONString(object);
                execute = new FormDataStreamPluginVO();
                if (object instanceof JSONObject) {
                    execute.setMore(Boolean.FALSE);
                    execute.setReturnJson(JSONObject.parseObject(jsonString));
                } else {
                    execute.setMore(Boolean.TRUE);
                    execute.setReturnJsonList(JSONArray.parseArray(jsonString, JSONObject.class));
                }
            }
        }
        if (execute == null) {
            insertLog(formDataStreamTrigger, dataStreamCommon, null, null, null);
            super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            return;
        }
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamPluginNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setJsonValue(execute.getReturnJson());
        dataStreamCalculateVO.setJsonValueList(execute.getReturnJsonList());
        if (execute.getMore()) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("key", execute.getReturnJsonList());
            returnJson.put("size", execute.getReturnJsonList().size());
            dataStreamCalculateVO.setJsonValue(returnJson);
            dataStreamCalculateVO.setNodeType("more");
        }
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon, execute.getResult(), null, null);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    private List<PluginParamMapping> getPluginMapping(Map<Long, DataStreamCalculateVO> nodeIdMap,
                                                      List<DataStreamParamMapping> paramMappings,
                                                      FormDataStreamTrigger formDataStreamTrigger, PluginVO info,
                                                      SystemAllDataVO systemAllData) {
        List<PluginParamMapping> pluginParamMappings = new ArrayList<>();
        for (DataStreamParamMapping dataStreamParamMapping : paramMappings) {
            PluginParamMapping pluginParamMapping = new PluginParamMapping();
            pluginParamMapping.setFieldId(dataStreamParamMapping.getFieldId());
            pluginParamMapping.setFieldType(dataStreamParamMapping.getFieldType());
            Object value = null;
            Object resolverArg = dataStreamParamMapping.getResolverArg();
            if ("quoteField".equals(dataStreamParamMapping.getResolverType())) {
                DataStreamQuoteField quoteField =
                        JSONObject.parseObject(JSONObject.toJSONString(resolverArg), DataStreamQuoteField.class);
                List<Object> values = MongoSearchUtils.getJsonValueByQuote(nodeIdMap, quoteField);
                if (SystemDefaultFieldEnum.urlKeyList().contains(quoteField.getQuoteFieldId())) {
                    DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
                    value = getUrl(formDataStreamTrigger, info, dataStreamCalculateVO, quoteField);
                } else {
                    if ("user".equals(dataStreamParamMapping.getFieldType())) {
                        if (values != null) {
                            List<FormUser> formUserList =
                                    JSONArray.parseArray(JSONArray.toJSONString(values), FormUser.class);
                            value = formUserList.stream().map(FormUser::getAssigneeId).collect(Collectors.toList());
                        }
                    } else {
                        FormDataService formDataService = formDataContext.getHandler(quoteField.getQuoteFieldType());
                        if (formDataService != null) {
                            value = formDataService.transValue(values, quoteField.getQuoteFieldId(),
                                    quoteField.getQuoteFieldType(), systemAllData);
                        } else {
                            if (CollectionUtils.isNotEmpty(values) && values.get(0) != null) {
                                value = values.get(0).toString();
                            }
                        }
                    }
                }
            } else if ("group".equals(dataStreamParamMapping.getResolverType())) {
                Map<String, Object> context = new HashMap<>();
                JSONObject dataStreamQuoteFields = JSONObject.parseObject(JSONObject.toJSONString(resolverArg));
                dataStreamQuoteFields.forEach((key, val) -> {
                    DataStreamQuoteField dataStreamQuoteField =
                            JSONObject.parseObject(JSONObject.toJSONString(val), DataStreamQuoteField.class);
                    DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(dataStreamQuoteField.getNodeId());
                    if (SystemDefaultFieldEnum.urlKeyList().contains(dataStreamQuoteField.getQuoteFieldId())) {
                        String finalUrl =
                                getUrl(formDataStreamTrigger, info, dataStreamCalculateVO, dataStreamQuoteField);
                        context.put(key, finalUrl);
                    } else {
                        List<Object> values = MongoSearchUtils.getJsonValueByQuote(nodeIdMap, dataStreamQuoteField);
                        FormDataService formDataService =
                                formDataContext.getHandler(dataStreamQuoteField.getQuoteFieldType());
                        String content = null;
                        if (formDataService != null) {
                            content = formDataService.transValue(values, dataStreamQuoteField.getQuoteFieldId(),
                                    dataStreamQuoteField.getQuoteFieldType(), systemAllData);
                        } else {
                            if (CollectionUtils.isNotEmpty(values) && values.get(0) != null) {
                                content = values.get(0).toString();
                            }
                        }
                        context.put(key, content);
                    }
                });
                value = StringUtil.replaceValue(dataStreamParamMapping.getContent(), context);
            } else if ("markdown".equals(dataStreamParamMapping.getResolverType())) {
                List<FormMessageMarkdown> markdownList =
                        JSONObject.parseArray(JSONObject.toJSONString(resolverArg), FormMessageMarkdown.class);
                List<Markdown> markDown = MessageUtils.getMarkDown(markdownList, nodeIdMap);
                value = FormMarkdownUtils.markContent(markDown);
            } else if ("subForm".equals(dataStreamParamMapping.getResolverType())) {
                List<List<PluginParamMapping>> subFormList = new ArrayList<>();
                if ("subForm".equals(dataStreamParamMapping.getIteratorType())) {
                    List<DataStreamParamMapping> subFormConfigList =
                            JSONObject.parseArray(JSONObject.toJSONString(resolverArg), DataStreamParamMapping.class);
                    DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(dataStreamParamMapping.getNodeId());
                    JSONArray jsonArray = JsonObjectUtils.getJsonArray(dataStreamCalculateVO.getJsonValue(),
                            dataStreamParamMapping.getSubForm());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        List<PluginParamMapping> subFormIndexList = new ArrayList<>();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        for (DataStreamParamMapping subForm : subFormConfigList) {
                            PluginParamMapping subFormPlugin = new PluginParamMapping();
                            subFormPlugin.setFieldId(subForm.getFieldId());
                            subFormPlugin.setFieldType(subForm.getFieldType());
                            DataStreamQuoteField quoteField =
                                    JSONObject.parseObject(JSONObject.toJSONString(subForm.getResolverArg()),
                                            DataStreamQuoteField.class);
                            subFormPlugin.setValue(jsonObject.get(quoteField.getQuoteFieldId()));
                            subFormIndexList.add(subFormPlugin);
                        }
                        subFormList.add(subFormIndexList);
                    }
                }
                pluginParamMapping.setSubFormList(subFormList);
            } else {
                if ("user".equals(dataStreamParamMapping.getFieldType())) {
                    List<UserScope> userScopes =
                            JSONArray.parseArray(JSONArray.toJSONString(resolverArg), UserScope.class);
                    value = userService.getUserByScope(userScopes);
                } else {
                    value = resolverArg;
                }
            }
            pluginParamMapping.setValue(value);
            pluginParamMappings.add(pluginParamMapping);
            if (CollectionUtils.isNotEmpty(dataStreamParamMapping.getChildren())) {
                List<PluginParamMapping> pluginMapping =
                        getPluginMapping(nodeIdMap, dataStreamParamMapping.getChildren(), formDataStreamTrigger, info,
                                systemAllData);
                pluginParamMapping.setChildren(pluginMapping);
            }
        }
        return pluginParamMappings;
    }

    private String getUrl(FormDataStreamTrigger formDataStreamTrigger, PluginVO info,
                          DataStreamCalculateVO dataStreamCalculateVO, DataStreamQuoteField quoteField) {
        JSONObject jsonValue = dataStreamCalculateVO.getJsonValue();
        String uuid = jsonValue.getString("uuid");
        String finalUrl = SystemDefaultFieldEnum.getFinalUrl(formDataStreamTrigger.getApplicationId(),
                dataStreamCalculateVO.getFormId(), uuid, quoteField.getButtonId(), quoteField.getQuoteFieldId(), null);
        String domainName = ConfigCache.getValue(ConfigEnum.LOWCODE_PUBLIC_PUBLISH_URL.name());
        String lowcodeUrl = domainName + finalUrl;
        if (info.getPluginType().contains("DING_TALK")) {
            finalUrl = getDingLoginUrl(domainName, lowcodeUrl);
        }
        return finalUrl;
    }

    private String getDingLoginUrl(String domainName, String lowcodeUrl) {
        CompanyVO company = companyService.info(UserUtils.getUser().getCompanyId());
        DingTalkConfig dingTalkConfig = JSONObject.parseObject(company.getPullConfig(), DingTalkConfig.class);
        return Constants.getDingTalkUrl(domainName, dingTalkConfig, lowcodeUrl);
    }
}
