package com.wuji.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hzm
 * @since 2024-12-25
 */
@Getter
@Setter
@TableName("act_hi_identitylink")
@ApiModel(value = "HiIdentitylinkEntity对象", description = "")
public class HiIdentitylinkEntity {

    // /**
    //  * 主键自增id
    //  */
    // @TableField(value = "ID_")
    // private String id;

    @TableField(value = "GROUP_ID_")
    private String groupId;

    @TableField(value = "TYPE_")
    private String type;

    @TableField(value = "USER_ID_")
    private String userId;

    @TableField(value = "TASK_ID_")
    private String taskId;

    @TableField(value = "CREATE_TIME_")
    private Date createTime;

    @TableField(value = "PROC_INST_ID_")
    private String procInstId;

    @TableField(value = "SCOPE_ID_")
    private String scopeId;

    @TableField(value = "SUB_SCOPE_ID_")
    private String subScopeId;

    @TableField(value = "SCOPE_TYPE_")
    private String scopeType;

    @TableField(value = "SCOPE_DEFINITION_ID_")
    private String scopeDefinitionId;
}
