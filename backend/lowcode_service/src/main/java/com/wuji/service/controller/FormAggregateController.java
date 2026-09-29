package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.model.request.FormAggregateCreateRequest;
import com.wuji.service.model.request.FormAggregateDataRequest;
import com.wuji.service.model.request.FormAggregateListRequest;
import com.wuji.service.model.request.FormAggregateUpdateRequest;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.valid.AggregateTableValidator;
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

/**
 * <p>
 * 聚合表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-10
 */
@RestController
@RequestMapping("/form/aggregate")
public class FormAggregateController {
    @Autowired
    private FormAggregateService formAggregateService;


    @ApiOperation("新增")
    @PostMapping("/create")
    @ClientFunction(resourceCode = ResourceCodeConstants.AGGREGATE_TABLE,
            resourceValidator = AggregateTableValidator.class, applicationLocation = IdentifierLocationEnum.BODY_FIELD)
    public Response<String> create(@RequestBody FormAggregateCreateRequest formAggregateCreateRequest) {
        return Response.success(formAggregateService.create(formAggregateCreateRequest));
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public void update(@RequestBody FormAggregateUpdateRequest formAggregateUpdateRequest) {
        formAggregateService.update(formAggregateUpdateRequest);
    }

    @ApiOperation("聚合表列表")
    @PostMapping("/queryPage")
    public QueryPageVO<FormAggregateVO> queryPage(@RequestBody FormAggregateListRequest formAggregateListRequest) {
        return formAggregateService.queryPage(formAggregateListRequest);
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id,
                       @RequestParam(value = "applicationId", required = false) String applicationId) {
        formAggregateService.delete(id, applicationId);
    }


    @ApiOperation("聚合表详情")
    @GetMapping("/info/{id}")
    public FormAggregateVO detail(@PathVariable String id,
                                  @RequestParam(value = "applicationId", required = false) String applicationId) {
        return formAggregateService.info(id, applicationId);
    }

    @PostMapping("/getDataById/{id}")
    public Object getDataById(@PathVariable String id, @RequestBody FormAggregateDataRequest formAggregateDataRequest) {
        return formAggregateService.getDataById(id, formAggregateDataRequest);
    }

}
