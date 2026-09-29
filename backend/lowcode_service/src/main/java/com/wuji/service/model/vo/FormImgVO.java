package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class FormImgVO {

    private String imgUrl;

    private String imgType;

    private String imgKey;

    private String fileName;

    private Boolean secret;

    private String bucket;

    private String id;
}
