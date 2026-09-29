package com.wuji.service.model.info.plugin;

import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.Data;

import java.util.List;

@Data
public class HttpPluginRequestMapping {
    private String fieldId;

    private String subForm;

    private String fieldType;


    private String quoteType;

    /**
     * 自定义value
     */
    private Object customValue;

    private DataStreamQuoteField quoteField;

    private List<HttpPluginRequestMapping> children;


}
