package com.wuji.factory.model.vo;

import com.wuji.service.model.request.factory.DataFactoryReturnFieldVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataFactoryStageVO {
    private List<DataFactoryReturnFieldVO> fields = new ArrayList<>();

   private String name;

   private String id;
}
