package com.wuji.plugin.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OAuth2ClientCredentialsDemo {
    // OAuth 服务器配置
    private static final String TOKEN_ENDPOINT = "https://oauth.example.com/token";
    private static final String CLIENT_ID = "your-client-id";
    private static final String CLIENT_SECRET = "your-client-secret";
    private static final String SCOPE = "read:data"; // 可选的权限范围

    // 受保护的资源服务器
    private static final String PROTECTED_RESOURCE_URL = "https://api.example.com/data";

    public static void main(String[] args) {
        try {
            // 1. 获取访问令牌
            String accessToken = getAccessToken();
            System.out.println("获取到的访问令牌: " + accessToken);

            if (accessToken != null && !accessToken.isEmpty()) {
                // 2. 使用访问令牌访问受保护资源
                String resourceResponse = accessProtectedResource(accessToken);
                System.out.println("受保护资源响应: " + resourceResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从 OAuth 服务器获取访问令牌
     */
    private static String getAccessToken() throws IOException {
        // 创建 HTTP 客户端
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 POST 请求到令牌端点
            HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT);

            // 设置请求参数
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new BasicNameValuePair("client_id", CLIENT_ID));
            params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
            if (SCOPE != null && !SCOPE.isEmpty()) {
                params.add(new BasicNameValuePair("scope", SCOPE));
            }

            // 设置请求实体
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    System.out.println("令牌端点响应: " + responseBody);

                    // 解析 JSON 响应，提取 access_token
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    return jsonNode.get("access_token").asText();
                }
            }
        }
        return null;
    }

    /**
     * 使用访问令牌访问受保护的资源
     */
    private static String accessProtectedResource(String accessToken) throws IOException {
        // 创建 HTTP 客户端
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 GET 请求到受保护资源
            HttpGet httpGet = new HttpGet(PROTECTED_RESOURCE_URL);

            // 在请求头中添加访问令牌
            httpGet.setHeader("Authorization", "Bearer " + accessToken);

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            }
        }
        return null;
    }
}

