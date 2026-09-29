package com.wuji.message.controller;

import com.wuji.message.service.MessageUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-04-30
 */
@RestController
@RequestMapping("/message/user")
public class MessageUserController {
    @Autowired
    private MessageUserService messageUserService;

    @ApiOperation("view")
    @PutMapping("/view/{messageId}")
    public void view(@PathVariable Long messageId) {
        messageUserService.view(messageId);
    }
}
