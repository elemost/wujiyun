package com.wuji.workflow.service;

import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.vo.ModelVO;
import org.flowable.bpmn.model.BpmnModel;

public interface FlowDesignerService {

    /**
     * 设计保存模型
     *
     * @param modelId                 模型id
     * @param bpmnXml
     * @param formModelDesignerDomain
     * @param newVersion
     * @return
     */
    String designModule(String modelId, String bpmnXml, FormModelDesignerDomain formModelDesignerDomain, Boolean newVersion);

    /**
     * 设计保存模型
     *
     * @param modelId    模型id
     * @param bpmnModel
     * @param formModelDesignerDomain
     * @param newVersion
     * @return
     */
    String designModule(String modelId, BpmnModel bpmnModel, FormModelDesignerDomain formModelDesignerDomain, Boolean newVersion);


    /**
     * 获取模型信息
     *
     * @param modelId 模型id
     * @return
     */
    ModelVO getModelJSON(String modelId);

    ModelVO getModelJSONByDeployId(String deployId);

    void delete(String applicationId);

}
