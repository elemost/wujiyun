package com.wuji.service.model.request;

import com.wuji.service.model.vo.FormPrivilegeVO;
import lombok.Data;

import java.util.List;

@Data
public class FormDataExtraParameterRequest {

    private List<FormPrivilegeVO> formPrivilegeVO;

    private Boolean extraButton;

    private String viewId;

    private String formId;

    private String groupId;

    private Boolean filterAdminPrivilege;
}
