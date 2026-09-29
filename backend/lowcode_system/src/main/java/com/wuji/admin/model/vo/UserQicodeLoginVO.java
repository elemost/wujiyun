package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQicodeLoginVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("获取二维码的凭证")
    private String ticket;

    @ApiModelProperty("生成二维码的screen")
    private String qrScene;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    private String openId;

    private String unionId;

    @ApiModelProperty("是否使用")
    private Byte used;

    @ApiModelProperty("扫码后动作")
    private String event;
}
