package com.wuji.console.config;

import com.google.common.collect.Lists;

import java.util.List;

public class Constants {
    public static List<String> blackList =
            Lists.newArrayList("form/mongodb/update", "form/mongodb/insert", "form/mongodb/analysisExcel",
                    "form/mongodb/exportExcel", "form/mongodb/updateBatch", "form/mongodb/batchDelete");

    public static Boolean checkUrl(String url, Long companyId) {
        if (companyId != null) {
            return Boolean.FALSE;
        }
        for (String blackUrl : blackList) {
            if (url.contains(blackUrl)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
