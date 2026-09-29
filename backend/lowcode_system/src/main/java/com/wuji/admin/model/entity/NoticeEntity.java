package com.wuji.admin.model.entity;

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
 * 通知公告表
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
@Getter
@Setter
@TableName("sys_notice")
@ApiModel(value = "NoticeEntity对象", description = "通知公告表")
public class NoticeEntity {

    @ApiModelProperty("公告ID")
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer noticeId;

    @ApiModelProperty("公告标题")
    private String noticeTitle;

    @ApiModelProperty("公告类型（1通知 2公告）")
    private String noticeType;

    @ApiModelProperty("公告内容")
    private String noticeContent;

    @ApiModelProperty("公告状态（0正常 1关闭）")
    private String status;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;
}
