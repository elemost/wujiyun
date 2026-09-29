package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormImgRequest {

    private String id;

    private String imgUrl;

    private String imgType;

    private String imgKey;

    private Boolean secret;

    private String bucket;

    private String fileName;
}
