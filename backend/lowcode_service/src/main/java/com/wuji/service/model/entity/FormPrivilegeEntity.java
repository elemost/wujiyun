package com.wuji.service.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.enums.FormPrivilegeDataScopeTypeEnum;
import com.wuji.service.enums.FormPrivilegeUserPrivilegeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@TableName("lc_form_privilege")
@ApiModel(value = "FormPrivilegeEntity对象", description = "")
public class FormPrivilegeEntity extends BaseUuidEntity {

    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("字段权限")
    private String fieldPrivilege;

    @ApiModelProperty("操作字段权限")
    private String operateFieldPrivilege;

    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("操作权限")
    private String operatePrivilege;

    @ApiModelProperty("查看权限")
    private String viewPrivilege;

    /**
     * @see FormPrivilegeDataScopeTypeEnum
     */
    @ApiModelProperty("数据范围")
    private String dataScope;

    /**
     * @see FormPrivilegeUserPrivilegeEnum
     */
    private String userPrivilege;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    private Integer sort;

    private String privilegeConfig;

    public static FormPrivilegeEntity getFormDefaultConfig() {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setUserPrivilege("CUSTOM");
        formPrivilegeEntity.setGroupName("默认权限");
        formPrivilegeEntity.setFieldPrivilege("[]");
        formPrivilegeEntity.setViewPrivilege("[\"VIEW\",\"EDIT\",\"DELETE\",\"EXPORT\",\"IMPORT\",\"DETAIL\"]");
        formPrivilegeEntity.setOperatePrivilege("[\"SUBMIT\",\"SAVE\"]");
        formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
        formPrivilegeEntity.setOperateFieldPrivilege("[]");
        return formPrivilegeEntity;
    }

    public static FormPrivilegeEntity getViewDefaultConfig() {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setUserPrivilege("CUSTOM");
        formPrivilegeEntity.setGroupName("默认权限");
        formPrivilegeEntity.setFieldPrivilege("[]");
        formPrivilegeEntity.setViewPrivilege("[\"VIEW\",\"EDIT\",\"DELETE\",\"EXPORT\",\"IMPORT\",\"DETAIL\"]");
        formPrivilegeEntity.setOperatePrivilege("[\"SUBMIT\",\"SAVE\"]");
        formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
        formPrivilegeEntity.setOperateFieldPrivilege("[]");
        return formPrivilegeEntity;
    }

    public static FormPrivilegeEntity getFlowableFormDefaultConfig() {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setUserPrivilege("CUSTOM");
        formPrivilegeEntity.setGroupName("默认权限");
        formPrivilegeEntity.setFieldPrivilege("[]");
        formPrivilegeEntity.setViewPrivilege("[\"VIEW\",\"EDIT\",\"DELETE\",\"SUB_DATA\",\"EXPORT\",\"IMPORT\",\"DETAIL\"]");
        formPrivilegeEntity.setOperatePrivilege("[\"SUBMIT\",\"SAVE\"]");
        formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
        formPrivilegeEntity.setOperateFieldPrivilege("[]");
        return formPrivilegeEntity;
    }

    public static FormPrivilegeEntity getDashboardFormDefaultConfig() {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setUserPrivilege("CUSTOM");
        formPrivilegeEntity.setGroupName("默认权限");
        return formPrivilegeEntity;
    }


    public static FormPrivilegeEntity getAdminDefaultConfig() {
        FormPrivilegeEntity formPrivilegeEntity = new FormPrivilegeEntity();
        formPrivilegeEntity.setId(ObjectId.getGuid());
        formPrivilegeEntity.setGroupName("超级管理员权限组");
        formPrivilegeEntity.setFieldPrivilege("[]");
        formPrivilegeEntity.setViewPrivilege("[\"VIEW\",\"EDIT\",\"DELETE\",\"EXPORT\",\"IMPORT\",\"DETAIL\"]");
        formPrivilegeEntity.setOperatePrivilege("[\"SUBMIT\",\"SAVE\"]");
        formPrivilegeEntity.setDataScope("[{\"dataScopeType\":\"ALL\"}]");
        formPrivilegeEntity.setOperateFieldPrivilege("[]");
        return formPrivilegeEntity;
    }

}
