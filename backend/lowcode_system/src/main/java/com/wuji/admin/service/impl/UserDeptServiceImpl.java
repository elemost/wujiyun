package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.UserDeptMapper;
import com.wuji.admin.model.entity.UserDeptEntity;
import com.wuji.admin.model.request.UserDeptSaveRequest;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-11
 */
@Service
@DS("slave")
public class UserDeptServiceImpl extends ServiceImpl<UserDeptMapper, UserDeptEntity> implements UserDeptService {

    @Autowired
    private UserDeptMapper userDeptMapper;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void save(List<Long> deptIdList, Long userId) {
        delete(userId);
        if (CollectionUtils.isEmpty(deptIdList)) {
            return;
        }
        List<UserDeptEntity> userDeptEntityList = new ArrayList<>();
        for (Long deptId : deptIdList) {
            UserDeptEntity userDeptEntity = new UserDeptEntity();
            userDeptEntity.setDeptId(deptId);
            userDeptEntity.setUserId(userId);
            userDeptEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userDeptEntityList.add(userDeptEntity);
        }
        if (CollectionUtils.isNotEmpty(userDeptEntityList)) {
            saveBatch(userDeptEntityList);
        }
    }

    @Override
    public void delete(Long userId) {
        LambdaQueryWrapper<UserDeptEntity> delete = new LambdaQueryWrapper<>();
        delete.eq(UserDeptEntity::getUserId, userId);
        delete.eq(UserDeptEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        userDeptMapper.delete(delete);
    }

    @Override
    public void save(List<UserDeptSaveRequest> userDeptSaveRequestList, List<Long> userIdList) {
        LambdaQueryWrapper<UserDeptEntity> delete = new LambdaQueryWrapper<>();
        delete.in(UserDeptEntity::getUserId, userIdList);
        delete.eq(UserDeptEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        userDeptMapper.delete(delete);
        if (CollectionUtils.isEmpty(userDeptSaveRequestList)) {
            return;
        }
        List<UserDeptEntity> userDeptEntityList = new ArrayList<>();
        for (UserDeptSaveRequest userDeptSaveRequest : userDeptSaveRequestList) {
            UserDeptEntity userDeptEntity = new UserDeptEntity();
            userDeptEntity.setDeptId(userDeptSaveRequest.getDeptId());
            userDeptEntity.setUserId(userDeptSaveRequest.getUserId());
            userDeptEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userDeptEntityList.add(userDeptEntity);
        }
        saveBatch(userDeptEntityList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<UserDeptVO> getByUserIdList(List<Long> userIdList, Long companyId) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserDeptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserDeptEntity::getUserId, userIdList);
        if (companyId != null) {
            queryWrapper.eq(UserDeptEntity::getCompanyId, companyId);
        } else {
            queryWrapper.eq(UserDeptEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        }
        List<UserDeptEntity> userDeptEntityList = userDeptMapper.selectList(queryWrapper);
        List<Long> deptIdList = userDeptEntityList.stream().map(UserDeptEntity::getDeptId).collect(Collectors.toList());
        Map<Long, String> deptNameMap = departmentService.deptIdToMap(deptIdList);
        List<UserDeptVO> userDeptVOList = new ArrayList<>();
        for (UserDeptEntity userDeptEntity : userDeptEntityList) {
            UserDeptVO userDeptVO = new UserDeptVO();
            userDeptVO.setDeptId(userDeptEntity.getDeptId());
            userDeptVO.setUserId(userDeptEntity.getUserId());
            userDeptVO.setDeptName(deptNameMap.get(userDeptEntity.getDeptId()));
            if (StringUtils.isEmpty(userDeptVO.getDeptName())) {
                continue;
            }
            userDeptVOList.add(userDeptVO);
        }
        return userDeptVOList;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<Long> getByDeptIdList(List<Long> deptIdList) {
        if (CollectionUtils.isEmpty(deptIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<UserDeptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserDeptEntity::getDeptId, deptIdList);
        queryWrapper.eq(UserDeptEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<UserDeptEntity> userDeptEntityList = userDeptMapper.selectList(queryWrapper);
        return userDeptEntityList.stream().map(UserDeptEntity::getUserId).collect(Collectors.toList());
    }
}
