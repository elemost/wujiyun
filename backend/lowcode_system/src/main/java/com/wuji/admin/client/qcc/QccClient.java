package com.wuji.admin.client.qcc;

import com.wuji.admin.client.qcc.model.FuzzySearchVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "qccClient", url = "${qcc.server:http://jisuqygsxx.market.alicloudapi.com}",
        configuration = QccFeignConfiguration.class)
public interface QccClient {

    @GetMapping("/enterprise/search")
    QccResult<FuzzySearchVO> fuzzySearch(@RequestParam("keyword") String keyword);

}
