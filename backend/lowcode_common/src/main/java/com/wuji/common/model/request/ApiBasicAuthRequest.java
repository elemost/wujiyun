package com.wuji.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApiBasicAuthRequest {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    public static ApiBasicAuthRequest build(String password, String username) {
        ApiBasicAuthRequest apiBasicAuthRequest = new ApiBasicAuthRequest();
        apiBasicAuthRequest.setPassword(password);
        apiBasicAuthRequest.setUsername(username);
        return apiBasicAuthRequest;
    }
}
