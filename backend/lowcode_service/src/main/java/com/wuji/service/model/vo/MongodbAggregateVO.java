package com.wuji.service.model.vo;

import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.service.model.info.MongodbAggregateData;
import com.wuji.service.model.info.MongodbSearchField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MongodbAggregateVO {
    private MongodbAggregateData data;

    private List<MongodbSearchField> fieldyList;

    private List<MongodbSearchField> fieldxList;

    private List<MongodbSearchField> metricList;

    private List<DepartmentVO> departmentList = new ArrayList<>();

    private List<UserVO> userList = new ArrayList<>();

    private Integer dataSize;

    private Integer metricCount;
}
