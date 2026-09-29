package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.common.privilege.annotation.ClientFunction;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.constant.ResourceCodeConstants;
import com.wuji.service.model.request.FormDataStreamCreateRequest;
import com.wuji.service.model.request.FormDataStreamListRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.FormDataStreamFromVO;
import com.wuji.service.model.vo.FormDataStreamVO;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.valid.FormDataStreamValidator;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-12-30
 */
@RestController
@RequestMapping("/form/data/stream")
public class FormDataStreamController {

    @Autowired
    private FormDataStreamService formDataStreamService;

    @ApiOperation("数据流新增")
    @PostMapping("/create")
    @ClientFunction(resourceCode = ResourceCodeConstants.DATA_STREAM, resourceValidator = FormDataStreamValidator.class,
            applicationLocation = IdentifierLocationEnum.BODY_FIELD)
    public Response<String> create(@RequestBody FormDataStreamCreateRequest formDataStreamCreateRequest) {
        return Response.success(formDataStreamService.create(formDataStreamCreateRequest));
    }

    @ApiOperation("数据流编辑")
    @PutMapping("/update")
    // @ClientFunction(resourceCode = ResourceCodeConstants.DATA_STREAM, resourceValidator = FormDataStreamValidator.class)
    public void update(@RequestBody FormDataStreamUpdateRequest formDataStreamUpdateRequest) {
        formDataStreamService.update(formDataStreamUpdateRequest);
    }

    @ApiOperation("数据流详情")
    @GetMapping("/info/{id}")
    public FormDataStreamVO update(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        return formDataStreamService.info(id, applicationId);
    }

    @ApiOperation("根据formId获取数据流")
    @PostMapping("/queryList")
    public List<FormDataStreamVO> getByFormId(@RequestBody FormDataStreamListRequest formDataStreamListRequest) {
        return formDataStreamService.queryList(formDataStreamListRequest);
    }

    @ApiOperation("根据formId获取数据流")
    @PostMapping("/formList")
    public List<FormDataStreamFromVO> formList(@RequestBody FormDataStreamListRequest formDataStreamListRequest) {
        return formDataStreamService.formList(formDataStreamListRequest);
    }


    @ApiOperation("数据流发布")
    @PutMapping("/publish")
    public void publish(@RequestBody FormDataStreamUpdateRequest formDataStreamUpdateRequest) {
        formDataStreamService.publish(formDataStreamUpdateRequest);
    }

    @ApiOperation("根据formId获取数据流")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        formDataStreamService.delete(id, applicationId);
    }

    @ApiOperation("打开关闭智能助手")
    @GetMapping("/openOrClose/{id}")
    public void openOrClose(@PathVariable String id, @RequestParam("applicationId") String applicationId,
                            @RequestParam("enable") Boolean enable) {
        formDataStreamService.openOrClose(id, applicationId, enable);
    }


}
