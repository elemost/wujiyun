package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;

import java.util.List;

@Data
public class WeComUserInfoVO extends WeComResult {
    private String name;

    private List<String> department;

    private String position;

    private String userid;
}
