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
 * @since 2024-12-02
 */
@Getter
@Setter
@TableName("lc_application_user_sort")
@ApiModel(value = "ApplicationUserSortEntity对象", description = "")
public class ApplicationUserSortEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String userId;

    private Long companyId;

    private String applicationId;

    private Integer sort;
}
