package com.wuji.service.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.FormFlowableCopyRequest;
import com.wuji.service.model.request.FormFlowableDoneListRequest;
import com.wuji.service.model.request.FormFlowableOwnerListRequest;
import com.wuji.service.model.request.FormFlowableToDoListRequest;
import com.wuji.service.model.request.FormWorkflowCreateTaskRequest;
import com.wuji.service.model.request.WorkFlowBatchAuditRequest;
import com.wuji.service.model.vo.FormDoneTaskVO;
import com.wuji.service.model.vo.FormFlowableCopyVO;
import com.wuji.service.model.vo.FormFlowableStatisticVO;
import com.wuji.service.model.vo.FormOwnerTaskVO;
import com.wuji.service.model.vo.FormPendingCountVO;
import com.wuji.service.model.vo.FormPendingTaskVO;

import java.util.List;

public interface FormWorkflowService {

    /**
     * 发起任务
     *
     * @param formWorkflowCreateTaskRequest
     */
    void createTask(FormWorkflowCreateTaskRequest formWorkflowCreateTaskRequest);


    /**
     * 代办列表
     * @param flowableToDoListRequest
     * @return
     */
    QueryPageVO<FormPendingTaskVO> getPendingList(FormFlowableToDoListRequest flowableToDoListRequest);

    List<FormPendingCountVO> getFormPendingCount(FormFlowableToDoListRequest flowableToDoListRequest);

    Integer pendingCount(FormFlowableToDoListRequest formFlowableToDoListRequest);

    /**
     * 获取自己发起任务
     * @param flowableOwnerListRequest
     * @return
     */
    QueryPageVO<FormOwnerTaskVO> getOwnerList(FormFlowableOwnerListRequest flowableOwnerListRequest);

    /**
     * 获取自己已办任务
     * @param formFlowableDoneListRequest
     * @return
     */
    QueryPageVO<FormDoneTaskVO> getDoneList(FormFlowableDoneListRequest formFlowableDoneListRequest);

    /**
     * 获取抄送列表
     * @param formFlowableCopyRequest
     * @return
     */
    QueryPageVO<FormFlowableCopyVO> getCopyList(FormFlowableCopyRequest formFlowableCopyRequest);


    FormFlowableStatisticVO flowableStatistic();

    String batchComplete(WorkFlowBatchAuditRequest workFlowBatchAuditRequest);

    String batchReject(WorkFlowBatchAuditRequest workFlowBatchAuditRequest);
}
