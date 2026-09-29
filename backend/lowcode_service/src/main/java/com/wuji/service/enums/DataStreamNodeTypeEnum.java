package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public enum DataStreamNodeTypeEnum {
    TRIGGER("trigger"),
    CREATE("create"),
    CALCULATE("calculate"),
    UPDATE("update"),
    DELETE("delete"),
    ONE("one"),
    MORE("more"),
    PLUGIN("plugin"),
    TIME_TRIGGER("time_trigger"),
    CYCLE("loop"),
    END("end"),
    BUTTON_TRIGGER("button_trigger"),
    START("start"),;

    private final String nodeType;

    public static List<String> valueIsJson() {
        return Lists.newArrayList(CREATE.getNodeType(), TRIGGER.getNodeType(), ONE.getNodeType(), MORE.getNodeType(),
                BUTTON_TRIGGER.getNodeType(), TIME_TRIGGER.getNodeType());
    }

    public static List<String> formNodeList() {
        return Lists.newArrayList(DELETE.getNodeType(), UPDATE.getNodeType(), CREATE.getNodeType(), ONE.getNodeType(),
                MORE.getNodeType());
    }

    public static List<String> typeExistSize() {
        return Lists.newArrayList(ONE.getNodeType(), MORE.getNodeType());
    }


}
