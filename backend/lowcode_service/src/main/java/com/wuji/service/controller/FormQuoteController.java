package com.wuji.service.controller;

import com.wuji.service.model.request.FormQuoteSaveRequest;
import com.wuji.service.model.request.RelevanceRelationRequest;
import com.wuji.service.model.vo.FormQuoteVO;
import com.wuji.service.model.vo.RelevanceRelationVO;
import com.wuji.service.service.FormQuoteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-11-09
 */
@RestController
@RequestMapping("/form/quote")
public class FormQuoteController {
    @Autowired
    private FormQuoteService formQuoteService;


    @ApiOperation("保存")
    @PostMapping("/save")
    public void save(@RequestBody FormQuoteSaveRequest formQuoteSaveRequest) {
        formQuoteService.save(formQuoteSaveRequest);
    }

    @ApiOperation("表单引用信息")
    @GetMapping("/info/{formId}")
    public FormQuoteVO info(@PathVariable String formId, @RequestParam("applicationId") String applicationId) {
        return formQuoteService.getInfo(formId, applicationId);
    }

    @ApiOperation("表单引用信息")
    @PostMapping("/relevanceRelation")
    public List<RelevanceRelationVO> relevanceRelation(@RequestBody RelevanceRelationRequest relevanceRelationRequest) {
        return formQuoteService.relevanceRelation(relevanceRelationRequest);
    }


}
