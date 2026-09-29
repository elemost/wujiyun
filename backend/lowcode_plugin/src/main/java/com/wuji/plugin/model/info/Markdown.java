package com.wuji.plugin.model.info;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Markdown {
    private String label;

    private Object markdownValue;

    // text url
    private String markdownType;

    private String urlLabel;
}
