package com.wuji.workflow.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 抄送对象表
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
@Getter
@Setter
@TableName("lc_flowable_copy_user")
@ApiModel(value = "FlowableCopyUserEntity对象", description = "抄送对象表")
public class FlowableCopyUserEntity {

    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("抄送id")
    private Long copyId;

    @ApiModelProperty("用户id")
    private Long userId;

    private Boolean userView;

    private Date viewTime;
}
