package com.wuji.service.controller;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.FormDataStreamLogRequest;
import com.wuji.service.model.vo.DataStreamTriggerLogStageVO;
import com.wuji.service.model.vo.DataStreamTriggerLogVO;
import com.wuji.service.service.FormDataStreamLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form/data/stream/log")
public class FormDataStreamLogController {

    @Autowired
    private FormDataStreamLogService formDataStreamLogService;

    @ApiOperation("智能助手运行日志")
    @PostMapping("/queryList")
    public QueryPageVO<DataStreamTriggerLogVO> queryList(
            @RequestBody FormDataStreamLogRequest formDataStreamLogRequest) {
        return formDataStreamLogService.queryList(formDataStreamLogRequest);
    }


    @ApiOperation("智能助手日志步骤")
    @GetMapping("/getStageByLogUuid/{uuid}")
    public List<DataStreamTriggerLogStageVO> getStageByLogUuid(@PathVariable String uuid) {
        return formDataStreamLogService.getStageByLogUuid(uuid);
    }

    @ApiOperation("智能助手日志重试")
    @GetMapping("/tryAgain/{uuid}")
    public void tryAgain(@PathVariable String uuid) {
        formDataStreamLogService.tryAgain(uuid);
    }
}
