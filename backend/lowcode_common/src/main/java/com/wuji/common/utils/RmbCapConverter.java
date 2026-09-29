package com.wuji.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RmbCapConverter {
    private static final String[] RMB_UNIT = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿","拾","佰","仟"};
    private static final String[] RMB_DIGIT = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    public static String convertToRmbCap(double amount) {
        BigDecimal bd = new BigDecimal(amount);
        String rmbCap = "";

        // 判断金额正负
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            rmbCap += "负";
            bd = bd.abs();  // 取绝对值
        }

        String rmbStr = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();

        int dotPos = rmbStr.indexOf(".");
        String integerPart = dotPos == -1 ? rmbStr : rmbStr.substring(0, dotPos);
        String decimalPart = dotPos == -1 ? "" : rmbStr.substring(dotPos + 1);

        rmbCap += translateIntegerPart(integerPart);
        rmbCap += translateDecimalPart(decimalPart);

        return rmbCap;
    }

    private static String translateIntegerPart(String integerPart) {
        // 移除整数部分的前导零
        integerPart = integerPart.replaceFirst("^0+", "");

        if (integerPart.isEmpty()) {
            return RMB_DIGIT[0];
        }

        StringBuilder rmbCap = new StringBuilder();
        int digitCount = integerPart.length();
        int unitIndex = digitCount - 1;
        boolean zeroFlag = false;

        for (int i = 0; i < digitCount; i++) {
            int digit = integerPart.charAt(i) - '0';

            if (digit == 0) {
                zeroFlag = true;
            } else {
                if (zeroFlag) {
                    rmbCap.append(RMB_DIGIT[0]);
                }

                zeroFlag = false;
                rmbCap.append(RMB_DIGIT[digit]).append(RMB_UNIT[unitIndex]);
            }

            unitIndex--;
        }

        return rmbCap.toString()+"元";
    }

    private static String translateDecimalPart(String decimalPart) {
        if (decimalPart.isEmpty() || decimalPart.equals("00")) {
            return "";
        } else {
            int digit1 = decimalPart.charAt(0) - '0';
            int digit2 = decimalPart.charAt(1) - '0';
            StringBuilder rmbCap = new StringBuilder();

            if (digit1 != 0) {
                rmbCap.append(RMB_DIGIT[digit1]).append("角");
            }

            if (digit2 != 0) {
                rmbCap.append(RMB_DIGIT[digit2]).append("分");
            }

            return rmbCap.toString();
        }
    }
}
