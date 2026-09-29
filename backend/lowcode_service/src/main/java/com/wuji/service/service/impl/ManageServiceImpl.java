package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractManageConverter;
import com.wuji.service.enums.ManageKeyEnum;
import com.wuji.service.mapper.ManageMapper;
import com.wuji.service.model.entity.ManageEntity;
import com.wuji.service.model.request.ManageCreateRequest;
import com.wuji.service.model.request.ManageUpdateRequest;
import com.wuji.service.model.request.MangeListRequest;
import com.wuji.service.model.vo.ManageApplicationVO;
import com.wuji.service.model.vo.ManageUserVO;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ManageApplicationService;
import com.wuji.service.service.ManageService;
import com.wuji.service.service.ManageUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-08
 */
@Service
public class ManageServiceImpl extends ServiceImpl<ManageMapper, ManageEntity> implements ManageService {

    @Autowired
    private ManageMapper manageMapper;

    @Autowired
    private ManageUserService manageUserService;

    @Autowired
    private ManageApplicationService manageApplicationService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    public void create(ManageCreateRequest manageCreateRequest) {
        ManageEntity manageEntity = AbstractManageConverter.INSTANCE.toEntity(manageCreateRequest);
        manageEntity.setId(ObjectId.getGuid());
        manageEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        manageEntity.setCreator(UserUtils.getUser().getNickName());
        manageEntity.setModifier(UserUtils.getUser().getNickName());
        manageMapper.insert(manageEntity);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
    }

    @Override
    public void update(ManageUpdateRequest manageUpdateRequest) {
        ManageEntity manageEntity = AbstractManageConverter.INSTANCE.toEntity(manageUpdateRequest);
        manageEntity.setModifier(UserUtils.getUser().getNickName());
        manageMapper.updateById(manageEntity);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
    }

