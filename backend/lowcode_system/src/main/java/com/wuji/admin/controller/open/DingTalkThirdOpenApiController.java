package com.wuji.admin.controller.open;

import com.wuji.admin.service.DingTalkThirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/api/third/ding/talk")
@Slf4j
public class DingTalkThirdOpenApiController {
    @Autowired
    private DingTalkThirdService dingTalkThirdService;
}
