package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@Getter
@Setter
@TableName("wj_client_function")
@ApiModel(value = "ClientFunctionEntity对象", description = "")
public class ClientFunctionEntity {

    private String clientId;

    private String permissionKey;

    private String permissionName;

    private String equityId;

    private Integer limitCount;
}
