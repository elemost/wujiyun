package com.wuji.admin.controller;

import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.model.request.CorpCoopUserRequest;
import com.wuji.admin.model.vo.CorpCoopVO;
import com.wuji.admin.model.vo.UserImportVO;
import com.wuji.admin.service.CorpCoopUserService;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/corp/coop")
public class CorpCoopController {

    @Autowired
    private CorpCoopUserService corpCoopUserService;

    @ApiOperation("获取外部公司")
    @GetMapping("/getCorpCompany")
    public List<CorpCoopVO> getCorpCompany() {
        return corpCoopUserService.getCorpCompany();
    }

    @ApiOperation("创建外部用户")
    @PostMapping("/create")
    public void createCorpCoopUser(@RequestBody CorpCoopUserRequest corpCoopUserRequest) {
        corpCoopUserService.createCorpCoopUser(corpCoopUserRequest);
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
    }

    @ApiOperation("删除外部用户")
    @PostMapping("/delete/{userId}")
    public void delete(@PathVariable String userId) {
        corpCoopUserService.delete(userId);
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
    }

    @ApiOperation("导入用户")
    @PostMapping("/import")
    public UserImportVO multipartFile(MultipartFile multipartFile) {
        UserImportVO userImportVO = corpCoopUserService.importCorp(multipartFile);
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
        return userImportVO;
    }

    @ApiOperation("模板")
    @PostMapping("/template")
    public void template(HttpServletResponse response) {
        corpCoopUserService.template(response);
    }

}
