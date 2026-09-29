package com.wuji.factory.controller;

import com.wuji.factory.model.request.DataFactoryRequest;
import com.wuji.factory.model.vo.DataFactoryDistinctFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageFieldVO;
import com.wuji.factory.service.FormDataFactoryExecuteService;
import com.wuji.service.model.request.factory.DataFactoryVO;
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

@RestController
@RequestMapping("/form/data/factory/execute")
public class FormDataFactoryExecuteController {

    @Autowired
    private FormDataFactoryExecuteService formDataFactoryExecuteService;

    @ApiOperation("关联查询")
    @PostMapping("/lookUp")
    public DataFactoryVO lookUp(@RequestBody DataFactoryRequest dataFactoryRequest) {
        return formDataFactoryExecuteService.lookUpAndAggregate(dataFactoryRequest);
    }

    @ApiOperation("关联查询")
    @PostMapping("/lookUp/distinctField")
    public DataFactoryDistinctFieldVO distinctField(@RequestBody DataFactoryRequest dataFactoryRequest) {
        return formDataFactoryExecuteService.distinctField(dataFactoryRequest);
    }

    @ApiOperation("关联查询")
    @PostMapping("/stageField")
    public List<DataFactoryStageFieldVO> getStageField(@RequestBody DataFactoryRequest dataFactoryRequest) {
        return formDataFactoryExecuteService.stageField(dataFactoryRequest);
    }

    @ApiOperation("关联查询")
    @GetMapping("/trigger/{id}")
    public void trigger(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
         formDataFactoryExecuteService.trigger(id, applicationId);
    }

}
