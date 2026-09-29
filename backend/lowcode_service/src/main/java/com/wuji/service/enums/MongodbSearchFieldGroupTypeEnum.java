package com.wuji.service.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum MongodbSearchFieldGroupTypeEnum {
    DODV("天的增长值", FormAggregateTimeTransTypeEnum.DAY, false),
    MOMV("月的增长值", FormAggregateTimeTransTypeEnum.MONTH, false),
    YOYV("年增长值", FormAggregateTimeTransTypeEnum.YEAR, false),
    WOWV("周增长值", FormAggregateTimeTransTypeEnum.WEEK, false),
    QOQV("季增长值", FormAggregateTimeTransTypeEnum.QUARTER, false),
    DODR("天的增长率", FormAggregateTimeTransTypeEnum.DAY, true),
    MOMR("月的增长率", FormAggregateTimeTransTypeEnum.MONTH, true),
    YOYR("年增长率", FormAggregateTimeTransTypeEnum.YEAR, true),
    WOWR("周增长率", FormAggregateTimeTransTypeEnum.WEEK, true),
    QOQR("季增长率", FormAggregateTimeTransTypeEnum.QUARTER, true),
    VOVV("value增长值", null, false),
    VOVR("value增长率", null, true);

    private final String message;

    private final FormAggregateTimeTransTypeEnum transType;

    private final Boolean needDivide;

    public static List<String> groupTypeList() {
        return Lists.newArrayList(DODV.name(), MOMV.name(), YOYV.name(), WOWV.name(), QOQV.name(), DODR.name(),
                MOMR.name(), YOYR.name(), WOWR.name(), QOQR.name());
    }
}
