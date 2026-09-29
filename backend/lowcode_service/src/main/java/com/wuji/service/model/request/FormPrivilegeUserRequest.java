package com.wuji.service.model.request;

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
 * @since 2024-10-15
 */
@Getter
@Setter
@TableName("lc_form_privilege_user")
@ApiModel(value = "FormPrivilegeUserEntity对象", description = "")
public class FormPrivilegeUserRequest {

    private String groupId;

    private String businessId;

    private String businessType;
}
