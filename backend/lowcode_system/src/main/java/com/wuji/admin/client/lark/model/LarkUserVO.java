package com.wuji.admin.client.lark.model;

import lombok.Data;

import java.util.List;

@Data
public class LarkUserVO {
    private String name;

    private String job_title;

    private List<String> department_ids;

    private String mobile;

    private String open_id;

    private String union_id;

    private String user_id;

    private LarkUserStatus status;
}
