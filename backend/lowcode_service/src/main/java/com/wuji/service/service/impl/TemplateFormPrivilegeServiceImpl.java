package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractTemplateFormPrivilegeConverter;
import com.wuji.service.mapper.TemplateFormPrivilegeMapper;
import com.wuji.service.model.entity.TemplateFormPrivilegeEntity;
import com.wuji.service.model.vo.FormPrivilegeDetailVO;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.TemplateFormPrivilegeService;
import com.wuji.service.service.TemplateFormPrivilegeUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * @since 2025-01-20
 */
@Service
public class TemplateFormPrivilegeServiceImpl
        extends ServiceImpl<TemplateFormPrivilegeMapper, TemplateFormPrivilegeEntity>
        implements TemplateFormPrivilegeService {

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private TemplateFormPrivilegeUserService templateFormPrivilegeUserService;

    @Autowired
    private TemplateFormPrivilegeMapper templateFormPrivilegeMapper;

    @Override
    public Map<String, String> generateTemplate(String applicationId, String templateApplicationId,
                                                List<String> formIdList, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormPrivilegeEntity::getApplicationId, templateApplicationId);
            templateFormPrivilegeMapper.delete(queryWrapper);
            templateFormPrivilegeUserService.deleteByApplicationId(templateApplicationId);
        }
        List<FormPrivilegeDetailVO> formPrivilegeDetailVOS = formPrivilegeService.getByFormId(applicationId, formIdList);
        List<TemplateFormPrivilegeEntity> templateFormPrivilegeEntityList = new ArrayList<>();
        Map<String, String> privilegeIdMap = new HashMap<>();
        List<FormPrivilegeUserVO> formPrivilegeUserList = new ArrayList<>();
        for (FormPrivilegeDetailVO formPrivilegeDetailVO : formPrivilegeDetailVOS) {
            TemplateFormPrivilegeEntity templateFormPrivilegeEntity =
                    AbstractTemplateFormPrivilegeConverter.INSTANCE.toEntity(formPrivilegeDetailVO);
            String guid = ObjectId.getGuid();
            privilegeIdMap.put(formPrivilegeDetailVO.getId(), guid);
            templateFormPrivilegeEntity.setId(guid);
            templateFormPrivilegeEntity.setApplicationId(templateApplicationId);
            templateFormPrivilegeEntity.setCreatorName(UserUtils.getUser().getNickName());
            templateFormPrivilegeEntity.setModifierName(UserUtils.getUser().getNickName());
            templateFormPrivilegeEntityList.add(templateFormPrivilegeEntity);
            formPrivilegeUserList.addAll(formPrivilegeDetailVO.getFormPrivilegeUserList());
        }
        saveBatch(templateFormPrivilegeEntityList);
        templateFormPrivilegeUserService.generateTemplate(templateApplicationId, privilegeIdMap, formPrivilegeUserList);
        return privilegeIdMap;
    }

    @Override
    public List<TemplateFormPrivilegeVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<TemplateFormPrivilegeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateFormPrivilegeEntity::getApplicationId, applicationId);
        queryWrapper.eq(TemplateFormPrivilegeEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(TemplateFormPrivilegeEntity::getSort);
        queryWrapper.orderByAsc(TemplateFormPrivilegeEntity::getId);
        List<TemplateFormPrivilegeEntity> templateFormPrivilegeEntityList =
                templateFormPrivilegeMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(templateFormPrivilegeEntityList)) {
            return new ArrayList<>();
        }
        List<TemplateFormPrivilegeVO> templateFormPrivilegeVOS = new ArrayList<>();
        List<TemplateFormPrivilegeUserVO> templateFormPrivilegeUserVOS =
                templateFormPrivilegeUserService.getByApplicationId(applicationId);
        Map<String, List<TemplateFormPrivilegeUserVO>> groupMap = templateFormPrivilegeUserVOS.stream()
                .collect(Collectors.groupingBy(TemplateFormPrivilegeUserVO::getGroupId));
        for (TemplateFormPrivilegeEntity templateFormPrivilegeEntity : templateFormPrivilegeEntityList) {
            TemplateFormPrivilegeVO templateFormPrivilegeVO =
                    AbstractTemplateFormPrivilegeConverter.INSTANCE.toVO(templateFormPrivilegeEntity);
            templateFormPrivilegeVO.setTemplateFormPrivilegeUserList(groupMap.get(templateFormPrivilegeVO.getId()));
            templateFormPrivilegeVOS.add(templateFormPrivilegeVO);
        }
        return templateFormPrivilegeVOS;
    }
}
