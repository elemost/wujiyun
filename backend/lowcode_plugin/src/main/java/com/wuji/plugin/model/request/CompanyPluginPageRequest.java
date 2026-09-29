package com.wuji.plugin.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyPluginPageRequest extends BasePageRequest {

    private String pluginName;

}
