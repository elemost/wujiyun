package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.CompanyRelationMapper;
import com.wuji.admin.model.entity.CompanyRelationEntity;
import com.wuji.admin.model.request.CompanyRelationSaveRequest;
import com.wuji.admin.model.request.ContactPersonSaveRequest;
import com.wuji.admin.model.vo.ContactPersonVO;
import com.wuji.admin.service.CompanyRelationService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
@Service
@DS("slave")
public class CompanyRelationServiceImpl extends ServiceImpl<CompanyRelationMapper, CompanyRelationEntity>
        implements CompanyRelationService {

    @Autowired
    private CompanyRelationMapper companyRelationMapper;

    @Autowired
    private UserService userService;

    @Override
    public void saveRelation(CompanyRelationSaveRequest companyRelationSaveRequest) {
        CompanyRelationEntity companyRelationEntity = new CompanyRelationEntity();
        companyRelationEntity.setCompanyId(companyRelationSaveRequest.getCompanyId());
        companyRelationEntity.setRelatedCompany(companyRelationSaveRequest.getRelatedCompany());
        companyRelationEntity.setRelatedType(companyRelationSaveRequest.getRelatedType());
        save(companyRelationEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContactPerson(ContactPersonSaveRequest contactPersonSaveRequest) {
        LambdaQueryWrapper<CompanyRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyRelationEntity::getRelatedType, "CONTACT_PERSON");
        queryWrapper.eq(CompanyRelationEntity::getRelatedCompany, contactPersonSaveRequest.getCompanyId());
        queryWrapper.eq(CompanyRelationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        companyRelationMapper.delete(queryWrapper);
        List<CompanyRelationEntity> companyRelationEntityList = new ArrayList<>();
        for (Long userId : contactPersonSaveRequest.getUserIdList()) {
            CompanyRelationEntity companyRelationEntity = new CompanyRelationEntity();
            companyRelationEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            companyRelationEntity.setUserId(userId);
            companyRelationEntity.setRelatedType("CONTACT_PERSON");
            companyRelationEntity.setRelatedCompany(contactPersonSaveRequest.getCompanyId());
            companyRelationEntityList.add(companyRelationEntity);
        }
        if (CollectionUtils.isNotEmpty(contactPersonSaveRequest.getUserIdList())) {
            saveBatch(companyRelationEntityList);
        }
    }

    @Override
    public ContactPersonVO getContactPerson(Long companyId) {
        LambdaQueryWrapper<CompanyRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyRelationEntity::getRelatedType, "CONTACT_PERSON");
        queryWrapper.eq(CompanyRelationEntity::getRelatedCompany, companyId);
        queryWrapper.eq(CompanyRelationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<CompanyRelationEntity> companyRelationEntityList = companyRelationMapper.selectList(queryWrapper);
        List<Long> userIdList =
                companyRelationEntityList.stream().map(CompanyRelationEntity::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ContactPersonVO();
        }
        List<UserVO> userVOS = userService.queryByIds(userIdList);
        ContactPersonVO contactPersonVO = new ContactPersonVO();
        contactPersonVO.setUserVOList(userVOS);
        return contactPersonVO;
    }

    @Override
    public List<Long> getCurrentUserRelationCompany() {
        LambdaQueryWrapper<CompanyRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyRelationEntity::getRelatedType, "CONTACT_PERSON");
        queryWrapper.eq(CompanyRelationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(CompanyRelationEntity::getUserId, UserUtils.getUser().getUserId());
        List<CompanyRelationEntity> companyRelationEntityList = companyRelationMapper.selectList(queryWrapper);
        return companyRelationEntityList.stream().map(CompanyRelationEntity::getRelatedCompany)
                .collect(Collectors.toList());
    }
}
