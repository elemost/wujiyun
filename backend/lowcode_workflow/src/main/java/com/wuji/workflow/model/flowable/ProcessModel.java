package com.wuji.workflow.model.flowable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wuji.workflow.model.flowable.model.Node;
import lombok.Data;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;

import java.util.List;

/**
 * @description：流程模型
 */
@Data
public class ProcessModel {
    @JsonSerialize(using = ToStringSerializer.class)
    private String modelKey;
    private String modelName;
    private Node process;
    private String remark;

    public BpmnModel toBpmnModel() {
        BpmnModel bpmnModel = new BpmnModel();
        // 命名空
        bpmnModel.setTargetNamespace("https://flowable.org/bpmn20");
        // 创建一个流程实例
        Process process = new Process();

        // 设置流程的id
        process.setId(this.getModelKey());
        // 设置流程的name
        process.setName(this.getModelName());
        // 设置流程的文档
        process.setDocumentation(this.getRemark());
        // 递归构建所有节点
        Node node = this.getProcess();
        List<FlowElement> flowElementList = node.convert();
        for (FlowElement flowElement : flowElementList) {
            process.addFlowElement(flowElement);
        }
        // 设置流程
        bpmnModel.addProcess(process);
        // 自动布局
        new BpmnAutoLayout(bpmnModel).execute();
        return bpmnModel;
    }

}
