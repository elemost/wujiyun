package com.wuji.service.model.vo;

import com.wuji.service.model.request.MongoGroupLookUpRequest;
import lombok.Data;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@Data
public class SearchFilterVO {
    private Criteria search;

    private List<MongoGroupLookUpRequest> mongoGroupLookUpRequestList;

    private List<FormPrivilegeVO> formPrivilegeList;
}
