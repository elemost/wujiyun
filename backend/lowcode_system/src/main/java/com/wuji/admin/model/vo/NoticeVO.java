package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoticeVO {
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

    @ApiModelProperty("备注")
    private String remark;
}
