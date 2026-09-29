package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeComCreateOrderVO extends WeComResult {
    private String order_id;
}
