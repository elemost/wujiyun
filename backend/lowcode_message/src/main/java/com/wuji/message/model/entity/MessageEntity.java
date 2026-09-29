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
 * @since 2025-04-29
 */
@Getter
@Setter
@TableName("lc_message")
@ApiModel(value = "MessageEntity对象", description = "")
public class MessageEntity  {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private Long companyId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("消息类型")
    private String messageType;

    @ApiModelProperty("创建人")
    private String creator;
    /**
     * 创建时间
     */
    protected Date createTime;

    private Boolean deleted;

    private String sendType;
}
