package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepartmentListResult extends WeComResult {
    private List<WeComDepartmentVO> department;
}
