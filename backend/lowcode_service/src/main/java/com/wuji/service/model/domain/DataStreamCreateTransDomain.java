package com.wuji.service.model.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamCreateTransDomain {
    private Boolean more;

    private List<JSONObject> returnList;
}
