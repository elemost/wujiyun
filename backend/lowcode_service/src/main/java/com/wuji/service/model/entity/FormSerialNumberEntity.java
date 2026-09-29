package com.wuji.service.model.entity;

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
 * @since 2025-03-01
 */
@Getter
@Setter
@TableName("lc_form_serial_number")
@ApiModel(value = "FormSerialNumberEntity对象", description = "")
public class FormSerialNumberEntity {

    private String serialKey;

    private String dateTime;

    private Integer serialNumber;
}
