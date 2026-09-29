package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class FormPrivilegeUserEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String groupId;

    private String businessId;

    private String businessType;

    private String applicationId;

    private String categoryId;

    private Boolean deleted;
}
