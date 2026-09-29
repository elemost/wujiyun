package com.wuji.common.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum OrderStatusEnum {
    NO_PAY((short)0, "未支付"),
    PAYING((short)1, "支付中"),
    PAYED((short)2, "已支付"),
    CANCLE((short)-1, "已取消"),
    ;

    private Short code;

    private String desc;

    OrderStatusEnum(Short code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Short getCode() {
        return code;
    }

    public void setCode(Short code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Short getStatus() {
        return Short.valueOf(code);
    }

    public static String getDescByCode(Short code) {
        if (code==null) {
            return "";
        }
        for (OrderStatusEnum enums : OrderStatusEnum.values()) {
            if (enums.getCode().equals(code)) {
                return enums.getDesc();
            }
        }
        return "";
    }
    /**
     * 返回所有的枚举
     * @param
     * @return
     * @author txy
     * @date 2022/8/26 18:38
     */
    public static List<OrderStatusEnum> getAllEnumList() {
        List<OrderStatusEnum> enumList = new ArrayList<>(Arrays.asList(values()));
        return enumList;
    }
}
