package com.wuji.workflow.config;


import com.wuji.workflow.listeners.global.HistoricActivityInstanceEndListener;
import com.wuji.workflow.listeners.global.ProcessEndListener;
import com.wuji.workflow.listeners.global.ProcessStartListener;
import com.wuji.workflow.listeners.global.TaskCreateListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author : bruce.liu
 * @title: : FlowableGlobListenerConfig
 * @projectName : flowable
 * @description: 全局监听配置 ContextRefreshedEvent在类被初始化之后触发
 * @date : 2021/05/11
 */
@Component
public class FlowableGlobListenerConfig {
    @Autowired
    private SpringProcessEngineConfiguration configuration;

    @Autowired
    private ProcessEndListener processEndListener;

    @Autowired
    private ProcessStartListener processStartListener;

    @Autowired
    private TaskCreateListener taskCreateListener;

    @Autowired
    private HistoricActivityInstanceEndListener historicActivityInstanceEndListener;

    @PostConstruct
    public void onApplicationEvent() {
        FlowableEventDispatcher dispatcher = configuration.getEventDispatcher();
        //添加流程实例结束全局监听
        dispatcher.addEventListener(processEndListener, FlowableEngineEventType.PROCESS_COMPLETED);
        dispatcher.addEventListener(processStartListener, FlowableEngineEventType.PROCESS_STARTED);
        dispatcher.addEventListener(taskCreateListener, FlowableEngineEventType.TASK_CREATED);
        dispatcher.addEventListener(historicActivityInstanceEndListener,
                FlowableEngineEventType.HISTORIC_ACTIVITY_INSTANCE_ENDED);
    }
}
