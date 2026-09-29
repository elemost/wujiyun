package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ManagePostSystemRequest extends BasePageRequest{
    private String postName;
}
