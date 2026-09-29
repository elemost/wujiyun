package com.wuji.admin.model.vo.pull;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentPullVO {
    private String parentId;

    private String name;

    private String deptId;

    private List<String> leaderList;
}
