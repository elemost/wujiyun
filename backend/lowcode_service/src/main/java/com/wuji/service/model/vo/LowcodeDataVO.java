package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wuji.common.model.info.FormDept;
import com.wuji.service.model.info.FormExtraFunctionButton;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LowcodeDataVO {

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
    private Date createTime;

    private String createTimeString;

    @ApiModelProperty("修改时间")
    private Date modifyTime;

    private String modifyTimeString;

    @ApiModelProperty("创建人")
    private String creator;

    private String creatorName;

    @ApiModelProperty("修改人")
    private String modifier;

    private String processInstanceId;
    /**
     * 任务名称
     */
    private String taskName;


    private String status;

    private String statusName;

    private String processStatus;

    private List<FormDept> deptList;

    private List<String> operatePivilegeList;

    private List<String> viewPivilegeList = new ArrayList<>();

    private List<FormExtraFunctionButton> buttonList;

    private String dataTitle;

    private String modelId;

    private List<FormPendingTaskVO> tasks;

    private Map<String, String> lockMap;

    private String ruleDesc;

    private List<LowcodeDataVO> children;
}
