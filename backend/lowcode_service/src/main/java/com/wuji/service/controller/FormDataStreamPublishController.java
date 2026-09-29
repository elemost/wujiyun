package com.wuji.service.controller;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.request.FormDataStreamButtonTriggerRequest;
import com.wuji.service.service.FormDataStreamPublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/form/data/stream/publish")
public class FormDataStreamPublishController {

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @ApiOperation("")
    @PostMapping("/trigger/{formId}")
    public void trigger(@PathVariable String formId, @RequestParam("applicationId") String applicationId,
                        @RequestBody JSONObject jsonObject) {
        LowcodeDataDomain lowcodeDataDomain = new LowcodeDataDomain();
        lowcodeDataDomain.setInstValue(jsonObject);
        lowcodeDataDomain.setCreator(FormUser.getCurrent(UserUtils.getUser()));
        formDataStreamPublishService.trigger(formId, "update", lowcodeDataDomain, applicationId,
                new FormDataStreamTrigger());
    }

    @ApiOperation("按钮触发")
    @PostMapping("/buttonTrigger")
    public void buttonTrigger(@RequestBody FormDataStreamButtonTriggerRequest formDataStreamButtonTriggerRequest) {
        formDataStreamPublishService.buttonTrigger(formDataStreamButtonTriggerRequest);
    }

}
