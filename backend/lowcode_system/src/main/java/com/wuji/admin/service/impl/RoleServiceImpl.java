package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractRoleConverter;
import com.wuji.admin.enums.InitRoleEnum;
import com.wuji.admin.mapper.RoleMapper;
import com.wuji.admin.model.entity.RoleEntity;
import com.wuji.admin.model.request.RoleCreateRequest;
import com.wuji.admin.model.request.RoleListRequest;
import com.wuji.admin.model.request.RoleUpdateRequest;
import com.wuji.admin.model.vo.RoleDetailVO;
import com.wuji.admin.model.vo.RoleVO;
import com.wuji.admin.service.RoleMenuService;
import com.wuji.admin.service.RoleService;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
@Service
@DS("slave")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    private static final String LOWCODE = "lowcode";

    @Override
    public void create(RoleCreateRequest roleCreateRequest) {
        final RoleEntity roleEntity = AbstractRoleConverter.INSTANCE.toEntity(roleCreateRequest);
        roleEntity.setCreateBy(UserUtils.getUser().getUserName());
        roleEntity.setUpdateBy(UserUtils.getUser().getUserName());
        roleEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        roleEntity.setSource(LOWCODE);
        roleMapper.insert(roleEntity);
        roleMenuService.save(roleCreateRequest.getMenuIdList(), roleEntity.getRoleId(), Boolean.FALSE);
    }

    @Override
    public void update(RoleUpdateRequest roleUpdateRequest) {
        final RoleEntity roleEntity = AbstractRoleConverter.INSTANCE.toEntity(roleUpdateRequest);
        roleEntity.setUpdateBy(UserUtils.getUser().getUserName());
        roleMapper.updateById(roleEntity);
        roleMenuService.save(roleUpdateRequest.getMenuIdList(), roleEntity.getRoleId(), Boolean.TRUE);
    }

    @Override
    public QueryPageVO<RoleVO> queryList(RoleListRequest roleListRequest) {
        String companyId = ConfigCache.getValue(ConfigEnum.SYSTEM_ROLE_COMPANY.name());
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(roleListRequest.getRoleKey()), RoleEntity::getRoleKey,
                roleListRequest.getRoleKey());
        queryWrapper.eq(StringUtils.isNotEmpty(roleListRequest.getStatus()), RoleEntity::getStatus,
                roleListRequest.getStatus());
        queryWrapper.like(StringUtils.isNotEmpty(roleListRequest.getRoleName()), RoleEntity::getRoleName,
                roleListRequest.getRoleName());
        queryWrapper.ge(roleListRequest.getStartTime() != null, RoleEntity::getCreateTime,
                roleListRequest.getStartTime());
        queryWrapper.le(roleListRequest.getEndTime() != null, RoleEntity::getCreateTime, roleListRequest.getEndTime());
        queryWrapper.eq(RoleEntity::getSource, LOWCODE);
        queryWrapper.eq(RoleEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.orderByAsc(RoleEntity::getRoleSort);
        if (StringUtils.isNotEmpty(companyId)) {
            queryWrapper.in(RoleEntity::getCompanyId, Lists.newArrayList(UserUtils.getUser().getCompanyId(), 0));
        } else {
            queryWrapper.eq(RoleEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        }

        final Page<RoleEntity> roleEntityPage =
                roleMapper.selectPage(new Page<>(roleListRequest.getPageNum(), roleListRequest.getPageSize()),
                        queryWrapper);
        return PageUtils.toQueryPage(roleEntityPage,
                roleEntityPage.getRecords().stream().map(AbstractRoleConverter.INSTANCE::toVO)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<RoleVO> queryByIdList(List<Long> roleIdList) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.orderByAsc(RoleEntity::getRoleSort);
        queryWrapper.in(CollectionUtils.isNotEmpty(roleIdList), RoleEntity::getRoleId, roleIdList);
        final List<RoleEntity> roleEntities = roleMapper.selectList(queryWrapper);
        return roleEntities.stream().map(AbstractRoleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public RoleDetailVO queryById(Long id) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(RoleEntity::getRoleId, id);
        final RoleEntity roleEntity = roleMapper.selectOne(queryWrapper);
        if (roleEntity == null) {
            return null;
        }
        final RoleDetailVO roleDetailVO = AbstractRoleConverter.INSTANCE.toDetailVO(roleEntity);
        final List<Long> menuIds = roleMenuService.queryByRoleIdList(Collections.singletonList(id));
        roleDetailVO.setMenuList(menuIds);
        return roleDetailVO;
    }

    @Override
    public List<RoleVO> getAllRole() {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        final List<RoleEntity> roleEntities = roleMapper.selectList(queryWrapper);
        return roleEntities.stream().map(AbstractRoleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void deleted(Long id) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleEntity::getRoleId, id);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setDelFlag(Constants.DELETED);
        roleMapper.update(roleEntity, queryWrapper);
    }

    @Override
    public void initRole() {
        final InitRoleEnum[] values = InitRoleEnum.values();
        final List<Long> roleIdList = Arrays.stream(values).map(InitRoleEnum::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        final List<RoleEntity> roleEntities = roleMapper.selectBatchIds(roleIdList);
        roleIdList.removeAll(roleEntities.stream().map(RoleEntity::getRoleId).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        List<RoleEntity> insertList = new ArrayList<>();
        for (InitRoleEnum initRoleEnum : values) {
            if (roleIdList.contains(initRoleEnum.getId())) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setRoleKey(initRoleEnum.getRoleKey());
                roleEntity.setRoleName(initRoleEnum.getRoleName());
                roleEntity.setRoleSort(initRoleEnum.getRoleSort());
                roleEntity.setStatus("0");
                insertList.add(roleEntity);
            }
        }
        saveBatch(insertList);
    }
}
