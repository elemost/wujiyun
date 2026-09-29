package com.wuji.open.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.DepartmentMapper;
import com.wuji.admin.model.entity.DepartmentEntity;
import com.wuji.admin.service.DepartmentService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.open.converter.AbstractDeptOpenConverter;
import com.wuji.open.model.request.DeptOpenCreateRequest;
import com.wuji.open.model.request.DeptOpenUpdateRequest;
import com.wuji.open.model.vo.DeptOpenTreeVO;
import com.wuji.open.service.DeptOpenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptOpenServiceImpl implements DeptOpenService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<DeptOpenTreeVO> tree() {
        List<DepartmentVO> departmentVOList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), "00");
        List<DepartmentVO> parentList = departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                .collect(Collectors.toList());
        return parentList.stream().map(AbstractDeptOpenConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public Long create(DeptOpenCreateRequest deptOpenCreateRequest) {
        checkSameName(null, deptOpenCreateRequest.getDeptName(), deptOpenCreateRequest.getParentId(), "00");
        UserDomain user = UserUtils.getUser();
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setCreateTime(new Date());
        departmentEntity.setUpdateTime(new Date());
        departmentEntity.setCompanyId(user.getCompanyId());
        departmentEntity.setDeptType("00");
        departmentEntity.setDeptName(deptOpenCreateRequest.getDeptName());
        departmentEntity.setOrderNum(deptOpenCreateRequest.getOrderNum());
        departmentEntity.setStatus(Constants.NORMAL);
        departmentEntity.setDelFlag(Constants.NORMAL);
        departmentMapper.insert(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
        return departmentEntity.getDeptId();
    }

    @Override
    public Long update(DeptOpenUpdateRequest deptOpenUpdateRequest) {
        checkSameName(deptOpenUpdateRequest.getDeptId(), deptOpenUpdateRequest.getDeptName(),
                deptOpenUpdateRequest.getParentId(), "00");
        UserDomain user = UserUtils.getUser();
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setDeptId(deptOpenUpdateRequest.getDeptId());
        departmentEntity.setCreateTime(new Date());
        departmentEntity.setUpdateTime(new Date());
        departmentEntity.setCompanyId(user.getCompanyId());
        departmentEntity.setDeptType("00");
        departmentEntity.setDeptName(deptOpenUpdateRequest.getDeptName());
        departmentEntity.setOrderNum(deptOpenUpdateRequest.getOrderNum());
        departmentMapper.updateById(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
        return departmentEntity.getDeptId();
    }

    @Override
    public void delete(Long id) {
        departmentService.delete(id);
    }

    private void checkSameName(Long deptId, String deptName, Long parentId, String deptType) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(deptId != null, DepartmentEntity::getDeptId, deptId);
        queryWrapper.eq(DepartmentEntity::getDeptName, deptName);
        queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(DepartmentEntity::getParentId, parentId);
        queryWrapper.eq(DepartmentEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(StringUtils.isNotEmpty(deptType), DepartmentEntity::getDeptType, deptType);
        boolean exists = departmentMapper.exists(queryWrapper);
        if (exists) {
            throw new AdminException(AdminResultCode.DEPT_NAME_EXIST);
        }
    }
}
