package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.privilege.annotation.ApplicationId;
import com.wuji.common.privilege.annotation.ResourceId;
import com.wuji.service.model.domain.ParentInfoMongodbDomain;
import com.wuji.service.model.request.publish.PublicPublishSecretRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormInsertDataRequest extends PublicPublishSecretRequest {

    @ResourceId
    private String formId;

    private Integer version;

    private JSONObject instValue;

    private String uuid;

    private String status;

    private String creator;

    @ApplicationId
    private String applicationId;

    // 主流程发起流程 需要填写的信息
    private ParentInfoMongodbDomain parentInfo;

    private Boolean dataStreamTrigger = false;

    private List<String> triggerParentList = new ArrayList<>();
}
