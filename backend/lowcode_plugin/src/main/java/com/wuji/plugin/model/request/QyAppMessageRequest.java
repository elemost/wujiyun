package com.wuji.plugin.model.request;

import com.wuji.plugin.model.info.Markdown;
import lombok.Data;

import java.util.List;

@Data
public class QyAppMessageRequest {
    private Long companyId;

    private List<Long> userIdList;

    private List<Markdown> configList;
}
