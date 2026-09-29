package com.wuji.workflow.model.entity;

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
 * @since 2024-09-24
 */
@Getter
@Setter
@TableName("lc_flowable_config")
@ApiModel(value = "FlowableConfigEntity对象", description = "")
public class FlowableConfigEntity {
    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    private String modelId;

    private String config;
}
