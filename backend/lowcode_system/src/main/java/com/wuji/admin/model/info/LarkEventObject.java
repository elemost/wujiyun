package com.wuji.admin.model.info;

import lombok.Data;

@Data
public class LarkEventObject {
    private String department_id;

    private String name;

    private String open_department_id;

    private String parent_department_id;

    private LarkEventObjectStatus status;


}