    @Override
    public List<ManageVO> manageList(MangeListRequest mangeListRequest) {
        LambdaQueryWrapper<ManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtils.isNotEmpty(mangeListRequest.getApplicationIdList())) {
            List<String> groupIdList =
                    manageApplicationService.getByApplicationIdList(mangeListRequest.getApplicationIdList());
            if (CollectionUtils.isEmpty(groupIdList)) {
                return new ArrayList<>();
            }
            queryWrapper.in(ManageEntity::getId, groupIdList);
        }
        if (CollectionUtils.isNotEmpty(mangeListRequest.getUserIdList())) {
            List<String> groupIdList = manageUserService.getByUserIdList(mangeListRequest.getUserIdList());
            if (CollectionUtils.isEmpty(groupIdList)) {
                return new ArrayList<>();
            }
            queryWrapper.in(ManageEntity::getId, groupIdList);
        }
        queryWrapper.eq(ManageEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.in(CollectionUtils.isNotEmpty(mangeListRequest.getGroupIdList()), ManageEntity::getId,
                mangeListRequest.getGroupIdList());
        queryWrapper.eq(ManageEntity::getDeleted, Boolean.FALSE);
        List<ManageEntity> manageEntityList = manageMapper.selectList(queryWrapper);
        return manageEntityList.stream().map(AbstractManageConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<ManageVO> info(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<ManageEntity> manageEntityList = manageMapper.selectBatchIds(idList);
        List<ManageVO> manageVOList =
                manageEntityList.stream().map(AbstractManageConverter.INSTANCE::toVO).collect(Collectors.toList());
        List<ManageUserVO> manageUserList = manageUserService.getByGroupIds(idList);
        Map<String, List<ManageUserVO>> groupIdToUserMap =
                manageUserList.stream().collect(Collectors.groupingBy(ManageUserVO::getGroupId));
        List<ManageApplicationVO> manageApplicationVOList = manageApplicationService.getByGroupIds(idList);
        Map<String, List<ManageApplicationVO>> groupIdToApplicationMap =
                manageApplicationVOList.stream().collect(Collectors.groupingBy(ManageApplicationVO::getGroupId));
        for (ManageVO manageVO : manageVOList) {
            manageVO.setManageUserList(groupIdToUserMap.get(manageVO.getId()));
            manageVO.setManageApplicationList(groupIdToApplicationMap.get(manageVO.getId()));
        }
        return manageVOList;
    }

    @Override
    public void delete(String id) {
        LambdaQueryWrapper<ManageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageEntity::getId, id);
        ManageEntity manageEntity = new ManageEntity();
        manageEntity.setDeleted(Boolean.TRUE);
        manageEntity.setModifier(UserUtils.getUser().getNickName());
        manageMapper.update(manageEntity, queryWrapper);
        manageUserService.delete(id);
        manageApplicationService.delete(id);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
    }

    @Override
    public ManageVO currentUserInfo() {
        ManageUserVO currentUserGroup = manageUserService.getCurrentUserGroup();
        ManageVO manageVO = new ManageVO();
        if (UserUtils.getUser() != null && UserUtils.getUser().getAdminUser()) {
            manageVO.setSuperManage(Boolean.TRUE);
            manageVO.setPrivilegeKeys(
                    Arrays.stream(ManageKeyEnum.values()).map(Enum::name).collect(Collectors.toList()));
            return manageVO;
        }
        if (currentUserGroup == null) {
            return null;
        }
        String groupId = currentUserGroup.getGroupId();
        if (Constants.SUPER_MANAGE.equals(groupId)) {
            manageVO.setId(Constants.SUPER_MANAGE);
            manageVO.setSuperManage(Boolean.TRUE);
            manageVO.setPrivilegeKeys(
                    Arrays.stream(ManageKeyEnum.values()).map(Enum::name).collect(Collectors.toList()));
            return manageVO;
        }
        List<ManageVO> info = info(Collections.singletonList(groupId));
        if (CollectionUtils.isEmpty(info)) {
            return null;
        }
        manageVO = info.get(0);
        List<String> privilege = new ArrayList<>();
        if (manageVO.getDeptManage()) {
            privilege.add(ManageKeyEnum.INTERNAL_DEPT.name());
        }
        if (manageVO.getRoleRead()) {
            privilege.add(ManageKeyEnum.INTERNAL_ROLE_READ.name());
        }
        if (manageVO.getRoleWrite()) {
            privilege.add(ManageKeyEnum.INTERNAL_ROLE_WRITE.name());
        }
        if (manageVO.getCorpCoopManage()) {
            privilege.add(ManageKeyEnum.EXTERNAL.name());
        }
        manageVO.setPrivilegeKeys(privilege);
        return manageVO;
    }

    @Override
    public Map<Long, ManageVO> userManegeMap(Long companyId) {
        Map<Long, ManageVO> userIdToMap = new HashMap<>();
        Long admin = userCompanyService.getAdmin(companyId);
        ManageVO adminManage = new ManageVO();
        adminManage.setSuperManage(Boolean.TRUE);
        userIdToMap.put(admin, adminManage);
        List<ManageUserVO> manageUserVOS = manageUserService.allUser();
        if (CollectionUtils.isEmpty(manageUserVOS)) {
            return userIdToMap;
        }
        Map<String, ManageVO> groupIdToMap =
                info(manageUserVOS.stream().map(ManageUserVO::getGroupId).collect(Collectors.toList())).stream()
                        .collect(Collectors.toMap(ManageVO::getId, c -> c));
        for (ManageUserVO manageUserVO : manageUserVOS) {
            ManageVO manageVO = groupIdToMap.get(manageUserVO.getGroupId());
            if (Constants.SUPER_MANAGE.equals(manageUserVO.getGroupId())) {
                manageVO = new ManageVO();
                manageVO.setId(manageUserVO.getGroupId());
                manageVO.setSuperManage(Boolean.TRUE);
                userIdToMap.put(manageUserVO.getUserId(), manageVO);
                continue;
            } else {
                if (manageVO == null) {
                    continue;
                }
            }
            userIdToMap.put(manageUserVO.getUserId(), manageVO);
        }
        return userIdToMap;
    }
}
