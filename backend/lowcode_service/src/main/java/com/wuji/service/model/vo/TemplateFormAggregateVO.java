package com.wuji.service.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TemplateFormAggregateVO {
    private String id;

    @TableId("application_id")
    private String applicationId;

    private String name;

    private String config;
}
