package com.wuji.service.model.info;

import lombok.Data;

@Data
public class FormDataTitle {
    private String uuid;

    private String title;

    public FormDataTitle(String uuid, String title) {
        this.title = title;
        this.uuid = uuid;
    }
}
