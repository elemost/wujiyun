package com.wuji.service.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ApplicationUserSortVO {
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
