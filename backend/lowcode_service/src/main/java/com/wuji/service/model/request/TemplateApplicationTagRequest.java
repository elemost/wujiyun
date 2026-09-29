package com.wuji.service.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2025-03-03
 */
@Getter
@Setter
public class TemplateApplicationTagRequest {


    private String applicationId;

    private String tagType;

    private String tagValue;
}
