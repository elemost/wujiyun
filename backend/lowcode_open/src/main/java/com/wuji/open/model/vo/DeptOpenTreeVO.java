package com.wuji.open.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeptOpenTreeVO {
    private Long deptId;

    private Long parentId;

    private String deptName;

    private List<DeptOpenTreeVO> children;
}
