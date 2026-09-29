package com.wuji.plugin.demo;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 简道云插件节点OAuth2.0 Authorization Code认证客户端
 * 支持简道云配置中的各项参数传递
 */
public class JiandaoyunOauthClient {
    // 简道云配置参数
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationUrl;
    private String tokenUrl;
    private String refreshTokenUrl;
    private Map<String, String> authParams; // 授权参数
    private Map<String, String> tokenParams; // 接口参数
    private Map<String, String> refreshParams; // 刷新Token参数

    /**
     * 构造函数，初始化基础配置
     */
    public JiandaoyunOauthClient(String clientId, String clientSecret, String redirectUri,
                                 String authorizationUrl, String tokenUrl, String refreshTokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authorizationUrl = authorizationUrl;
        this.tokenUrl = tokenUrl;
        this.refreshTokenUrl = refreshTokenUrl;
        this.authParams = new HashMap<>();
        this.tokenParams = new HashMap<>();
        this.refreshParams = new HashMap<>();
    }

    // 添加授权参数（对应简道云"设置授权参数"）
    public void addAuthParam(String key, String value) {
        this.authParams.put(key, value);
    }

    // 添加接口参数（对应简道云"设置接口参数"）
    public void addTokenParam(String key, String value) {
        this.tokenParams.put(key, value);
    }

    // 添加刷新Token参数（对应简道云"配置刷新Token参数"）
    public void addRefreshParam(String key, String value) {
        this.refreshParams.put(key, value);
    }

    /**
     * 生成授权链接（对应简道云"配置授权地址"）
     */
    public String generateAuthorizationUrl() throws Exception {
        StringBuilder urlBuilder = new StringBuilder(authorizationUrl);
        urlBuilder.append("?response_type=code")
                .append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8.name()))
                .append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name()));

        // 添加额外授权参数
        Set<Map.Entry<String, String>> entrySet = authParams.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            urlBuilder.append("&").append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
        }

        return urlBuilder.toString();
    }

    /**
     * 使用Authorization Code获取访问令牌
     */
    public TokenResponse getAccessToken(String code) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // 构建请求参数
            StringBuilder paramsBuilder = new StringBuilder();
            paramsBuilder.append("grant_type=authorization_code")
                    .append("&code=").append(code)
                    .append("&client_id=").append(clientId)
                    .append("&client_secret=").append(clientSecret)
                    .append("&redirect_uri=").append(redirectUri);

            // 添加额外接口参数
            Set<Map.Entry<String, String>> entrySet = tokenParams.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                paramsBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }

            httpPost.setEntity(new StringEntity(paramsBuilder.toString()));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    return new TokenResponse(
                            jsonObject.getString("access_token"),
                            jsonObject.getString("refresh_token"),
                            jsonObject.getInteger("expires_in")
                    );
                }
            }
        }
        return null;
    }

    /**
     * 刷新访问令牌（对应简道云"配置刷新Token参数"）
     */
    public TokenResponse refreshAccessToken(String refreshToken) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(refreshTokenUrl);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // 构建刷新参数
            StringBuilder paramsBuilder = new StringBuilder();
            paramsBuilder.append("grant_type=refresh_token")
                    .append("&refresh_token=").append(refreshToken)
                    .append("&client_id=").append(clientId)
                    .append("&client_secret=").append(clientSecret);

            // 添加额外刷新参数
            Set<Map.Entry<String, String>> entrySet = refreshParams.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                paramsBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }

            httpPost.setEntity(new StringEntity(paramsBuilder.toString()));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    return new TokenResponse(
                            jsonObject.getString("access_token"),
                            jsonObject.getString("refresh_token"),
                            jsonObject.getInteger("expires_in")
                    );
                }
            }
        }
        return null;
    }

    /**
     * 调用受保护的API
     */
    public String callProtectedApi(String apiUrl, String accessToken) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        }
    }

    /**
     * 令牌响应模型
     */
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Integer expiresIn;

        public TokenResponse(String accessToken, String refreshToken, Integer expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }

        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public Integer getExpiresIn() { return expiresIn; }
    }

    // 示例用法
    public static void main(String[] args) {
        try {
            // 1. 初始化配置（对应简道云的各项设置）
            JiandaoyunOauthClient client = new JiandaoyunOauthClient(
                    "your_client_id",          // 客户端ID
                    "your_client_secret",      // 客户端密钥
                    "https://your-redirect-uri", // 重定向地址（简道云"设置重定向地址"）
                    "https://third-party.com/oauth/authorize", // 授权地址（简道云"配置授权地址"）
                    "https://third-party.com/oauth/token",     // 令牌接口地址
                    "https://third-party.com/oauth/refresh"    // 刷新令牌地址
            );

            // 添加授权参数（简道云"设置授权参数"）
            client.addAuthParam("scope", "read:data write:data");
            client.addAuthParam("state", "random_state_123");

            // 添加接口参数（简道云"设置接口参数"）
            client.addTokenParam("resource", "jiandaoyun_plugin");

            // 添加刷新Token参数（简道云"配置刷新Token参数"）
            client.addRefreshParam("scope", "offline_access");

            // 2. 生成授权链接
            String authUrl = client.generateAuthorizationUrl();
            System.out.println("请访问以下链接授权: " + authUrl);

            // 3. 模拟获取到的Authorization Code（实际从回调地址获取）
            String code = "user_authorized_code_from_callback";

            // 4. 获取访问令牌
            TokenResponse token = client.getAccessToken(code);
            if (token != null) {
                System.out.println("获取到AccessToken: " + token.getAccessToken());
                System.out.println("有效期: " + token.getExpiresIn() + "秒");

                // 5. 调用API
                String apiResult = client.callProtectedApi("https://third-party.com/api/data", token.getAccessToken());
                System.out.println("API返回结果: " + apiResult);

                // 6. 刷新令牌（当令牌快过期时）
                TokenResponse newToken = client.refreshAccessToken(token.getRefreshToken());
                System.out.println("刷新后的AccessToken: " + newToken.getAccessToken());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
