package com.wuji.plugin.model.info.auth;

import com.wuji.plugin.model.info.HttpIdentityAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthorizationCode extends HttpIdentityAuth {
    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String authorizationUrl;

    private String tokenUrl;

    private String refreshTokenUrl;

    // 授权参数
    private Map<String, String> authParams;

    // 接口参数
    private Map<String, String> tokenParams;

    // 刷新Token参数
    private Map<String, String> refreshParams;


    public String generateAuthorizationUrl() throws Exception {
        StringBuilder urlBuilder = new StringBuilder(authorizationUrl);
        urlBuilder.append("?response_type=code").append("&client_id=")
                .append(URLEncoder.encode(clientId, StandardCharsets.UTF_8.name())).append("&redirect_uri=")
                .append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name()));

        // 添加额外授权参数
        Set<Map.Entry<String, String>> entrySet = authParams.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            urlBuilder.append("&").append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
        }

        return urlBuilder.toString();
    }
}
