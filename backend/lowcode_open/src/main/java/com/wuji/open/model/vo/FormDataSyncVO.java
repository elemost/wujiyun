package com.wuji.open.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormDataSyncVO {
    private String uuid;

    private JSONObject instValue;
}
