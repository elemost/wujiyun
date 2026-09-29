package com.wuji.service.controller;

import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.model.request.FormExtraFunctionCreateRequest;
import com.wuji.service.model.request.FormExtraFunctionSortRequest;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.service.FormExtraFunctionService;
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

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-12-23
 */
@RestController
@RequestMapping("/form/extra/function")
public class FormExtraFunctionController {


    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @ApiOperation("通过表单id获取数据")
    @GetMapping("/getByFormId/{applicationId}/{formId}")
    public List<FormExtraFunctionVO> getByFormId(@PathVariable String formId, @PathVariable String applicationId,
                                                 @RequestParam(value = "functionType", defaultValue = "CUSTOM_BUTTON")
                                                 String functionType) {
        return formExtraFunctionServiceImpl.getByFormId(formId, applicationId, functionType);
    }

    @ApiOperation("通过id获取数据")
    @GetMapping("/info/{id}")
    public FormExtraFunctionVO info(@PathVariable String id) {
        return formExtraFunctionServiceImpl.info(id);
    }

    @ApiOperation("通过id删除数据")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        formExtraFunctionServiceImpl.delete(id);
    }

    @ApiOperation("排序")
    @PostMapping("/sort")
    public void sort(@RequestBody FormExtraFunctionSortRequest formExtraFunctionSortRequest) {
        formExtraFunctionSortRequest.setFunctionType(FormExtraFunctionTypeEnum.CUSTOM_BUTTON.name());
        formExtraFunctionServiceImpl.sort(formExtraFunctionSortRequest);
    }

    @ApiOperation("保存")
    @PostMapping("/save")
    public void save(@RequestBody FormExtraFunctionCreateRequest formExtraFunctionCreateRequest) {
        formExtraFunctionServiceImpl.save(formExtraFunctionCreateRequest);
    }
}
