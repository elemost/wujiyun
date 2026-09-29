package com.wuji.factory.model.request;

import com.wuji.factory.model.info.DataFactoryStage;
import com.wuji.service.model.vo.FieldExistNameVO;
import lombok.Data;

import java.util.List;

@Data
public class DataFactoryRequest {
    private String stageId;

    private String applicationId;

    private String distinctField;

    private List<DataFactoryStage> dataFactoryStageList;

    private List<FieldExistNameVO> allFormConfigCommonList;
}
