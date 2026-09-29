package com.wuji.service.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormColumn {

    private String title;

    private String dataIndex;

    private String key;

    private String width;

    private String tag;

    private Integer colSpan;

    private Integer rowSpan;

    private String value;

    private String tagId;

    private List<FormColumn> children = new ArrayList<>();
}
