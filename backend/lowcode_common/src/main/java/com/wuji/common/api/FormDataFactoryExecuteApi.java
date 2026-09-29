package com.wuji.common.api;

import com.wuji.common.model.vo.DataFactoryInputFormVO;
import com.wuji.common.model.vo.DataFactoryStageAllFieldVO;
import com.wuji.common.model.vo.DataFactoryStageInfoVO;
import com.wuji.common.model.vo.FormDataFactoryParamVO;

import java.util.List;

public interface FormDataFactoryExecuteApi {

    DataFactoryStageInfoVO getStageField(String applicationId, String id);

    FormDataFactoryParamVO getParam(String applicationId, String id);

    List<DataFactoryStageAllFieldVO> getAllFinalField(String applicationId);

    List<DataFactoryInputFormVO> getAllInput(String applicationId, List<String> id);
}
