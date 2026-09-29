package com.wuji.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.utils.TimeUtils;
import com.wuji.plugin.model.info.Markdown;
import com.wuji.service.model.info.FormMessageMarkdown;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageUtils {
    public static List<Markdown> getMarkDown(List<FormMessageMarkdown> formMessageMarkdowns, JSONObject jsonObject) {
        List<Markdown> markdowns = new ArrayList<>();
        for (FormMessageMarkdown formMessageMarkdown : formMessageMarkdowns) {
            if ("custom".equals(formMessageMarkdown.getValueType())) {
                markdowns.add(new Markdown(formMessageMarkdown.getLabel(), formMessageMarkdown.getCustomValue(),
                        formMessageMarkdown.getMarkdownType(), formMessageMarkdown.getUrlLabel()));
            } else {
                markdowns.add(new Markdown(formMessageMarkdown.getLabel(),
                        jsonObject.getOrDefault(formMessageMarkdown.getFieldId(), ""),
                        formMessageMarkdown.getMarkdownType(), formMessageMarkdown.getUrlLabel()));
            }
        }
        return markdowns;
    }

    public static List<Markdown> getMarkDown(List<FormMessageMarkdown> formMessageMarkdowns,
                                             Map<Long, DataStreamCalculateVO> nodeIdMap) {

        List<Markdown> markdowns = new ArrayList<>();
        for (FormMessageMarkdown formMessageMarkdown : formMessageMarkdowns) {

            if ("custom".equals(formMessageMarkdown.getValueType())) {
                markdowns.add(new Markdown(formMessageMarkdown.getLabel(), formMessageMarkdown.getCustomValue(),
                        formMessageMarkdown.getMarkdownType(), formMessageMarkdown.getUrlLabel()));
            } else {
                JSONObject jsonObject = nodeIdMap.get(formMessageMarkdown.getNodeId()).getJsonValue();
                List<Object> jsonValue = MongoSearchUtils.getJsonValue(formMessageMarkdown.getFieldId(), "",
                        formMessageMarkdown.getFieldType(), jsonObject);
                if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(formMessageMarkdown.getFieldType())) {
                    if (CollectionUtils.isNotEmpty(jsonValue)) {
                        Object object = jsonValue.get(0);
                        jsonValue = new ArrayList<>();
                        if (object != null) {
                            jsonValue.add(TimeUtils.formatDateTime(new Date(Long.parseLong(object.toString()))));
                        }
                    }
                }
                markdowns.add(new Markdown(formMessageMarkdown.getLabel(), StringUtils.join(jsonValue, "，"),
                        formMessageMarkdown.getMarkdownType(), formMessageMarkdown.getUrlLabel()));
            }
        }
        return markdowns;
    }
}
