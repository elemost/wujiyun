package com.wuji.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wuji.common.api.SaleOrderApi;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.ConfigVO;
import com.wuji.common.model.vo.OrdersSuccessVO;
import com.wuji.common.service.ConfigService;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.plugin.context.PluginContext;
import com.wuji.plugin.model.info.Markdown;
import com.wuji.plugin.model.info.config.WeComRobotConfig;
import com.wuji.plugin.utils.FormMarkdownUtils;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.model.info.FormMessageMarkdown;
import com.wuji.service.model.vo.ApplicationInfoVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationInfoService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.TemplateApplicationService;
import com.wuji.service.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SaleOrderApiImpl implements SaleOrderApi {

    @Autowired
    private TemplateApplicationService templateApplicationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private PluginContext pluginContext;


    @Override
    public void generateTemplate(String templateApplicationId, Integer day, UserDomain userDomain) {
        UserUtils.setUser(userDomain);
        ApplicationVO applicationVO = applicationService.getByTemplateId(templateApplicationId);
        String applicationId = null;
        if (applicationVO == null) {
            applicationId = templateApplicationService.generateTemplate(templateApplicationId);
        } else {
            applicationId = applicationVO.getId();
        }
        ApplicationInfoVO applicationInfoVO =
                applicationInfoService.getByApplicationAndKey(applicationId, ApplicationInfoKeyEnum.EXPIRE_TIME.name());
        if (applicationInfoVO != null) {
            long time = Long.parseLong(applicationInfoVO.getInfoValue()) + day * 24 * 60 * 60 * 1000;
            applicationInfoService.saveByKey(applicationId, ApplicationInfoKeyEnum.EXPIRE_TIME.name(),
                    String.valueOf(time));
        } else {
            applicationInfoService.saveByKey(applicationId, ApplicationInfoKeyEnum.EXPIRE_TIME.name(),
                    String.valueOf(TimeUtils.getDataZero(new Date(), day).getTime()));
        }
        UserUtils.clearUser();
    }

    @Override
    public void sendWeCom(OrdersSuccessVO ordersSuccessVO) {
        ConfigVO configVO = configService.detailByKey(ConfigEnum.LOWCODE_WECOM_ORDER_SUCCESS.name());
        ConfigVO urlVO = configService.detailByKey(ConfigEnum.LOWCODE_WECOM_ORDER_SUCCESS_URL.name());
        List<FormMessageMarkdown> markdownList =
                JSONArray.parseArray(configVO.getConfigValue(), FormMessageMarkdown.class);
        List<Markdown> configList = MessageUtils.getMarkDown(markdownList, JSON.parseObject(JSON.toJSONString(ordersSuccessVO)));
        WeComRobotConfig weComRobotConfig = new WeComRobotConfig();
        weComRobotConfig.setMarkdowns(FormMarkdownUtils.markContent(configList));
        weComRobotConfig.setUrl(urlVO.getConfigValue());
        pluginContext.getHandler("WECOM_ROBOT").execute(weComRobotConfig);
    }
}
