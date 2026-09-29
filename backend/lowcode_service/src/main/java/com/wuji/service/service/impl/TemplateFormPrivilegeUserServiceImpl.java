package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractTemplateFormPrivilegeUserConverter;
import com.wuji.service.mapper.TemplateFormPrivilegeUserMapper;
import com.wuji.service.model.entity.TemplateFormPrivilegeUserEntity;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;
import com.wuji.service.service.TemplateFormPrivilegeUserService;
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
 * @since 2025-01-20
 */
@Service
public class TemplateFormPrivilegeUserServiceImpl
        extends ServiceImpl<TemplateFormPrivilegeUserMapper, TemplateFormPrivilegeUserEntity>
        implements TemplateFormPrivilegeUserService {

    @Autowired
    private TemplateFormPrivilegeUserMapper templateFormPrivilegeUserMapper;

    @Override
    public void generateTemplate(String applicationId, Map<String, String> privilegeIdMap,
                                 List<FormPrivilegeUserVO> formPrivilegeUserVOList) {

        if (CollectionUtils.isEmpty(formPrivilegeUserVOList)) {
            return;
        }
        List<TemplateFormPrivilegeUserEntity> insertList = new ArrayList<>();
        for (FormPrivilegeUserVO formPrivilegeUserVO : formPrivilegeUserVOList) {
            TemplateFormPrivilegeUserEntity templateFormPrivilegeUserEntity =
                    AbstractTemplateFormPrivilegeUserConverter.INSTANCE.toEntity(formPrivilegeUserVO);
            templateFormPrivilegeUserEntity.setApplicationId(applicationId);
            insertList.add(templateFormPrivilegeUserEntity);
        }
        saveBatch(insertList);
    }

    @Override
    public List<TemplateFormPrivilegeUserVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormPrivilegeUserEntity::getApplicationId, applicationId);
        queryWrapper.eq(TemplateFormPrivilegeUserEntity::getDeleted, Boolean.FALSE);
        List<TemplateFormPrivilegeUserEntity> templateFormPrivilegeUserEntities =
                templateFormPrivilegeUserMapper.selectList(queryWrapper);
        return templateFormPrivilegeUserEntities.stream().map(AbstractTemplateFormPrivilegeUserConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormPrivilegeUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormPrivilegeUserEntity::getApplicationId, applicationId);
        templateFormPrivilegeUserMapper.delete(queryWrapper);
    }
}
