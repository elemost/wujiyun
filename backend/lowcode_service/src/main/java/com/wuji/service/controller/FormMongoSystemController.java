package com.wuji.service.controller;

import com.wuji.service.model.request.FormMongoSyncUserRequest;
import com.wuji.service.model.vo.FormMongoUserFieldVO;
import com.wuji.service.service.FormMongoSystemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form/mongodb/system")
public class FormMongoSystemController {

    @Autowired
    private FormMongoSystemService formMongoSystemService;

    @ApiOperation("表单同步用户")
    @GetMapping("/userField")
    public List<FormMongoUserFieldVO> getUserField() {
        return formMongoSystemService.getUserField();
    }



    @ApiOperation("表单同步用户")
    @PostMapping("/syncUser")
    public void syncUser(@RequestBody FormMongoSyncUserRequest formMongoSyncUserRequest) {
        formMongoSystemService.syncUser(formMongoSyncUserRequest);
    }

}
