package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FormDataStreamTrigger {
    private String triggerId;

    private String applicationId;

    // create delete update process_finish activity_finish
    private String action;

    private JSONObject jsonObject;

    private List<String> updateKey;

    private Boolean trigger;

    private DataStreamTriggerLogDomain dataStreamTrigger;

    private String activityId;

    private String activityAction;

    private UserDomain userDomain;

    private Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap;

    private List<String> parentList = new ArrayList<>();

    private Boolean endNode;

    private Integer cycleIndex;

    private Long cycleId;

    private String cycleKey;

    private Integer delay = 0;
}
