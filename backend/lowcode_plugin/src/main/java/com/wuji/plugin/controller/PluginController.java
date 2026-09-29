package com.wuji.plugin.controller;

import com.wuji.common.model.Response;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.plugin.model.request.PluginListRequest;
import com.wuji.plugin.model.request.PluginRequest;
import com.wuji.plugin.model.vo.PluginVO;
import com.wuji.plugin.service.PluginService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 插件表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private PluginService pluginService;

    @ApiOperation("创建插件")
    @PostMapping("/create")
    public Response<String> create(@RequestBody PluginRequest pluginRequest) {
        return Response.success(pluginService.create(pluginRequest));
    }

    @ApiOperation("修改插件")
    @PostMapping("/update")
    public void update(@RequestBody PluginRequest pluginRequest) {
        pluginService.update(pluginRequest);
    }

    @ApiOperation("插件详情")
    @GetMapping("/info/{id}")
    public PluginVO info(@PathVariable String id) {
        return pluginService.info(id);
    }

    @ApiOperation("插件列表")
    @PostMapping("/queryList")
    public QueryPageVO<PluginVO> queryList(@RequestBody PluginListRequest pluginRequest) {
        return pluginService.queryList(pluginRequest);
    }

    @ApiOperation("插件列表")
    @GetMapping("/selectList")
    public List<PluginVO> selectList() {
        return pluginService.selectList();
    }

    @ApiOperation("插件删除")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        pluginService.delete(id);
    }

}
