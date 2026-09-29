package com.wuji.plugin.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wuji.common.model.entity.BaseUuidEntity;
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
 * @since 2025-09-28
 */
@Getter
@Setter
@TableName("lc_company_plugin")
@ApiModel(value = "CompanyPluginEntity对象", description = "")
public class CompanyPluginEntity extends BaseUuidEntity {

    private Long companyId;

    private String pluginConfig;

    private String pluginType;

    private String pluginName;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("插件参数")
    private String pluginParam;

    private String pluginReturn;

    private String functionType;
}
