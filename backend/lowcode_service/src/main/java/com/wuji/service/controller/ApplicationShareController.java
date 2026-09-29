package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.ApplicationShareCreateRequest;
import com.wuji.service.model.vo.ApplicationShareVO;
import com.wuji.service.service.ApplicationShareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/application/share")
public class ApplicationShareController {

    @Autowired
    private ApplicationShareService applicationShareService;

    @ApiOperation("生成分享id")
    @PostMapping("/create")
    public Response<String> createShare(@RequestBody ApplicationShareCreateRequest applicationShareCreateRequest) {
        return Response.success(applicationShareService.createShare(applicationShareCreateRequest));
    }

    @ApiOperation("获取分享信息")
    @GetMapping("/detail/{shareId}")
    public ApplicationShareVO detail(@PathVariable String shareId) {
        return applicationShareService.info(shareId);
    }

}
