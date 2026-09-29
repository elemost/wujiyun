package com.wuji.admin.client.lark.model;

import lombok.Data;

import java.util.List;

@Data
public class LarkDepartmentInfoVO {
    private String name;

    // private String department_id;

    private String parent_department_id;

    private String chat_id;

    private String open_department_id;

    private List<Integer> group_chat_employee_types;

    private List<LarkDepartmentLeaderInfoVO> leaders;
}
