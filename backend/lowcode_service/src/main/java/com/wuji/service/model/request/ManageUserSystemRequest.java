package com.wuji.service.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManageUserSystemRequest extends BasePageRequest {
    private String userType;

    private String id;

    private List<Long> departmentIdList;

    private Long departmentId;

    private List<Long> postIdList;

    private String postId;

    private List<String> idList;

}
