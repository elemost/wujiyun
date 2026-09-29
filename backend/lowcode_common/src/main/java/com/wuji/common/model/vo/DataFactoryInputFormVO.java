package com.wuji.common.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class DataFactoryInputFormVO {

    private String id;

    private Integer version;

    private List<Input> inputs;

    @Data
    public static class Input {
        private String formId;

        private String applicationId;
    }
}
