package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormPrivilegeUserConverter;
import com.wuji.service.enums.PrivilegeBusinessType;
import com.wuji.service.mapper.FormPrivilegeUserMapper;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.entity.FormPrivilegeUserEntity;
import com.wuji.service.model.request.FormPrivilegeGroupSaveRequest;
import com.wuji.service.model.request.FormPrivilegeUserRequest;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;
import com.wuji.service.service.FormPrivilegeUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-10-15
 */
@Service
public class FormPrivilegeUserServiceImpl extends ServiceImpl<FormPrivilegeUserMapper, FormPrivilegeUserEntity>
        implements FormPrivilegeUserService {

    @Autowired
    private FormPrivilegeUserMapper formPrivilegeUserMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public void save(String applicationId, String categoryId, String groupId,
                     List<FormPrivilegeUserRequest> formPrivilegeUserRequestList) {
        LambdaQueryWrapper<FormPrivilegeUserEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FormPrivilegeUserEntity::getGroupId, groupId);
        deleteWrapper.eq(FormPrivilegeUserEntity::getApplicationId, applicationId);
        formPrivilegeUserMapper.delete(deleteWrapper);
        if (CollectionUtils.isEmpty(formPrivilegeUserRequestList)) {
            return;
        }
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = new ArrayList<>();
        for (FormPrivilegeUserRequest formPrivilegeUserRequest : formPrivilegeUserRequestList) {
            FormPrivilegeUserEntity formPrivilegeUserEntity =
                    AbstractFormPrivilegeUserConverter.INSTANCE.toEntity(formPrivilegeUserRequest);
            formPrivilegeUserEntity.setCategoryId(categoryId);
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setGroupId(groupId);
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        }
        saveBatch(formPrivilegeUserEntityList);
    }

    @Override
    public List<FormPrivilegeUserVO> getByGroupIdList(List<String> groupIdList) {
        if (CollectionUtils.isEmpty(groupIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormPrivilegeUserEntity::getGroupId, groupIdList);
        queryWrapper.eq(FormPrivilegeUserEntity::getDeleted, Boolean.FALSE);
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = formPrivilegeUserMapper.selectList(queryWrapper);
        List<Long> deptIdList = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>();
        List<Long> postIdList = new ArrayList<>();
        for (FormPrivilegeUserEntity formPrivilegeUserEntity : formPrivilegeUserEntityList) {
            if (StringUtils.isEmpty(formPrivilegeUserEntity.getBusinessId())) {
                continue;
            }
            if (PrivilegeBusinessType.DEPT.getType().equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                deptIdList.add(Long.valueOf(formPrivilegeUserEntity.getBusinessId()));
            } else if (PrivilegeBusinessType.USER.getType()
                    .equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                userIdList.add(Long.valueOf(formPrivilegeUserEntity.getBusinessId()));
            } else if (PrivilegeBusinessType.POST.getType()
                    .equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                postIdList.add(Long.valueOf(formPrivilegeUserEntity.getBusinessId()));
            }
        }
        List<FormPrivilegeUserVO> formPrivilegeUserVOList = new ArrayList<>();
        Map<Long, String> idToNameMap = userService.getIdToNameMap(userIdList);
        Map<Long, String> deptedIdToMap = departmentService.deptIdToMap(deptIdList);
        Map<Long, String> postIdMap = postService.getListByIdList(postIdList);
        for (FormPrivilegeUserEntity formPrivilegeUserEntity : formPrivilegeUserEntityList) {
            FormPrivilegeUserVO formPrivilegeUserVO =
                    AbstractFormPrivilegeUserConverter.INSTANCE.toVO(formPrivilegeUserEntity);
            if (PrivilegeBusinessType.DEPT.getType().equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                formPrivilegeUserVO.setBusinessName(
                        deptedIdToMap.get(Long.valueOf(formPrivilegeUserEntity.getBusinessId())));
            } else if (PrivilegeBusinessType.USER.getType()
                    .equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                formPrivilegeUserVO.setBusinessName(
                        idToNameMap.get(Long.valueOf(formPrivilegeUserEntity.getBusinessId())));
            } else if (PrivilegeBusinessType.POST.getType()
                    .equalsIgnoreCase(formPrivilegeUserEntity.getBusinessType())) {
                formPrivilegeUserVO.setBusinessName(
                        postIdMap.get(Long.valueOf(formPrivilegeUserEntity.getBusinessId())));
            }
            formPrivilegeUserVOList.add(formPrivilegeUserVO);
        }
        return formPrivilegeUserVOList;
    }

    @Override
    public void deleted(String groupId) {
        LambdaQueryWrapper<FormPrivilegeUserEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FormPrivilegeUserEntity::getGroupId, groupId);
        FormPrivilegeUserEntity formPrivilegeUserEntity = new FormPrivilegeUserEntity();
        formPrivilegeUserEntity.setDeleted(Boolean.TRUE);
        formPrivilegeUserMapper.update(formPrivilegeUserEntity, deleteWrapper);
    }

    @Override
    public List<FormPrivilegeUserVO> getUserPrivilegeList(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormPrivilegeUserEntity::getApplicationId, applicationIdList);
        queryWrapper.eq(FormPrivilegeUserEntity::getDeleted, Boolean.FALSE);
        addUserQuery(user, queryWrapper);

        return formPrivilegeUserMapper.selectList(queryWrapper).stream()
                .map(AbstractFormPrivilegeUserConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    private static void addUserQuery(UserDomain user, LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper) {
        queryWrapper.and(c -> c.and(d -> d.eq(FormPrivilegeUserEntity::getBusinessId, user.getUserId())
                        .eq(FormPrivilegeUserEntity::getBusinessType, PrivilegeBusinessType.USER.getType()))
                .or(CollectionUtils.isNotEmpty(user.getDataScopeDeptIdList()),
                        e -> e.in(FormPrivilegeUserEntity::getBusinessId, user.getDataScopeDeptIdList())
                                .eq(FormPrivilegeUserEntity::getBusinessType, PrivilegeBusinessType.DEPT.getType()))
                .or(CollectionUtils.isNotEmpty(user.getPostIdList()),
                        e -> e.in(FormPrivilegeUserEntity::getBusinessId, user.getPostIdList())
                                .eq(FormPrivilegeUserEntity::getBusinessType, PrivilegeBusinessType.POST.getType())));
    }

    @Override
    public List<FormPrivilegeUserVO> getUserPrivilegeListByCategory(List<String> categoryIdList, String applicationId) {
        if (CollectionUtils.isEmpty(categoryIdList)) {
            return new ArrayList<>();
        }
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormPrivilegeUserEntity::getCategoryId, categoryIdList);
        queryWrapper.eq(FormPrivilegeUserEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormPrivilegeUserEntity::getDeleted, Boolean.FALSE);
        addUserQuery(user, queryWrapper);

        return formPrivilegeUserMapper.selectList(queryWrapper).stream()
                .map(AbstractFormPrivilegeUserConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void useTemplate(String applicationId, Map<String, String> groupIdMap,
                            List<TemplateFormPrivilegeUserVO> templateFormPrivilegeUserList) {
        if (CollectionUtils.isEmpty(templateFormPrivilegeUserList)) {
            return;
        }
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = new ArrayList<>();
        for (TemplateFormPrivilegeUserVO templateFormPrivilegeUserVO : templateFormPrivilegeUserList) {
            FormPrivilegeUserEntity formPrivilegeUserEntity =
                    AbstractFormPrivilegeUserConverter.INSTANCE.toEntity(templateFormPrivilegeUserVO);
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setGroupId(groupIdMap.get(templateFormPrivilegeUserVO.getGroupId()));
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        }
        saveBatch(formPrivilegeUserEntityList);
    }

    @Override
    public void copyUserScope(String applicationId, String sourceApplicationId, Map<String, String> privilegeMap, String categoryId) {
        Collection<String> privilegeIds = privilegeMap.keySet();
        LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormPrivilegeUserEntity::getGroupId, privilegeIds);
        queryWrapper.eq(FormPrivilegeUserEntity::getApplicationId, sourceApplicationId);
        queryWrapper.eq(FormPrivilegeUserEntity::getDeleted, Boolean.FALSE);
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = formPrivilegeUserMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formPrivilegeUserEntityList)) {
            return;
        }
        for (FormPrivilegeUserEntity formPrivilegeUserEntity : formPrivilegeUserEntityList) {
            formPrivilegeUserEntity.setGroupId(privilegeMap.get(formPrivilegeUserEntity.getGroupId()));
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setId(null);
            if (categoryId != null) {
                formPrivilegeUserEntity.setCategoryId(categoryId);
            }
        }
        saveBatch(formPrivilegeUserEntityList);
    }

    @Override
    public void useTemplatePrivilege(String applicationId, Map<String, String> categoryToPrivilegeMap,
                                     List<FormPrivilegeEntity> formPrivilegeEntityList) {
        if (CollectionUtils.isEmpty(formPrivilegeEntityList)) {
            return;
        }
        if (categoryToPrivilegeMap.isEmpty()) {
            return;
        }
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = new ArrayList<>();
        categoryToPrivilegeMap.forEach((key, value) -> {
            FormPrivilegeUserEntity formPrivilegeUserEntity = new FormPrivilegeUserEntity();
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setCategoryId(key);
            formPrivilegeUserEntity.setGroupId(value);
            formPrivilegeUserEntity.setBusinessType("user");
            formPrivilegeUserEntity.setBusinessId(UserUtils.getUser().getUserId());
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        });
        saveBatch(formPrivilegeUserEntityList);
    }

    @Override
    public void saveDefault(String groupId, String categoryId, String applicationId) {
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = new ArrayList<>();
        Long admin = userService.getAdmin();
        if (admin != null) {
            FormPrivilegeUserEntity formPrivilegeUserEntity = new FormPrivilegeUserEntity();
            formPrivilegeUserEntity.setGroupId(groupId);
            formPrivilegeUserEntity.setCategoryId(categoryId);
            formPrivilegeUserEntity.setBusinessId(admin.toString());
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setBusinessType(PrivilegeBusinessType.USER.getType());
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        }
        if (admin == null || !admin.toString().equals(UserUtils.getUser().getUserId())) {
            FormPrivilegeUserEntity formPrivilegeUserEntity = new FormPrivilegeUserEntity();
            formPrivilegeUserEntity.setGroupId(groupId);
            formPrivilegeUserEntity.setCategoryId(categoryId);
            formPrivilegeUserEntity.setBusinessId(UserUtils.getUser().getUserId());
            formPrivilegeUserEntity.setApplicationId(applicationId);
            formPrivilegeUserEntity.setBusinessType(PrivilegeBusinessType.USER.getType());
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        }
        saveBatch(formPrivilegeUserEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePrivilegeByBusinessId(String businessId, String businessType,
                                          List<FormPrivilegeGroupSaveRequest> groupIds) {
        LambdaQueryWrapper<FormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormPrivilegeUserEntity::getBusinessId, businessId);
        queryWrapper.eq(FormPrivilegeUserEntity::getBusinessType, businessType);
        formPrivilegeUserMapper.delete(queryWrapper);
        if (CollectionUtils.isEmpty(groupIds)) {
            return;
        }
        List<FormPrivilegeUserEntity> formPrivilegeUserEntityList = new ArrayList<>();
        for (FormPrivilegeGroupSaveRequest formPrivilegeGroupSaveRequest : groupIds) {
            FormPrivilegeUserEntity formPrivilegeUserEntity = new FormPrivilegeUserEntity();
            formPrivilegeUserEntity.setGroupId(formPrivilegeGroupSaveRequest.getGroupId());
            formPrivilegeUserEntity.setCategoryId(formPrivilegeGroupSaveRequest.getCategoryId());
            formPrivilegeUserEntity.setBusinessId(businessId);
            formPrivilegeUserEntity.setApplicationId(formPrivilegeGroupSaveRequest.getApplicationId());
            formPrivilegeUserEntity.setBusinessType(businessType);
            formPrivilegeUserEntityList.add(formPrivilegeUserEntity);
        }
        saveBatch(formPrivilegeUserEntityList);
    }
}
