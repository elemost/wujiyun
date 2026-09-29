package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.service.converter.AbstractTemplateApplicationImgConverter;
import com.wuji.service.mapper.TemplateApplicationImgMapper;
import com.wuji.service.model.entity.TemplateApplicationImgEntity;
import com.wuji.service.model.request.TemplateApplicationImgRequest;
import com.wuji.service.model.vo.TemplateApplicationImgVO;
import com.wuji.service.service.TemplateApplicationImgService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class TemplateApplicationImgServiceImpl
        extends ServiceImpl<TemplateApplicationImgMapper, TemplateApplicationImgEntity>
        implements TemplateApplicationImgService {

    @Autowired
    private TemplateApplicationImgMapper templateApplicationImgMapper;

    @Override
    public List<TemplateApplicationImgVO> listById(String applicationId) {
        LambdaQueryWrapper<TemplateApplicationImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateApplicationImgEntity::getApplicationId, applicationId);
        List<TemplateApplicationImgEntity> templateApplicationImgEntities =
                templateApplicationImgMapper.selectList(queryWrapper);
        return templateApplicationImgEntities.stream().map(AbstractTemplateApplicationImgConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAll(List<TemplateApplicationImgRequest> templateApplicationImgRequestList,
                        List<String> applicationIdList) {

        List<TemplateApplicationImgEntity> templateApplicationImgEntityList = templateApplicationImgRequestList.stream()
                .map(AbstractTemplateApplicationImgConverter.INSTANCE::toEntity).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return;
        }
        LambdaQueryWrapper<TemplateApplicationImgEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.in(TemplateApplicationImgEntity::getApplicationId, applicationIdList);
        templateApplicationImgMapper.delete(deleteWrapper);
        saveBatch(templateApplicationImgEntityList);
    }
}
