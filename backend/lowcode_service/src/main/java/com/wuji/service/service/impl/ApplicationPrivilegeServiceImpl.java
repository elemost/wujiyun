package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.model.info.UserScope;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationPrivilegeConverter;
import com.wuji.service.enums.ApplicationPrivilegeTypeEnum;
import com.wuji.service.enums.PrivilegeBusinessType;
import com.wuji.service.mapper.ApplicationPrivilegeMapper;
import com.wuji.service.model.entity.ApplicationPrivilegeEntity;
import com.wuji.service.model.request.ApplicationPrivilegeSaveRequest;
import com.wuji.service.model.vo.ApplicationPrivilegeVO;
import com.wuji.service.service.ApplicationPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * @since 2024-10-14
 */
@Service
public class ApplicationPrivilegeServiceImpl extends ServiceImpl<ApplicationPrivilegeMapper, ApplicationPrivilegeEntity>
        implements ApplicationPrivilegeService {

    @Autowired
    private ApplicationPrivilegeMapper applicationPrivilegeMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public void save(String applicationId, String privilegeType,
                     List<ApplicationPrivilegeSaveRequest> applicationPrivilegeSaveList) {
        LambdaQueryWrapper<ApplicationPrivilegeEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ApplicationPrivilegeEntity::getApplicationId, applicationId);
        deleteWrapper.eq(ApplicationPrivilegeEntity::getPrivilegeType, privilegeType);
        applicationPrivilegeMapper.delete(deleteWrapper);
        if (CollectionUtils.isEmpty(applicationPrivilegeSaveList)) {
            return;
        }
        List<ApplicationPrivilegeEntity> applicationPrivilegeEntityList = new ArrayList<>();
        for (ApplicationPrivilegeSaveRequest applicationPrivilegeSaveRequest : applicationPrivilegeSaveList) {
            ApplicationPrivilegeEntity applicationPrivilegeEntity =
                    AbstractApplicationPrivilegeConverter.INSTANCE.toEntity(applicationPrivilegeSaveRequest);
            applicationPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            applicationPrivilegeEntity.setApplicationId(applicationId);
            applicationPrivilegeEntity.setPrivilegeType(privilegeType);
            applicationPrivilegeEntityList.add(applicationPrivilegeEntity);
        }
        saveBatch(applicationPrivilegeEntityList);
    }

    @Override
    public List<ApplicationPrivilegeVO> getPriviegeList(String applicationId) {
        LambdaQueryWrapper<ApplicationPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationPrivilegeEntity::getApplicationId, applicationId);
        List<ApplicationPrivilegeEntity> applicationPrivilegeEntityList =
                applicationPrivilegeMapper.selectList(queryWrapper);
        List<Long> deptIdList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        for (ApplicationPrivilegeEntity applicationPrivilegeEntity : applicationPrivilegeEntityList) {
            if (PrivilegeBusinessType.DEPT.getType().equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                deptIdList.add(Long.valueOf(applicationPrivilegeEntity.getBusinessId()));
            } else if (PrivilegeBusinessType.USER.getType()
                    .equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                userIdList.add(Long.valueOf(applicationPrivilegeEntity.getBusinessId()));
            } else if (PrivilegeBusinessType.POST.getType()
                    .equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                postIdList.add(Long.valueOf(applicationPrivilegeEntity.getBusinessId()));
            }
        }
        List<ApplicationPrivilegeVO> applicationPrivilegeVOList = new ArrayList<>();
        Map<Long, String> idToNameMap = userService.getIdToNameMap(userIdList);
        Map<Long, String> deptIdToMap = departmentService.deptIdToMap(deptIdList);
        Map<Long, String> postIdMap = postService.getListByIdList(postIdList);
        for (ApplicationPrivilegeEntity applicationPrivilegeEntity : applicationPrivilegeEntityList) {
            ApplicationPrivilegeVO applicationPrivilegeVO =
                    AbstractApplicationPrivilegeConverter.INSTANCE.toVO(applicationPrivilegeEntity);
            if (PrivilegeBusinessType.DEPT.getType().equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                applicationPrivilegeVO.setBusinessName(
                        deptIdToMap.get(Long.valueOf(applicationPrivilegeEntity.getBusinessId())));
            } else if (PrivilegeBusinessType.USER.getType()
                    .equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                applicationPrivilegeVO.setBusinessName(
                        idToNameMap.get(Long.valueOf(applicationPrivilegeEntity.getBusinessId())));
            } else if (PrivilegeBusinessType.POST.getType()
                    .equalsIgnoreCase(applicationPrivilegeEntity.getBusinessType())) {
                applicationPrivilegeVO.setBusinessName(
                        postIdMap.get(Long.valueOf(applicationPrivilegeEntity.getBusinessId())));
            }
            applicationPrivilegeVOList.add(applicationPrivilegeVO);
        }
        return applicationPrivilegeVOList;
    }

    @Override
    public void saveDefaultPrivilege(String applicationId) {
        ApplicationPrivilegeEntity applicationPrivilegeEntity = new ApplicationPrivilegeEntity();
        applicationPrivilegeEntity.setApplicationId(applicationId);
        applicationPrivilegeEntity.setBusinessId("0");
        applicationPrivilegeEntity.setBusinessType(PrivilegeBusinessType.DEPT.getType());
        applicationPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
        applicationPrivilegeEntity.setPrivilegeType(ApplicationPrivilegeTypeEnum.VIEW.name());
        applicationPrivilegeMapper.insert(applicationPrivilegeEntity);
    }

    @Override
    public void copy(String applicationId, String sourceApplicationId) {
        LambdaQueryWrapper<ApplicationPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationPrivilegeEntity::getApplicationId, sourceApplicationId);
        List<ApplicationPrivilegeEntity> applicationPrivilegeEntityList =
                applicationPrivilegeMapper.selectList(queryWrapper);
        for (ApplicationPrivilegeEntity applicationPrivilegeEntity : applicationPrivilegeEntityList) {
            applicationPrivilegeEntity.setId(null);
            applicationPrivilegeEntity.setApplicationId(applicationId);
        }
        saveBatch(applicationPrivilegeEntityList);
    }

    @Override
    public List<Long> getScope(String applicationId) {
        LambdaQueryWrapper<ApplicationPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationPrivilegeEntity::getApplicationId, applicationId);
        List<ApplicationPrivilegeEntity> applicationPrivilegeEntityList =
                applicationPrivilegeMapper.selectList(queryWrapper);
        List<UserScope> userScopeList =
                applicationPrivilegeEntityList.stream().map(AbstractApplicationPrivilegeConverter.INSTANCE::toScope)
                        .collect(Collectors.toList());
        return userService.getUserByScope(userScopeList);
    }
}
