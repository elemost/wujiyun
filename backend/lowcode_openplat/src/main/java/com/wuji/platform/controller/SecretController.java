package com.wuji.platform.controller;


import com.wuji.platform.model.request.SecretGenerateRequest;
import com.wuji.platform.model.request.SecretRemarkRequest;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.platform.service.SecretService;
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
 * @since 2025-08-06
 */
@RestController
@RequestMapping("/secret")
public class SecretController {

    @Autowired
    private SecretService secretService;

    @ApiOperation("生成密钥")
    @PostMapping("/generate")
    public void generate(@RequestBody SecretGenerateRequest secretGenerateRequest) {
        secretService.generate(secretGenerateRequest);
    }

    @ApiOperation("删除密钥")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        secretService.delete(id);
    }

    @ApiOperation("修改状态")
    @PutMapping("/updateState/{id}")
    public void updateState(@PathVariable String id, @RequestParam("state") String state) {
        secretService.updateState(id, state);
    }

    @ApiOperation("备注")
    @PutMapping("/remark")
    public void remark(@RequestBody SecretRemarkRequest secretRemarkRequest) {
        secretService.remark(secretRemarkRequest);
    }

    @ApiOperation("当前公司密钥")
    @GetMapping("/currentCompanyList")
    public List<SecretVO> currentCompanyList() {
        return secretService.currentCompanyList();
    }

}
