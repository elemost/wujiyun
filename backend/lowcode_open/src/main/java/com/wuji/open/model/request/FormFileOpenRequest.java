package com.wuji.open.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormFileOpenRequest {
    private List<String> fileIds;

    private String appKey;

    private String creator;
}
