package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.model.request.FormRuleCreateRequest;
import com.wuji.service.model.request.FormRuleSortRequest;
import com.wuji.service.model.request.FormRuleUpdateRequest;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.service.FormRuleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-12-16
 */
@RestController
@RequestMapping("/form/rule")
public class FormRuleController {

    @Autowired
    private FormRuleService formRuleService;

    @ApiOperation("保存配置")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormRuleCreateRequest formRuleCreateRequest) {
        return Response.success(formRuleService.create(formRuleCreateRequest));
    }

    @ApiOperation("修改配置")
    @PostMapping("/update")
    public void update(@RequestBody FormRuleUpdateRequest formRuleUpdateRequest) {
        formRuleService.update(formRuleUpdateRequest);
    }

    @ApiOperation("修改配置")
    @PostMapping("/updateStatus")
    public void updateStatus(@RequestBody FormRuleUpdateRequest formRuleUpdateRequest) {
        formRuleService.updateStatus(formRuleUpdateRequest);
    }

    @ApiOperation("配置详情")
    @GetMapping("/info/{id}")
    public FormRuleVO info(@PathVariable String id, @RequestParam("formId") String formId,
                           @RequestParam("applicationId") String applicationId) {
        return formRuleService.info(id, applicationId, formId);
    }

    @ApiOperation("规则列表")
    @GetMapping("/queryList")
    public List<FormRuleVO> queryList(@RequestParam("formId") String formId,
                                      @RequestParam("applicationId") String applicationId,
                                      @RequestParam("ruleType") String ruleType) {
        return formRuleService.queryList(formId, applicationId, ruleType, null);
    }

    @ApiOperation("规则列表")
    @GetMapping("/queryAllList")
    public Map<String, List<FormRuleVO>> queryAllList(@RequestParam("formId") String formId,
                                                      @RequestParam("applicationId") String applicationId) {
        return formRuleService.queryAllList(applicationId, formId);
    }

    @ApiOperation("删除规则")
    @DeleteMapping("/delete")
    public void delete(@RequestParam("formId") String formId, @RequestParam("applicationId") String applicationId,
                       @RequestParam("id") String id) {
        formRuleService.delete(id, applicationId, formId);
    }


    @ApiOperation("排序")
    @PostMapping("/sort")
    public void sort(@RequestBody FormRuleSortRequest formRuleSortRequest) {
        formRuleService.sort(formRuleSortRequest);
    }

}
