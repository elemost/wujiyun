package com.wuji.service.controller;

import com.wuji.service.model.vo.TaskVO;
import com.wuji.service.service.TaskService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-11-28
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation("任务id")
    @GetMapping("/info/{taskId}")
    public TaskVO info(@PathVariable String taskId) {
        return taskService.info(taskId);
    }

    @ApiOperation("任务id")
    @GetMapping("/infoByImport/{taskId}")
    public TaskVO infoByImport(@PathVariable String taskId) {
        return taskService.infoByImport(taskId);
    }

}
