package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.FormModuleInsertRequest;
import com.wuji.service.model.request.FormModuleUpdateRequest;
import com.wuji.service.model.request.InstrumentPanelViewListRequest;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormModuleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 表单组件表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-11-20
 */
@RestController
@RequestMapping("/form/module")
public class FormModuleController {
    @Autowired
    private FormModuleService formModuleService;

    @ApiOperation("新增模型组件")
    @PostMapping("/insert")
    public Response<String> insert(@RequestBody FormModuleInsertRequest formModuleInsertRequest) {
        return Response.success(formModuleService.insert(formModuleInsertRequest));
    }

    @ApiOperation("修改模型组件")
    @PutMapping("/update")
    public void update(@RequestBody FormModuleUpdateRequest formModuleUpdateRequest) {
        formModuleService.update(formModuleUpdateRequest);
    }

    @ApiOperation("模型组件列表")
    @GetMapping("/formModelInfo/{formId}/{applicationId}")
    public List<FormModuleVO> getByFormId(@PathVariable String formId, @PathVariable String applicationId) {
        return formModuleService.getByFormId(formId, applicationId);
    }

    @ApiOperation("模型组件详情")
    @GetMapping("/info/{id}")
    public FormModuleVO getById(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                                @RequestParam("formId") String formId) {
        return formModuleService.info(id, applicationId, formId);
    }

    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public QueryPageVO<LowcodeDataVO> queryList(
            @RequestBody InstrumentPanelViewListRequest instrumentPanelViewListRequest) {
        return formModuleService.queryList(instrumentPanelViewListRequest);
    }

}
