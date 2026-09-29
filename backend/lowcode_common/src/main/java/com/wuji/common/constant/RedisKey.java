package com.wuji.common.constant;

public class RedisKey {

    public static String getSuiteTicketKey(String suiteId) {
        return "SUITEID_" + suiteId;
    }

    public static String getSuiteCorpTokenKey(String suiteId, String corpId) {
        return "AUTH_CORP_" + corpId + "_" + suiteId;
    }

    public static String getSuiteTokenKey(String suiteId) {
        return "SUITE_TOKEN_" + suiteId;
    }
}
