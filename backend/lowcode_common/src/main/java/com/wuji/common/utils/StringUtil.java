package com.wuji.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String getInitials(String chinese) {
        StringBuilder pinyin = new StringBuilder();

        // 将中文字符串转换为拼音数组
        for (char ch : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);

            // 如果字符是中文，则获取其拼音的首字母
            if (pinyinArray != null && pinyinArray.length > 0) {
                pinyin.append(pinyinArray[0].charAt(0));
            } else {
                // 非中文字符直接拼接
                pinyin.append(ch);
            }
        }
        if (pinyin.length() > 0) {
            return pinyin.substring(0, 1);
        } else {
            return "";
        }
    }

    /**
     * 随机生成6位数
     *
     * @return
     */
    public static String getRandNum() {
        StringBuilder stringBuffer = new StringBuilder();
        int num;
        for (int i = 0; i < 6; i++) {
            num = (int) (Math.random() * 9 + 1);
            stringBuffer.append(num);
        }
        return stringBuffer.toString();
    }

    public static List<String> getSize(String input) {
        List<String> sizeList = new ArrayList<>();
        String regex = "size=(\\w+)\\*(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            sizeList.add(matcher.group(1));
            sizeList.add(matcher.group(2));
        }

        return sizeList;
    }

    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone; // 如果号码不合法，直接返回原值
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^1[3-9]\\d{9}$"; // 匹配以1开头，第二位为3-9，总共11位数字
        return phoneNumber != null && phoneNumber.matches(regex);
    }

    public static List<String> matchData(String text) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(text);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    public static String replaceValue(String content, Map<String, Object> valueMap) {
        for (String match : valueMap.keySet()) {
            content = content.replace("${" + match + "}", valueMap.getOrDefault(match, "").toString());
        }
        return content;
    }

    public static List<String> extractAllFunctionCalls(String expression) {
        List<String> result = new ArrayList<>();
        // 核心正则：扩展函数名 (SUM|AVERAGE|COUNT|MIN|MAX)
        String regex = "(SUM|AVERAGE|COUNT|MIN|MAX)\\([^)]+\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            result.add(matcher.group()); // 获取完整匹配的函数调用
        }
        return result;
    }

    public static void main(String[] args) {

    }

    public static String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    /**
     * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
     *
     * @param s    原始字符串
     * @param size 字符串指定长度
     * @param c    用于补齐的字符
     * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
     */
    public static final String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
