package com.wuji.common.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FunctionTypeVO {
    private String groupName;

    private List<FunctionVO> items;
}
