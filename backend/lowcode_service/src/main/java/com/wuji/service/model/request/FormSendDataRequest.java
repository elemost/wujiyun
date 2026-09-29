package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormSendDataRequest {
    private JSONObject dataJson;

    private String nonce;

    private String msgSignature;

    private String processInstanceId;

    // create delete update process_finish activity_finish time
    private String action;

    private String appKey;

    private String creator;

    private String uuid;
}
