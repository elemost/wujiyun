package com.wuji.admin.model.request;


import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;

import java.util.List;

@Data
public class UserSelectRequest extends BasePageRequest {
    private List<String> idList;

    private String id;

    private List<Long> departmentIdList;

    private Long departmentId;

    private List<Long> postIdList;

    private String postId;

    private String userType;

    @CorpCoop
    private String companyUuid;

    private List<Long> sourceCompanyId;

    private String status;

    private List<Long> departmentIdManageList;

    private List<Long> postIdManageList;

    private List<String> idManageList;
}
