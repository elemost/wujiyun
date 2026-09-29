package com.wuji.service.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
@Getter
@Setter
public class TemplateApplicationImgRequest {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String applicationId;

    private String imgUrl;
}
