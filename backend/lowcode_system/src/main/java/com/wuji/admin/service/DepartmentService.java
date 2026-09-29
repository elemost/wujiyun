package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.DepartmentEntity;
import com.wuji.admin.model.request.DepartmentCreateRequest;
import com.wuji.admin.model.request.DepartmentUpdateRequest;
import com.wuji.common.model.vo.DepartmentVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
public interface DepartmentService extends IService<DepartmentEntity> {
    void pullData(Long companyId);

    /**
     * 创建部门
     *
     * @param departmentCreateRequest
     * @return
     */
    Long create(DepartmentCreateRequest departmentCreateRequest);

    /**
     * 修改部门
     *
     * @param departmentUpdateRequest
     */
    void update(DepartmentUpdateRequest departmentUpdateRequest);

    /**
     * 删除部门
     *
     * @param id
     */
    void delete(Long id);

    void deleteBySourceCompanyId(Long sourceCompanyId);

    void deleteByThirdDeptId(List<String> thirdDeptIdList);

    List<DepartmentVO> queryListByIdList(List<Long> departmentIdList);

    List<DepartmentVO> querySelectList(List<Long> departmentIdList, String deptType);

    Map<Long, String> allDeptWithDelete(Long companyId);

    Map<Long, String> deptIdToMap(List<Long> departmentIdList);

    List<DepartmentVO> queryList(Long companyId, String deptType);

    List<DepartmentVO> queryAllList(Long companyId, String deptType, Boolean needCompany);

    List<DepartmentVO> queryListNow(Long companyId, String deptType);

    List<DepartmentVO> getParentListById(Long id, String deptType);

    void importDeptName(List<String> deptNameList, String deptType);

    void saveOrUpdate(List<String> deptIdList, Long companyId);

    Map<String, Long> checkAndCreateDept(List<String> deptNameList, Map<String, Long> deptNameToCompanyIdMap);
}
