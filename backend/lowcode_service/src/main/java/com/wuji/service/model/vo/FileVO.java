package com.wuji.service.model.vo;

import lombok.Data;

@Data
public class FileVO {
    private String fileUrl;

    private String filePath;

    private String fileName;

    private String imgUrl;

    private String imgType;

    private String imgKey;

    private Boolean secret;

    private String bucket;

    private String id;
}
