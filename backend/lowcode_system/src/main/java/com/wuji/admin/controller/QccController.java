package com.wuji.admin.controller;

import com.wuji.admin.client.qcc.model.QccBasicDetailVO;
import com.wuji.admin.service.QccService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/qcc")
@Slf4j
public class QccController {

    @Autowired
    private QccService qccService;

    @ApiOperation("公司模糊搜索")
    @GetMapping("/qccCompany/{companyName}")
    public List<QccBasicDetailVO> qccCompany(@PathVariable String companyName) {
        try {
            return qccService.qccCompany(companyName);
        } catch (Exception e) {
            log.error("获取公司失败", e);
            return new ArrayList<>();
        }
    }
}
