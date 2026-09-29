package com.wuji.factory.model.vo;

import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import lombok.Data;

import java.util.List;

@Data
public class DataFactoryStageFieldVO {
    private List<DataFactoryReturnFieldVO> fields;

    private String title;

    private String id;
}
