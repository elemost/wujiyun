package com.wuji.message.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MessageDomain {
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
    private Date createTime;

    private Boolean view;

    private Date viewTime;
}
