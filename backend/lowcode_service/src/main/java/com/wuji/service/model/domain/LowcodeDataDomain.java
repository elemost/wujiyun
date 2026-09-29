package com.wuji.service.model.domain;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.info.FormDept;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class LowcodeDataDomain {

    private String uuid;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("具体参数")
    private JSONObject instValue;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("来源表单id")
    private String sourceFormId;

    @ApiModelProperty("当前表单id")
    private String formId;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("应用类型")
    private String appType;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long modifyTime;


    private Object creator;

    private Object modifier;

    private List<FormDept> deptList;
    // @ApiModelProperty("创建人")
    // private String creator;
    //
    // @ApiModelProperty("修改人")
    // private String modifier;

    private String processStatus;

    private String processInstanceId;

    private String status;

    private String modelId;

    private ParentInfoMongodbDomain parentInfo;

    private String dataTitle;

    private String publicUserSign;

}
