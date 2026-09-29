package com.wuji.plugin.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class HttpPluginVO {
    private Object result;

    private JSONObject jsonObject;
}
