package com.wuji.factory.service;

import com.wuji.factory.model.request.DataFactoryRequest;
import com.wuji.factory.model.vo.DataFactoryDistinctFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageVO;
import com.wuji.factory.model.vo.FormDataFactoryBuildVO;
import com.wuji.service.model.request.factory.DataFactoryVO;

import java.util.List;

public interface FormDataFactoryExecuteService {
    DataFactoryVO lookUpAndAggregate(DataFactoryRequest dataFactoryRequest);

    DataFactoryDistinctFieldVO distinctField(DataFactoryRequest dataFactoryRequest);

    List<DataFactoryStageFieldVO> stageField(DataFactoryRequest dataFactoryRequest);

    List<DataFactoryStageVO> getInfoByIds(List<String> ids, String applicationId);

    FormDataFactoryBuildVO getParam(DataFactoryRequest dataFactoryRequest);

    void syncDataExecute(String id, String applicationId);

    void checkFormula();

    void trigger(String id, String applicationId);
}
