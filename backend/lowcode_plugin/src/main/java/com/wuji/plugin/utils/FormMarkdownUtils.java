package com.wuji.plugin.utils;

import com.wuji.plugin.model.info.Markdown;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FormMarkdownUtils {
    public static String markContent(List<Markdown> markdowns) {
        StringBuilder builder = new StringBuilder();
        for (Markdown markdown : markdowns) {
            String content = getContent(markdown);
            builder.append(content);
        }
        return builder.toString();
    }

    public static String lineBreak(String content) {
        return content + "\n>";
    }

    public static String getContent(Markdown markdown) {
        String content = "";
        if (StringUtils.isEmpty(markdown.getLabel())) {
            if ("url".equals(markdown.getMarkdownType())) {
                content = getUrlContent(markdown);
            } else {
                content = markdown.getMarkdownValue().toString();
            }
        } else {
            if ("url".equals(markdown.getMarkdownType())) {
                content = markdown.getLabel() + "：" + getUrlContent(markdown);
            } else {
                content = markdown.getLabel() + "：" + markdown.getMarkdownValue();
            }

        }
        return lineBreak(content);
    }

    public static String getUrlContent(Markdown markdown) {
        String content = "[%s](%s)";
        return String.format(content, markdown.getUrlLabel(), markdown.getMarkdownValue());
    }
}
