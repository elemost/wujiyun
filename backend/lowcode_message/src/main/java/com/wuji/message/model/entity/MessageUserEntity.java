package com.wuji.message.model.entity;

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
 *
 * </p>
 *
 * @author hzm
 * @since 2025-04-30
 */
@Getter
@Setter
@TableName("lc_message_user")
@ApiModel(value = "MessageUserEntity对象", description = "")
public class MessageUserEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    @ApiModelProperty("消息id")
    private Long messageId;

    @ApiModelProperty("是否查看")
    private Boolean view;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("查看时间")
    private Date viewTime;
}
