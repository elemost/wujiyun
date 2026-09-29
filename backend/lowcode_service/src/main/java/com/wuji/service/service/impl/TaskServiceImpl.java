package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.TaskProgressHolder;
import com.wuji.service.converter.AbstractTaskConverter;
import com.wuji.service.enums.TaskStatusEnum;
import com.wuji.service.mapper.TaskMapper;
import com.wuji.service.model.entity.TaskEntity;
import com.wuji.service.model.info.ImportProgress;
import com.wuji.service.model.request.TaskCreateRequest;
import com.wuji.service.model.vo.TaskVO;
import com.wuji.service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-11-28
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public String createTask(TaskCreateRequest taskCreateRequest) {
        TaskEntity task = AbstractTaskConverter.INSTANCE.toEntity(taskCreateRequest);
        task.setId(ObjectId.getGuid());
        task.setCreator(UserUtils.getUser().getNickName());
        task.setStatus(TaskStatusEnum.RUNNING.name());
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void taskFinish(String taskId, String result) {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setResult(result);
        task.setStatus(TaskStatusEnum.FINISH.name());
        taskMapper.updateById(task);
    }

    @Override
    public TaskVO info(String taskId) {
        TaskEntity task = taskMapper.selectById(taskId);
        return AbstractTaskConverter.INSTANCE.toVO(task);
    }

    @Override
    public TaskVO infoByImport(String taskId) {
        TaskEntity task = taskMapper.selectById(taskId);
        TaskVO taskVO = AbstractTaskConverter.INSTANCE.toVO(task);
        if (!TaskStatusEnum.FINISH.name().equals(task.getStatus())) {
            ImportProgress progress = TaskProgressHolder.getProgress(taskId);
            taskVO.setResult(JSONObject.toJSONString(progress));
        }
        return taskVO;
    }
}
