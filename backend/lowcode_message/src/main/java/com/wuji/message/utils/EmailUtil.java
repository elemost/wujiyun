package com.wuji.message.utils;

import com.wuji.message.model.info.EmailConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Slf4j
public class EmailUtil {

    public static void sendEmail(List<String> emailList, String html, String title, EmailConfig emailConfig) {
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", emailConfig.getMailHost());
        properties.put("mail.smtp.port", emailConfig.getMailPort()); // 对于Gmail, 使用465或587，取决于SSL/TLS的使用
        properties.put("mail.smtp.auth", "true"); // 需要验证
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.ssl.enable", true);
        // 获取默认的Session对象。这里认证用户名和密码。
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUserName(),
                        emailConfig.getPassword()); // 用你的邮箱和密码替换这里的username和password
            }
        });

        try {
            // 创建默认的MimeMessage对象。
            MimeMessage message = new MimeMessage(session);
            // Set From: 头部头字段
            message.setFrom(new InternetAddress(emailConfig.getUserName()));
            // Set To: 头部头字段
            List<Address> address = new ArrayList<>();
            for (String email : emailList) {
                if (StringUtils.isEmpty(email)) {
                    continue;
                }
                address.add(new InternetAddress(email));
            }
            if (CollectionUtils.isEmpty(address)) {
                return;
            }
            message.setRecipients(Message.RecipientType.TO, address.toArray(new Address[0]));
            // Set Subject: 头部头字段
            message.setSubject(title);
            // 设置消息体
            message.setText(html);
            // 发送消息
            Transport.send(message);
            log.info("发送成功");
        } catch (Exception e) {
            log.error("邮箱发送失败", e);
        }
    }

}
