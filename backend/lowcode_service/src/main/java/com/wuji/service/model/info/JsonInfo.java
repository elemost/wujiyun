package com.wuji.service.model.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonInfo {
    private String jsonPath;

    private String type;

    private List<JsonInfo> children = new ArrayList<>();
}
