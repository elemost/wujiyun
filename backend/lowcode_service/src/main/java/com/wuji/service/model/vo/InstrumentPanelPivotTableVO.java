package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.service.model.info.FormColumn;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InstrumentPanelPivotTableVO {
    private List<FormColumn> columns;

    private List<JSONObject> dataList;

    private List<DepartmentVO> departmentList = new ArrayList<>();

    private List<UserVO> userList = new ArrayList<>();
}
