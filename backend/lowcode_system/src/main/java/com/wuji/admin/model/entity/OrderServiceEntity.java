package com.wuji.admin.model.entity;

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
 * @since 2026-06-10
 */
@Getter
@Setter
@TableName("wj_order_service")
@ApiModel(value = "OrderServiceEntity对象", description = "")
public class OrderServiceEntity {

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String serviceCode;

    private Integer days;
}
