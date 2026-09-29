package com.wuji.message.service;

import java.util.List;

public interface EmailService {
    void sendEmailByEmail(List<String> emailList, String html, String title);

    void sendEmail(List<String> userIdList, String html, String title);
}
