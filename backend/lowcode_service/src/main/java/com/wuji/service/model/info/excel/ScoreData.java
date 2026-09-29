package com.wuji.service.model.info.excel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreData {
    private List<ColumnDef> columns;
    private List<Map<String, Object>> dataList; // 行数据，key 是 dataIndex
    private List<Dept> departmentList;
    private List<User> userList;
}


