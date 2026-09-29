package com.wuji.workflow.converter;

import com.wuji.workflow.model.domain.FlowablePageQueryDomain;
import com.wuji.workflow.model.request.FlowableDoneListRequest;
import com.wuji.workflow.model.request.FlowableOwnerListRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.TaskVO;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFlowableConverter {
    public static final AbstractFlowableConverter INSTANCE = Mappers.getMapper(AbstractFlowableConverter.class);

    public abstract FlowablePageQueryDomain toQueryDomain(FlowableDoneListRequest flowableDoneListRequest);

    public abstract FlowablePageQueryDomain toQueryDomain(FlowableToDoListRequest flowableToDoListRequest);

    public abstract FlowablePageQueryDomain toQueryDomain(FlowableOwnerListRequest flowableOwnerListRequest);

    public abstract TaskVO toVO(Task task);
}
