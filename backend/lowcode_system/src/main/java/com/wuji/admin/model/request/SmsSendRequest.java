package com.wuji.admin.model.request;

import lombok.Data;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Data
public class SmsSendRequest {
    private List<String> mobileList;
    private String smsScene;
    private LinkedHashMap<String, String> params;

    public SmsSendRequest(Builder builder) {
        this.mobileList = builder.mobileList;
        this.smsScene = builder.smsScene;
        this.params = builder.params;
    }

    public static class Builder {
        private List<String> mobileList;
        private String smsScene;
        private LinkedHashMap<String, String> params;

        public Builder setMobileList(List<String> mobileList) {
            this.mobileList = mobileList;
            return this;
        }

        public Builder setSmsScene(String smsScene) {
            this.smsScene = smsScene;
            return this;
        }

        public Builder setParams(LinkedHashMap<String, String> params) {
            this.params = params;
            return this;
        }

        public SmsSendRequest build() {
            return new SmsSendRequest(this);
        }
    }
}
