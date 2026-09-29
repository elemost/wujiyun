package com.wuji.service.model.entity;

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
 * @since 2026-02-11
 */
@Getter
@Setter
@TableName("lc_form_data_factory_input")
@ApiModel(value = "FormDataFactoryInputEntity对象", description = "")
public class FormDataFactoryInputEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected Long id;

    private String dataFactoryId;

    private String applicationId;

    private String inputFormId;

    private String inputApplicationId;

    private Integer version;
}
