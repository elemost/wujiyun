package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.constant.UserConstants;
import com.wuji.admin.converter.AbstractDepartmentConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.handler.PullDataContext;
import com.wuji.admin.mapper.DepartmentMapper;
import com.wuji.admin.model.entity.DepartmentEntity;
import com.wuji.admin.model.request.DepartmentCreateRequest;
import com.wuji.admin.model.request.DepartmentSaveOrUpdateRequest;
import com.wuji.admin.model.request.DepartmentUpdateRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
@Service
@DS("slave")
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentEntity>
        implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PullDataContext pullDataContext;

    @Autowired
    private UserCompanyService userCompanyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pullData(Long companyId) {
        LambdaUpdateWrapper<DepartmentEntity> clearThirdWrapper = new LambdaUpdateWrapper<>();
        clearThirdWrapper.eq(DepartmentEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        clearThirdWrapper.set(DepartmentEntity::getThirdId, null);
        update(clearThirdWrapper);
        pull(companyId);
        DepartmentCache.clear(companyId);
    }

    private void pull(Long companyId) {
        List<DepartmentVO> departmentVOList = queryListNow(companyId, DepartmentTypeEnum.INTERNAL_DEPT.getCode());
        Map<Long, Map<String, Long>> parentIdToDeptNameMap = departmentVOList.stream().collect(
                Collectors.groupingBy(DepartmentVO::getParentId,
                        Collectors.toMap(DepartmentVO::getDeptName, DepartmentVO::getDeptId)));
        List<DepartmentEntity> departmentEntityList = saveDepartment(companyId, 0L, "-1", parentIdToDeptNameMap);
        insertChild(departmentEntityList, parentIdToDeptNameMap);
    }

    private List<DepartmentEntity> saveDepartment(Long companyId, Long parentId, String thirdParentId,
                                                  Map<Long, Map<String, Long>> parentIdToDeptNameMap) {
        List<DepartmentEntity> departmentEntityList = new ArrayList<>();
        CompanyVO info = companyService.info(companyId);
        List<DepartmentPullVO> departmentDingVOS;
        CompanyDataSourceEnum dataSourceEnum = CompanyDataSourceEnum.valueOf(info.getDataSource());
        if (thirdParentId.equals("-1")) {
            thirdParentId = dataSourceEnum.getRootParentId();
        }
        departmentDingVOS = pullDataContext.getHandler(dataSourceEnum.name()).getDeptChildList(thirdParentId, info);
        for (DepartmentPullVO departmentDingVO : departmentDingVOS) {
            Map<String, Long> deptNameMap = parentIdToDeptNameMap.get(parentId);
            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setParentId(parentId);
            departmentEntity.setDeptName(departmentDingVO.getName());
            departmentEntity.setCreateTime(new Date());
            departmentEntity.setDeptType("00");
            departmentEntity.setUpdateTime(new Date());
            departmentEntity.setCompanyId(companyId);
            departmentEntity.setThirdId(departmentDingVO.getDeptId());
            String leader = setLeader(departmentDingVO, dataSourceEnum);
            departmentEntity.setLeader(leader);
            if (deptNameMap != null) {
                Long existId = deptNameMap.get(departmentDingVO.getName());
                if (existId != null) {
                    departmentEntity.setDeptId(existId);
                }
            }
            departmentEntityList.add(departmentEntity);
        }
        if (CollectionUtils.isNotEmpty(departmentEntityList)) {
            saveOrUpdateBatch(departmentEntityList);
        }
        return departmentEntityList;
    }

    private void insertChild(List<DepartmentEntity> departmentEntityList,
                             Map<Long, Map<String, Long>> parentIdToDeptNameMap) {
        for (DepartmentEntity departmentEntity : departmentEntityList) {
            List<DepartmentEntity> departmentEntities =
                    saveDepartment(departmentEntity.getCompanyId(), departmentEntity.getDeptId(),
                            departmentEntity.getThirdId(), parentIdToDeptNameMap);
            if (!departmentEntities.isEmpty()) {
                insertChild(departmentEntities, parentIdToDeptNameMap);
            }
        }
    }

    @Override
    public Long create(DepartmentCreateRequest departmentCreateRequest) {
        if ("1".equals(departmentCreateRequest.getDeptType())) {
            departmentCreateRequest.setDeptType("00");
        }
        checkSameName(null, departmentCreateRequest.getDeptName(), departmentCreateRequest.getParentId(),
                departmentCreateRequest.getDeptType());
        UserDomain user = UserUtils.getUser();
        DepartmentEntity departmentEntity = AbstractDepartmentConverter.INSTANCE.toEntity(departmentCreateRequest);
        departmentEntity.setCreateBy(user.getUserName());
        departmentEntity.setCreateTime(new Date());
        departmentEntity.setUpdateBy(user.getUserId());
        departmentEntity.setUpdateTime(new Date());
        departmentEntity.setCompanyId(user.getCompanyId());
        departmentMapper.insert(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
        return departmentEntity.getDeptId();
    }

    @Override
    public void update(DepartmentUpdateRequest departmentUpdateRequest) {
        if ("1".equals(departmentUpdateRequest.getDeptType())) {
            departmentUpdateRequest.setDeptType("00");
        }
        checkSameName(departmentUpdateRequest.getDeptId(), departmentUpdateRequest.getDeptName(),
                departmentUpdateRequest.getParentId(), departmentUpdateRequest.getDeptType());
        UserDomain user = UserUtils.getUser();
        DepartmentEntity departmentEntity = AbstractDepartmentConverter.INSTANCE.toEntity(departmentUpdateRequest);
        departmentEntity.setUpdateBy(user.getUserId());
        departmentEntity.setUpdateTime(new Date());
        departmentMapper.updateById(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
    }

    @Override
    public void delete(Long id) {
        UserDomain user = UserUtils.getUser();
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setDeptId(id);
        departmentEntity.setDelFlag(Constants.DELETED);
        departmentEntity.setUpdateBy(user.getUserName());
        departmentMapper.updateById(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
    }

    @Override
    public void deleteBySourceCompanyId(Long sourceCompanyId) {
        UserDomain user = UserUtils.getUser();
        LambdaUpdateWrapper<DepartmentEntity> delete = new LambdaUpdateWrapper<>();
        delete.eq(DepartmentEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        delete.eq(DepartmentEntity::getSourceCompanyId, sourceCompanyId);
        delete.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
        DepartmentEntity departmentEntity = departmentMapper.selectOne(delete);
        if (departmentEntity == null) {
            return;
        }
        departmentEntity.setDelFlag(Constants.DELETED);
        departmentEntity.setUpdateBy(user.getUserName());
        departmentMapper.updateById(departmentEntity);
        DepartmentCache.clear(user.getCompanyId());
    }

    @Override
    public void deleteByThirdDeptId(List<String> thirdDeptIdList) {
        LambdaQueryWrapper<DepartmentEntity> delete = new LambdaQueryWrapper<>();
        delete.in(DepartmentEntity::getThirdId, thirdDeptIdList);
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setDelFlag(Constants.DELETED);
        departmentMapper.update(departmentEntity, delete);
    }

    @Override
    public List<DepartmentVO> queryListByIdList(List<Long> departmentIdList) {
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DepartmentEntity::getDeptId, departmentIdList);
        queryWrapper.orderByAsc(DepartmentEntity::getOrderNum);
        final List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(queryWrapper);
        List<DepartmentVO> departmentVOList =
                departmentEntityList.stream().map(AbstractDepartmentConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        if (departmentIdList.contains(0L)) {
            CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
            if (info != null) {
                DepartmentVO departmentVO = companyAsDeptInfo(info);
                departmentVOList.add(departmentVO);
            }
        }
        return departmentVOList;
    }

    @Override
    public List<DepartmentVO> querySelectList(List<Long> departmentIdList, String deptType) {
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), deptType);
        }
        List<DepartmentVO> departmentVOS = new ArrayList<>();
        List<Long> deptIdList = new ArrayList<>();
        for (Long deptId : departmentIdList) {
            List<DepartmentVO> departmentVOList = getParentListById(deptId, deptType);
            addDept(departmentVOList, deptIdList, departmentVOS);
        }
        List<DepartmentVO> childDepartmentList =
                DepartmentCache.getChildDepartmentList(UserUtils.getUser().getCompanyId(), departmentIdList, deptType);
        addDept(childDepartmentList, deptIdList, departmentVOS);
        Map<Long, List<DepartmentVO>> parentMap =
                departmentVOS.stream().collect(Collectors.groupingBy(DepartmentVO::getParentId));

        Map<Long, DepartmentVO> deptNameMap = new HashMap<>();
        for (DepartmentVO departmentVO : departmentVOS) {
            deptNameMap.put(departmentVO.getDeptId(), departmentVO);
        }
        buildChild(departmentVOS, parentMap, deptNameMap);
        // CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        // if (info != null) {
        //     DepartmentVO departmentVO = companyAsDeptInfo(info);
        //     departmentVO.setChildren(departmentVOS.stream()
        //             .filter(c -> c.getParentId() != null && c.getParentId().toString().equals("0"))
        //             .collect(Collectors.toList()));
        //     departmentVOS.add(departmentVO);
        // }
        return departmentVOS;
    }

    @Override
    public Map<Long, String> allDeptWithDelete(Long companyId) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
        queryWrapper.orderByAsc(DepartmentEntity::getParentId);
        queryWrapper.orderByAsc(DepartmentEntity::getOrderNum);
        final List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(queryWrapper);
        Map<Long, String> collect = departmentEntityList.stream()
                .collect(Collectors.toMap(DepartmentEntity::getDeptId, DepartmentEntity::getDeptName));
        CompanyVO info = companyService.info(companyId);
        collect.put(0L, info.getCompanyName());
        return collect;
    }

    private static DepartmentVO companyAsDeptInfo(CompanyVO info) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setDeptId(0L);
        departmentVO.setFullName(info.getCompanyName());
        departmentVO.setDeptName(info.getCompanyName());
        if (StringUtils.isNotEmpty(info.getDataSource())) {
            CompanyDataSourceEnum companyDataSourceEnum = CompanyDataSourceEnum.valueOf(info.getDataSource());
            departmentVO.setThirdId(companyDataSourceEnum.getRootParentId());
        }
        departmentVO.setParentId(-99999L);
        return departmentVO;
    }

    private static void addDept(List<DepartmentVO> departmentVOList, List<Long> deptIdList,
                                List<DepartmentVO> departmentVOS) {
        for (DepartmentVO departmentVO : departmentVOList) {
            if (deptIdList.contains(departmentVO.getDeptId())) {
                continue;
            }
            departmentVOS.add(departmentVO);
            deptIdList.add(departmentVO.getDeptId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<Long, String> deptIdToMap(List<Long> departmentIdList) {
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return new HashMap<>();
        }
        List<DepartmentVO> departmentVOList = queryListByIdList(departmentIdList);
        Map<Long, String> deptIdMap =
                departmentVOList.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, DepartmentVO::getDeptName));
        if (departmentIdList.contains(0L)) {
            CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
            if (info != null) {
                deptIdMap.put(0L, info.getCompanyName());
            }
        }
        return deptIdMap;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<DepartmentVO> queryList(Long companyId, String deptType) {
        return getDepartmentVOS(companyId, deptType);
    }

    @Override
    public List<DepartmentVO> queryAllList(Long companyId, String deptType, Boolean needCompany) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
        queryWrapper.eq(StringUtils.isNotEmpty(deptType), DepartmentEntity::getDeptType, deptType);
        queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
        final List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(queryWrapper);
        List<DepartmentVO> departmentVOList =
                departmentEntityList.stream().map(AbstractDepartmentConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        if (needCompany) {
            DepartmentVO departmentVO = new DepartmentVO();
            departmentVO.setDeptId(0L);
            CompanyVO info = companyService.info(companyId);
            departmentVO.setDeptName(info.getCompanyName());
            departmentVOList.add(departmentVO);
        }
        return departmentVOList;
    }

    private List<DepartmentVO> getDepartmentVOS(Long companyId, String deptType) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
        queryWrapper.eq(StringUtils.isNotEmpty(deptType), DepartmentEntity::getDeptType, deptType);
        queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.orderByAsc(DepartmentEntity::getParentId);
        queryWrapper.orderByAsc(DepartmentEntity::getOrderNum);
        final List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(queryWrapper);
        final List<DepartmentVO> departmentVOS =
                departmentEntityList.stream().map(AbstractDepartmentConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        Map<Long, List<DepartmentVO>> parentMap =
                departmentVOS.stream().collect(Collectors.groupingBy(DepartmentVO::getParentId));
        Map<Long, DepartmentVO> deptNameMap =
                departmentVOS.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, c -> c));
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setDeptId(0L);
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        if (info != null) {
            departmentVO.setFullName(info.getCompanyName());
            departmentVO.setDeptName(info.getCompanyName());
            if (StringUtils.isNotEmpty(info.getDataSource())) {
                CompanyDataSourceEnum companyDataSourceEnum = CompanyDataSourceEnum.valueOf(info.getDataSource());
                departmentVO.setThirdId(companyDataSourceEnum.getRootParentId());
            }
            departmentVO.setChildren(parentMap.get(0L));
            departmentVO.setParentId(-99999L);
        }
        departmentVOS.add(departmentVO);
        buildChild(departmentVOS, parentMap, deptNameMap);
        return departmentVOS;
    }

    @Override
    public List<DepartmentVO> queryListNow(Long companyId, String deptType) {
        return getDepartmentVOS(companyId, deptType);
    }

    @Override
    public List<DepartmentVO> getParentListById(Long id, String deptType) {
        if (id == null) {
            return new ArrayList<>();
        }
        List<DepartmentVO> departmentEntityList = new ArrayList<>();
        List<DepartmentVO> departmentVOList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), deptType);
        Map<Long, DepartmentVO> deptIdToMap =
                departmentVOList.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, c -> c));
        DepartmentVO departmentVO = deptIdToMap.get(id);
        departmentEntityList.add(departmentVO);
        if (departmentVO == null) {
            return new ArrayList<>();
        }
        Long parentId = departmentVO.getParentId();
        while (parentId != -99999L) {
            departmentVO = deptIdToMap.get(parentId);
            parentId = departmentVO.getParentId();
            departmentEntityList.add(departmentVO);
        }
        return departmentEntityList;
    }

    @Override
    public void importDeptName(List<String> deptNameList, String deptType) {
        if (CollectionUtils.isEmpty(deptNameList)) {
            return;
        }
        List<DepartmentVO> departmentVOList = queryListNow(UserUtils.getUser().getCompanyId(), deptType);
        Map<Long, Map<String, Long>> parentIdToDeptNameMap = departmentVOList.stream().collect(
                Collectors.groupingBy(DepartmentVO::getParentId,
                        Collectors.toMap(DepartmentVO::getFullName, DepartmentVO::getDeptId)));
        for (String deptName : deptNameList) {
            String[] userDeptList = deptName.split(",");
            for (String userDept : userDeptList) {
                String[] deptNames = userDept.split("/");
                Long parentId = 0L;
                List<String> dealDeptNames = new ArrayList<>();
                for (String dept : deptNames) {
                    dealDeptNames.add(dept);
                    String fullPath = StringUtils.join(dealDeptNames, "/");
                    Map<String, Long> deptNameMap = parentIdToDeptNameMap.get(parentId);
                    if (deptNameMap == null) {
                        deptNameMap = new HashMap<>();
                    }
                    Long deptId = deptNameMap.get(fullPath);
                    if (deptId == null) {
                        DepartmentCreateRequest departmentCreateRequest = new DepartmentCreateRequest();
                        departmentCreateRequest.setDeptName(dept);
                        departmentCreateRequest.setParentId(parentId);
                        departmentCreateRequest.setOrderNum(0);
                        departmentCreateRequest.setDeptType(deptType);
                        deptId = create(departmentCreateRequest);
                        deptNameMap.put(fullPath, deptId);
                        parentIdToDeptNameMap.put(parentId, deptNameMap);
                    }
                    parentId = deptId;
                }
            }
        }

    }

    @Override
    public void saveOrUpdate(List<String> deptIdList, Long companyId) {
        CompanyVO info = companyService.info(companyId);
        CompanyDataSourceEnum dataSourceEnum = CompanyDataSourceEnum.valueOf(info.getDataSource());
        List<DepartmentSaveOrUpdateRequest> departmentCreateRequests = new ArrayList<>();
        for (String deptId : deptIdList) {
            // 使用钉钉id查找是否存在
            DepartmentEntity departmentEntity = getDeptByThirdId(companyId, deptId);
            DepartmentPullVO departmentPullVO = null;
            departmentPullVO = pullDataContext.getHandler(dataSourceEnum.name()).getDeptById(deptId, info);
            // 当parentId为1时说明新增的部门为根目录上
            if (!Objects.equals(departmentPullVO.getParentId(), dataSourceEnum.getRootParentId())) {
                DepartmentPullVO child = departmentPullVO;
                add(child, departmentCreateRequests, dataSourceEnum);
                if (departmentEntity != null) {
                    departmentCreateRequests.get(0).setDeptId(departmentEntity.getDeptId());
                }
                DepartmentEntity parent = null;
                // 循环查找他的父级
                while (parent == null && !Objects.equals(child.getParentId(), dataSourceEnum.getRootParentId())) {
                    LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(DepartmentEntity::getThirdId, child.getParentId());
                    queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
                    queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
                    parent = departmentMapper.selectOne(queryWrapper);
                    if (parent == null) {
                        child = pullDataContext.getHandler(dataSourceEnum.name())
                                .getDeptById(child.getParentId(), info);
                        add(child, departmentCreateRequests, dataSourceEnum);
                        // 钉钉部门id为1时说明已经到达根目录
                        if (Objects.equals(child.getParentId(), dataSourceEnum.getRootParentId())) {
                            departmentCreateRequests.get(0).setParentId(0L);
                        }
                    } else {
                        if (!Objects.equals(child.getParentId(), dataSourceEnum.getRootParentId())) {
                            departmentCreateRequests.get(0).setParentId(parent.getDeptId());
                        } else {
                            departmentCreateRequests.get(0).setParentId(0L);
                        }
                    }
                }
            } else {
                add(departmentPullVO, departmentCreateRequests, dataSourceEnum);
                if (departmentEntity != null) {
                    departmentCreateRequests.get(0).setDeptId(departmentEntity.getDeptId());
                }
                departmentCreateRequests.get(0).setParentId(0L);
            }
        }
        saveOrUpdateList(departmentCreateRequests, companyId);
    }

    @Override
    public Map<String, Long> checkAndCreateDept(List<String> deptNameList, Map<String, Long> deptNameToCompanyIdMap) {
        if (deptNameToCompanyIdMap.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DepartmentEntity::getSourceCompanyId, deptNameToCompanyIdMap.values());
        queryWrapper.eq(DepartmentEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(DepartmentEntity::getDelFlag, UserConstants.NORMAL);
        List<DepartmentEntity> departmentEntityList = departmentMapper.selectList(queryWrapper);
        Map<Long, DepartmentEntity> sourceCompanyMap =
                departmentEntityList.stream().collect(Collectors.toMap(DepartmentEntity::getSourceCompanyId, c -> c));
        List<DepartmentEntity> updateOrInsertList = new ArrayList<>();
        deptNameToCompanyIdMap.forEach((key, value) -> {
            DepartmentEntity departmentEntity = sourceCompanyMap.get(value);
            if (departmentEntity == null) {
                DepartmentEntity saveDept = new DepartmentEntity();
                saveDept.setStatus(UserConstants.NORMAL);
                saveDept.setParentId(0L);
                saveDept.setDeptName(key);
                saveDept.setCompanyId(UserUtils.getUser().getCompanyId());
                saveDept.setCreateTime(new Date());
                saveDept.setDeptType("10");
                saveDept.setUpdateTime(new Date());
                saveDept.setSourceCompanyId(value);
                updateOrInsertList.add(saveDept);
            } else {
                departmentEntity.setDeptName(key);
                departmentEntity.setUpdateTime(new Date());
                updateOrInsertList.add(departmentEntity);
            }
        });
        if (CollectionUtils.isNotEmpty(updateOrInsertList)) {
            saveOrUpdateBatch(updateOrInsertList);
        }
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
        return updateOrInsertList.stream()
                .collect(Collectors.toMap(DepartmentEntity::getDeptName, DepartmentEntity::getDeptId));
    }

    private DepartmentEntity getDeptByThirdId(Long companyId, String deptId) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentEntity::getThirdId, deptId);
        queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
        queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
        return departmentMapper.selectOne(queryWrapper);
    }


    private void saveOrUpdateList(List<DepartmentSaveOrUpdateRequest> departmentCreateRequests, Long companyId) {
        Long parentId = departmentCreateRequests.get(0).getParentId();
        for (DepartmentSaveOrUpdateRequest departmentSaveOrUpdateRequest : departmentCreateRequests) {
            if (departmentSaveOrUpdateRequest.getDeptId() != null) {
                parentId = saveDept(companyId, departmentSaveOrUpdateRequest, parentId);
                continue;
            }
            LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DepartmentEntity::getParentId, parentId);
            queryWrapper.eq(DepartmentEntity::getDeptName, departmentSaveOrUpdateRequest.getDeptName());
            queryWrapper.eq(DepartmentEntity::getCompanyId, companyId);
            queryWrapper.eq(DepartmentEntity::getDelFlag, Constants.NU_DELETED);
            DepartmentEntity departmentEntity = departmentMapper.selectOne(queryWrapper);
            if (departmentEntity != null) {
                departmentEntity.setDeptName(departmentSaveOrUpdateRequest.getDeptName());
                departmentEntity.setParentId(parentId);
                departmentEntity.setThirdId(departmentSaveOrUpdateRequest.getThirdId());
                departmentMapper.updateById(departmentEntity);
                parentId = departmentEntity.getDeptId();
            } else {
                parentId = saveDept(companyId, departmentSaveOrUpdateRequest, parentId);
            }
        }
        DepartmentCache.clear(UserUtils.getUser().getCompanyId());
    }

    private Long saveDept(Long companyId, DepartmentSaveOrUpdateRequest departmentSaveOrUpdateRequest, Long parentId) {
        DepartmentEntity departmentEntity =
                AbstractDepartmentConverter.INSTANCE.toEntity(departmentSaveOrUpdateRequest);
        departmentEntity.setParentId(parentId);
        departmentEntity.setCreateTime(new Date());
        departmentEntity.setUpdateTime(new Date());
        departmentEntity.setCompanyId(companyId);
        saveOrUpdate(departmentEntity);
        parentId = departmentEntity.getDeptId();
        return parentId;
    }

    private void add(DepartmentPullVO child, List<DepartmentSaveOrUpdateRequest> departmentCreateRequests,
                     CompanyDataSourceEnum dataSourceEnum) {
        DepartmentSaveOrUpdateRequest departmentCreateRequest = new DepartmentSaveOrUpdateRequest();
        departmentCreateRequest.setDeptName(child.getName());
        departmentCreateRequest.setOrderNum(0);
        departmentCreateRequest.setDeptType(DepartmentTypeEnum.INTERNAL_DEPT.getCode());
        departmentCreateRequest.setThirdId(child.getDeptId());
        String leader = setLeader(child, dataSourceEnum);
        departmentCreateRequest.setLeader(leader);
        departmentCreateRequests.add(0, departmentCreateRequest);
    }

    private String setLeader(DepartmentPullVO child, CompanyDataSourceEnum dataSourceEnum) {
        if (CollectionUtils.isNotEmpty(child.getLeaderList())) {
            List<UserCompanyVO> userCompanyVOS = new ArrayList<>();
            if (dataSourceEnum == CompanyDataSourceEnum.LARK) {
                userCompanyVOS = userCompanyService.getByLarkUserId(child.getLeaderList());
            } else if (dataSourceEnum == CompanyDataSourceEnum.DING_TALK) {
                userCompanyVOS = userCompanyService.getByLarkUserId(child.getLeaderList());
            }
            return userCompanyVOS.stream().map(c -> String.valueOf(c.getUserId())).collect(Collectors.joining(","));
        }
        return "";
    }

    private void buildChild(List<DepartmentVO> departmentVOS, Map<Long, List<DepartmentVO>> parentMap,
                            Map<Long, DepartmentVO> deptNameMap) {
        for (DepartmentVO departmentVO : departmentVOS) {
            departmentVO.setChildren(parentMap.get(departmentVO.getDeptId()));
            DepartmentVO parentVO = deptNameMap.get(departmentVO.getParentId());
            if (parentVO == null) {
                departmentVO.setFullName(departmentVO.getDeptName());
            } else {
                departmentVO.setFullName(parentVO.getFullName() + "/" + departmentVO.getDeptName());
            }
        }
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
