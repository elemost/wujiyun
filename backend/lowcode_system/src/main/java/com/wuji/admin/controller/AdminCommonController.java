package com.wuji.admin.controller;

import com.wuji.admin.service.AdminCommonService;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/common")
public class AdminCommonController {

    @Autowired
    private AdminCommonService commonService;

    @GetMapping("/system/user")
    public List<FormUser> getCurrent() {
        return commonService.getSystemUser();
    }

    @GetMapping("/system/dept")
    public List<FormDept> getSystemDept() {
        return commonService.getSystemDept();
    }
}
