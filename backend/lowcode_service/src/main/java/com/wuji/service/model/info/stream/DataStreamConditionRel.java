package com.wuji.service.model.info.stream;

import com.wuji.service.model.info.MongoFieldRelate;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamConditionRel {
    private List<MongoFieldRelate> relates;

    private String rel;
}
