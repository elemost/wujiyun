package com.wuji.plugin.controller;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.plugin.model.request.CompanyPluginCreateRequest;
import com.wuji.plugin.model.request.CompanyPluginPageRequest;
import com.wuji.plugin.model.request.CompanyPluginUpdateRequest;
import com.wuji.plugin.model.vo.CompanyPluginVO;
import com.wuji.plugin.service.CompanyPluginService;
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
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-09-28
 */
@RestController
@RequestMapping("/company/plugin")
public class CompanyPluginController {

    @Autowired
    private CompanyPluginService companyPluginService;

    @ApiOperation("保存插件")
    @PostMapping("/create")
    public void create(@RequestBody CompanyPluginCreateRequest companyPluginCreateRequest) {
        companyPluginService.create(companyPluginCreateRequest);
    }

    @ApiOperation("修改插件")
    @PostMapping("/update")
    public void update(@RequestBody CompanyPluginUpdateRequest companyPluginUpdateRequest) {
        companyPluginService.update(companyPluginUpdateRequest);
    }

    @ApiOperation("删除插件")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        companyPluginService.delete(id);
    }

    @ApiOperation("插件列表")
    @PostMapping("/queryList")
    public QueryPageVO<CompanyPluginVO> queryList(@RequestBody CompanyPluginPageRequest companyPluginPageRequest) {
        return companyPluginService.queryList(companyPluginPageRequest);
    }

    @ApiOperation("插件列表")
    @GetMapping("/selectList")
    public List<CompanyPluginVO> selectList() {
        return companyPluginService.selectList();
    }

    @ApiOperation("插件详情")
    @GetMapping("/info/{id}")
    public CompanyPluginVO info(@PathVariable String id) {
        return companyPluginService.info(id);
    }

}
