package com.wuji.workflow.model.flowable.model;

import lombok.Data;

@Data
public class NodeListener {
    private String event;
    private String implementation;
    private String implementationType;
}
