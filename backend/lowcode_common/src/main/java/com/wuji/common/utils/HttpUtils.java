package com.wuji.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.wuji.common.enums.HttpClientStatusLineEnum;
import com.wuji.common.enums.MethodEnum;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.request.ApiBasicAuthRequest;
import com.wuji.common.model.request.ApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpUtils {
    private static final String UTF_8 = "UTF-8";

    public static String post(String url, Object requestJsonBody) {
        try {
            final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            HttpClient httpClient = httpClientBuilder.build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(JSONObject.toJSONString(requestJsonBody), UTF_8));
            httpPost.addHeader("Content-Type", "application/json");
            // 发送post请求
            HttpResponse response = httpClient.execute(httpPost);
            final StatusLine statusLine = response.getStatusLine();
            final HttpClientStatusLineEnum httpClientStatusLineEnum =
                    HttpClientStatusLineEnum.getByCode(statusLine.getStatusCode());
            if (!httpClientStatusLineEnum.equals(HttpClientStatusLineEnum.OK)) {
                throw new BizException(httpClientStatusLineEnum.getMessage());
            }
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error("post 请求失败", e);
        }
        return null;
    }

    public static String apiRequest(ApiRequest apiRequest) {
        try {
            if (apiRequest.getMethod() == MethodEnum.GET) {
                return get(apiRequest);
            } else if (apiRequest.getMethod() == MethodEnum.POST) {
                return post(apiRequest);
            }
            throw new ServiceException("该请求方式不支持");
        } catch (Exception e) {
            log.error("请求接口失败", e);
            throw new BizException(e.getMessage());
        }
    }

    private static String get(ApiRequest apiRequest) throws IOException {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // dataAuth认证
        dataAuth(httpClientBuilder, apiRequest.getBasicAuth());

        // 创建HttpClient对象
        HttpClient httpClient = httpClientBuilder.build();

        final JSONObject queryJson = apiRequest.getQueryJson();
        // 构建queryString
        String params = buildQueryString(queryJson);
        // 创建HttpGet对象，指定要调用的GET接口的URL
        HttpGet httpGet = new HttpGet(apiRequest.getUrl() + params);

        // 构建header参数
        buildHeader(apiRequest, httpGet);

        // 发送GET请求
        HttpResponse response = httpClient.execute(httpGet);

        // 获取响应内容
        return checkAndReturn(apiRequest, response);
    }

    private static void dataAuth(HttpClientBuilder httpClientBuilder, ApiBasicAuthRequest basicAuth) {
        if (ObjectUtils.isEmpty(basicAuth)) {
            return;
        }
        // 创建 CredentialsProvider 并设置用户名和密码
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(basicAuth.getUsername(), basicAuth.getPassword()));
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    }

    private static String checkAndReturn(ApiRequest apiRequest, HttpResponse response) {
        final StatusLine statusLine = response.getStatusLine();

        final HttpClientStatusLineEnum httpClientStatusLineEnum =
                HttpClientStatusLineEnum.getByCode(statusLine.getStatusCode());
        if (!httpClientStatusLineEnum.equals(HttpClientStatusLineEnum.OK)) {
            throw new BizException(httpClientStatusLineEnum.getMessage());
        }
        try {
            HttpEntity entity = response.getEntity();
            final String returnValue = EntityUtils.toString(entity);
            if (StringUtils.isNotEmpty(apiRequest.getJsonpath())) {
                return JsonPath.read(returnValue, apiRequest.getJsonpath());
            }
            return returnValue;
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

    }

    private static String buildQueryString(JSONObject queryJson) throws UnsupportedEncodingException {
        List<String> paramList = new ArrayList<>();
        if (queryJson != null) {
            for (String key : queryJson.keySet()) {
                paramList.add(key + "=" + URLEncoder.encode(queryJson.getString(key), UTF_8));
            }
        }
        String params = "";
        if (CollectionUtils.isNotEmpty(paramList)) {
            params = "?" + StringUtils.join(paramList, "&");
        }
        return params;
    }

    private static String post(ApiRequest apiRequest) throws IOException {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // dataAuth认证
        dataAuth(httpClientBuilder, apiRequest.getBasicAuth());

        // 创建HttpClient对象
        HttpClient httpClient = httpClientBuilder.build();

        // 创建HttpPost对象，指定要调用的Post接口的URL
        HttpPost httpPost = new HttpPost(apiRequest.getUrl());

        // 构建header参数
        buildHeader(apiRequest, httpPost);

        // 构建body
        buildBody(apiRequest, httpPost);

        // 发送post请求
        HttpResponse response = httpClient.execute(httpPost);

        // 判断是否需要根据jsonpath进行提取，并返回结果
        return checkAndReturn(apiRequest, response);
    }

    private static void buildBody(ApiRequest apiRequest, HttpPost httpPost) {
        final JSONObject requestJsonBody = apiRequest.getRequestJsonBody();
        if (requestJsonBody != null) {
            switch (apiRequest.getContentTypeEnum()) {
                case JSON:
                    httpPost.setEntity(new StringEntity(JSONObject.toJSONString(requestJsonBody), UTF_8));
                    break;
                case FORM_DATA:
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    // 添加普通表单字段
                    for (String key : requestJsonBody.keySet()) {
                        builder.addTextBody(key, requestJsonBody.getString(key), ContentType.TEXT_PLAIN);
                    }
                    httpPost.setEntity(builder.build());
                    break;
                case FORM_URLENCODED:
                    List<NameValuePair> params = new ArrayList<>();
                    for (String key : requestJsonBody.keySet()) {
                        params.add(new BasicNameValuePair(key, requestJsonBody.getString(key)));
                    }
                    String formData = URLEncodedUtils.format(params, StandardCharsets.UTF_8);
                    // 5. 设置请求体和 Content-Type 头
                    HttpEntity requestEntity = new StringEntity(formData,
                            ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8));
                    httpPost.setEntity(requestEntity);
                    break;
                default:
                    break;
            }
        }
        final String requestStringBody = apiRequest.getRequestStringBody();
        if (requestStringBody != null) {
            switch (apiRequest.getContentTypeEnum()) {
                case XML:
                    httpPost.setEntity(new StringEntity(requestStringBody, UTF_8));
                    break;
                case TEXT_PLAIN:
                    httpPost.setEntity(new StringEntity(requestStringBody, ContentType.TEXT_PLAIN));
                    break;
                default:
                    break;
            }
        }
    }

    private static void buildHeader(ApiRequest apiRequest, HttpRequestBase httpRequestBase) {
        if (apiRequest.getHeaderJson() != null) {
            for (String key : apiRequest.getHeaderJson().keySet()) {
                httpRequestBase.addHeader(key, apiRequest.getHeaderJson().getString(key));
            }
        }
        if (apiRequest.getContentTypeEnum() != null) {
            httpRequestBase.addHeader("Content-Type", apiRequest.getContentTypeEnum().getContentType());
        }
    }
}
