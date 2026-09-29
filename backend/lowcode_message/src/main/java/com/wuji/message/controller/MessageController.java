package com.wuji.message.controller;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.message.model.request.MessageAllViewRequest;
import com.wuji.message.model.request.MessageCreateRequest;
import com.wuji.message.model.request.MessageListRequest;
import com.wuji.message.model.vo.MessageVO;
import com.wuji.message.service.MessageService;
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

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @ApiOperation("站内信")
    @PostMapping("")
    public QueryPageVO<MessageVO> queryPage(@RequestBody MessageListRequest messageListRequest) {
        return messageService.queryPage(messageListRequest);
    }

    @ApiOperation("站内信")
    @GetMapping("/info/{id}")
    public MessageVO info(@PathVariable String id) {
        return messageService.info(id);
    }


    @GetMapping("/notView")
    public Map<String, Integer> notView() {
        return messageService.notView();
    }

    @ApiOperation("新增")
    @PostMapping("/insert")
    public void insert(@RequestBody MessageCreateRequest messageCreateRequest) {
        messageService.insert(messageCreateRequest);
    }

    @ApiOperation("删除")
    @DeleteMapping("/delete")
    public void delete(@RequestParam Long messageId) {
        messageService.delete(messageId);
    }

    @ApiOperation("查看全部")
    @PostMapping("/allView")
    public void allView(@RequestBody MessageAllViewRequest messageAllViewRequest) {
        messageService.allView(messageAllViewRequest.getSource());
    }


}
