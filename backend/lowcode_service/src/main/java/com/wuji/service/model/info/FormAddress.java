package com.wuji.service.model.info;

import lombok.Data;

@Data
public class FormAddress {
    private String fullAddress;

    private String value;

    private String label;

    private String detailedAddress;

    private String district;

    private String city;

    private String province;
}
