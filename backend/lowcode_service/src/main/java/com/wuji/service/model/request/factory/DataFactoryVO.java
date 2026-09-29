package com.wuji.service.model.request.factory;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataFactoryVO {
    private List<DataFactoryReturnFieldVO> header;

    private List<JSONObject> data;
}
