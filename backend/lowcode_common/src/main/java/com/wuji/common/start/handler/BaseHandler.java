package com.wuji.common.start.handler;


public interface BaseHandler {
    default int getWeight() {
        return 1;
    }
}
