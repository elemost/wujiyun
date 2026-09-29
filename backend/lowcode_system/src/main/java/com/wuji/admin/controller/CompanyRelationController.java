package com.wuji.admin.controller;

import com.wuji.admin.model.request.ContactPersonSaveRequest;
import com.wuji.admin.model.vo.ContactPersonVO;
import com.wuji.admin.service.CompanyRelationService;
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
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/company/relation")
public class CompanyRelationController {

    @Autowired
    private CompanyRelationService companyRelationService;

    @ApiOperation("对接人保存")
    @PostMapping("/saveContactPerson")
    public void saveContactPerson(@RequestBody ContactPersonSaveRequest contactPersonSaveRequest) {
        companyRelationService.saveContactPerson(contactPersonSaveRequest);
    }

    @ApiOperation("获取对接人")
    @GetMapping("/getContactPerson/{companyId}")
    public ContactPersonVO getContactPerson(@PathVariable Long companyId) {
        return companyRelationService.getContactPerson(companyId);
    }

}
