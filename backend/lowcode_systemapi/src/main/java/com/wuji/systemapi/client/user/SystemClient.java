package com.wuji.systemapi.client.user;


import com.wuji.systemapi.client.user.model.FormDataRequest;
import com.wuji.systemapi.client.user.model.LowcodeDataOpenDomain;
import com.wuji.systemapi.client.user.model.SyncCompanyInfoRequest;
import com.wuji.systemapi.client.user.model.TemplateApplicationOpenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "systemClient", url = "${lowcode.system}", configuration = SystemFeignConfiguration.class)
public interface SystemClient {

    @PostMapping("/sync/company/syncCompanyInfo")
    SystemResult<String> syncCompanyInfo(@RequestBody SyncCompanyInfoRequest syncCompanyInfoRequest);

    @PostMapping("/company/app/getCurrent")
    SystemResult<String> getCurrent();

    @GetMapping("/template/application/detail/{templateId}")
    SystemResult<String> getTemplateInfo(@PathVariable String templateId);

    @PostMapping("/form/mongo/db/queryApplicationData/{applicationId}")
    SystemResult<List<LowcodeDataOpenDomain>> getTemplateInfo(@RequestBody List<FormDataRequest> formDataRequests,
                                                              @PathVariable String applicationId);

    @GetMapping("/template/application/info/{templateId}")
    SystemResult<String> templateInfo(@PathVariable String templateId);


    @PostMapping("/template/application/queryList")
    SystemResult<String> templateList(@RequestBody TemplateApplicationOpenRequest templateApplicationRequest);
}
