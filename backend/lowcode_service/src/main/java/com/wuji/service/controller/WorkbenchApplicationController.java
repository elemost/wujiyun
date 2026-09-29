package com.wuji.service.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.model.request.ApplicationQueryRequest;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workbench/application")
public class WorkbenchApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @ApiOperation("工作台应用列表")
    @PostMapping("/queryList")
    @CorpCoopResource(location = IdentifierLocationEnum.BODY_FIELD)
    public QueryPageVO<ApplicationVO> queryList(@RequestBody ApplicationQueryRequest applicationQueryRequest) {
        return applicationService.getApplicationListPrivilege(applicationQueryRequest);
    }

    @ApiOperation("最近使用")
    @GetMapping("/latestUseApplication")
    public List<ApplicationVO> latestUseApplication(@RequestParam Integer limitCount) {
        return applicationService.getLatestApplicationPrivilege(limitCount);
    }
}
