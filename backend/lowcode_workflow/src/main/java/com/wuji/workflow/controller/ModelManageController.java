package com.wuji.workflow.controller;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.workflow.model.request.ModelListRequest;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.ModelManageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/model/manage")
public class ModelManageController {
    @Autowired
    private ModelManageService modelManageService;

    @ApiOperation("创建模型")
    @PostMapping("/createModel")
    public void createModel(@RequestBody ModelRequest modelRequest) {
        modelManageService.createModel(modelRequest);
    }

    @ApiOperation("修改模型")
    @PutMapping("/updateModel")
    public void updateModel(@RequestBody ModelRequest modelRequest) {
        modelManageService.updateModel(modelRequest);
    }

    @ApiOperation("删除模型")
    @DeleteMapping("/removeModel/{modelIds}")
    public void removeModel(@PathVariable String[] modelIds) {
        modelManageService.removeModel(modelIds[0]);
    }


    @ApiOperation("发布模型")
    @PostMapping("/publish/{modelId}")
    public void publish(@PathVariable String modelId) {
        modelManageService.publish(modelId);
    }

    @ApiOperation("模型列表")
    @PostMapping("/list")
    public QueryPageVO<ModelVO> getModelList(@RequestBody ModelListRequest modelListRequest) {
        return modelManageService.getModelList(modelListRequest);
    }

    @ApiOperation("模型列表")
    @GetMapping("/getModelByKey/{modelKey}")
    public List<ModelVO> getModelByKey(@PathVariable String modelKey) {
        return modelManageService.getModelByKey(modelKey);
    }
}
