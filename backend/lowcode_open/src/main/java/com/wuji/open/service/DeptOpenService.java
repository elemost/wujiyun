package com.wuji.open.service;

import com.wuji.open.model.request.DeptOpenCreateRequest;
import com.wuji.open.model.request.DeptOpenUpdateRequest;
import com.wuji.open.model.vo.DeptOpenTreeVO;

import java.util.List;

public interface DeptOpenService {

    List<DeptOpenTreeVO> tree();

    Long create(DeptOpenCreateRequest deptOpenCreateRequest);

    Long update(DeptOpenUpdateRequest deptOpenUpdateRequest);

    void delete(Long id);
}
