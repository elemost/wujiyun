package com.wuji.workflow.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.workflow.model.request.ModelListRequest;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.ModelCopyVO;
import com.wuji.workflow.model.vo.ModelVO;

import java.util.List;

public interface ModelManageService {
    /**
     * 创建模型
     *
     * @param modelRequest
     * @return
     */
    String createModel(ModelRequest modelRequest);

    /**
     * 修改模型
     * @param modelRequest
     */
    void updateModel(ModelRequest modelRequest);

    /**
     * 移除模型
     * @param modelId
     */
    void removeModel(String modelId);

    /**
     * 发布模型
     *
     * @param modelId 模型id
     * @return
     */
    String publish(String modelId);

    /**
     * 模型列表
     *
     * @param modelListRequest 模型查询条件
     * @return
     */
    QueryPageVO<ModelVO> getModelList(ModelListRequest modelListRequest);


    List<ModelVO> getModelByKey(String key);

    ModelVO info(String modelId);

    ModelVO infoByProcessDefinitionId(String processDefinitionId);

    ModelCopyVO copyModel(ModelRequest modelCopyRequest, Boolean needDefault, Boolean exist);

    ModelCopyVO createAndPublishModel(ModelRequest modelCopyRequest, Boolean needDefault, Boolean exist);

    void deleteByApplication(String applicationId);
}
