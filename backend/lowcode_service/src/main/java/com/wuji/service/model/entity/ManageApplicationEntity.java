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
 * @since 2025-08-11
 */
@Getter
@Setter
@TableName("lc_manage_application")
@ApiModel(value = "ManageApplicationEntity对象", description = "")
public class ManageApplicationEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String groupId;

    private String applicationId;

    private Long companyId;
}
