package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.TaskEntity;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.vo.TaskVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-11-28
 */
public interface TaskService extends IService<TaskEntity> {
    String createTask(TaskCreateRequest taskCreateRequest);

    void taskFinish(String taskId, String result);

    TaskVO info(String taskId);

    TaskVO infoByImport(String taskId);
}
