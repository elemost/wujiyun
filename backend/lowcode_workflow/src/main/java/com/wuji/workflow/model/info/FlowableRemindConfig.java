package com.wuji.workflow.model.info;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class FlowableRemindConfig {
    private Boolean sendEmail = false;

    private Boolean inMail = false;

    private Boolean sendWeCom = false;

    private Boolean sendDingTalk = false;

    private String inMailContent;

    private String content;

    private String emailContent;

    private JSONObject quoteFieldJson;

    private String title;

    private List<FlowableAssigneeConfig> assigneeList;

    private List<String> assigneeUserList;
}
