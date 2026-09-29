package com.wuji.admin.client.qcc.model;

import lombok.Data;

import java.util.List;

@Data
public class FuzzySearchVO {
    private Integer total;

    private String keyword;

    private List<QccBasicDetailVO> list;
}
