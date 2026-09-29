package com.wuji.service.model.info.excel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnDef {
    private String title;
    private String dataIndex;   // 对应 dataList 的 key："1"、"2"...
}
