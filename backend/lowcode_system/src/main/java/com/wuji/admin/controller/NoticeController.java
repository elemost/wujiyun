package com.wuji.admin.controller;

import com.wuji.admin.model.request.NoticeRequest;
import com.wuji.admin.model.vo.NoticeVO;
import com.wuji.admin.service.NoticeService;
import com.wuji.common.model.vo.QueryPageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 通知公告表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("Notice列表")
    @PostMapping("/queryList")
    public QueryPageVO<NoticeVO> queryList(@RequestBody NoticeRequest noticeRequest) {
        return noticeService.queryList(noticeRequest);
    }
}
