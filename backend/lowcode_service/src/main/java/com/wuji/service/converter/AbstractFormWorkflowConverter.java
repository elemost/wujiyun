package com.wuji.service.converter;

import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.request.FormFlowableCopyRequest;
import com.wuji.service.model.request.FormFlowableDoneListRequest;
import com.wuji.service.model.request.FormFlowableOwnerListRequest;
import com.wuji.service.model.request.FormFlowableToDoListRequest;
import com.wuji.service.model.vo.FormDoneTaskVO;
import com.wuji.service.model.vo.FormFlowableCopyVO;
import com.wuji.service.model.vo.FormOwnerTaskVO;
import com.wuji.service.model.vo.FormPendingTaskVO;
import com.wuji.workflow.model.info.FlowableMongodbSearchFilter;
import com.wuji.workflow.model.request.FlowableCopyRequest;
import com.wuji.workflow.model.request.FlowableDoneListRequest;
import com.wuji.workflow.model.request.FlowableOwnerListRequest;
import com.wuji.workflow.model.request.FlowableToDoListRequest;
import com.wuji.workflow.model.vo.DoneTaskVO;
import com.wuji.workflow.model.vo.FlowableCopyVO;
import com.wuji.workflow.model.vo.OwnerTaskVO;
import com.wuji.workflow.model.vo.PendingTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AbstractFormWorkflowConverter {
    public static final AbstractFormWorkflowConverter INSTANCE = Mappers.getMapper(AbstractFormWorkflowConverter.class);

    @Mapping(source = "applicationId", target = "category")
    public abstract FlowableToDoListRequest toRequest(FormFlowableToDoListRequest formFlowableToDoListRequest);

    public abstract FormPendingTaskVO toVO(PendingTaskVO pendingTaskVO);

    @Mapping(source = "applicationId", target = "category")
    public abstract FlowableOwnerListRequest toRequest(FormFlowableOwnerListRequest formFlowableOwnerListRequest);


    public abstract FormOwnerTaskVO toVO(OwnerTaskVO ownerTaskVO);

    @Mapping(source = "applicationId", target = "category")
    public abstract FlowableDoneListRequest toRequest(FormFlowableDoneListRequest FlowableDoneListRequest);

    public abstract FormDoneTaskVO toVO(DoneTaskVO doneTaskVO);

    public abstract FlowableCopyRequest toRequest(FormFlowableCopyRequest formFlowableCopyRequest);

    public abstract FormFlowableCopyVO toVO(FlowableCopyVO flowableCopyVO);

    public abstract MongodbSearchFilter toFilter(FlowableMongodbSearchFilter filter);


}
