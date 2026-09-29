package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-08-04
 */
@Getter
@Setter
@TableName("wj_city")
@ApiModel(value = "CityEntity对象", description = "")
public class CityEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer pid;

    @ApiModelProperty("编号")
    private Integer code;

    private String cityName;

    @ApiModelProperty("等级")
    private Integer type;
}
