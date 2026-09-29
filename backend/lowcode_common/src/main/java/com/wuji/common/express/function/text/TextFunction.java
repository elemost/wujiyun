package com.wuji.common.express.function.text;

import cn.hutool.core.date.DateUtil;
import com.ql.util.express.Operator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextFunction extends Operator {

    public TextFunction(String name) {
        this.name = name;
    }


    @Override
    public Object executeInner(Object[] list) throws Exception {

        if (list.length == 0 || list.length == 1) {
            return "";
        }

        Object obj = null;

        // 获取参数
        Object num = list[0];

        if (num instanceof String) {
            String numStr = num.toString();
            if (numStr.indexOf("GMT+0800") > 0) {
                String pattern = "EEE MMM dd yyyy HH:mm:ss 'GMT'Z";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
                Date date = sdf.parse(numStr);
                String textFormat = list[1].toString();
                obj = DateUtil.format(date, textFormat);
            }
        } else {
            String textFormat = list[1].toString();
            DecimalFormat decimalFormat = new DecimalFormat(textFormat);
            obj = decimalFormat.format(num);
        }
        return obj;
    }
}
