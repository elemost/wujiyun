package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.service.enums.FormExtraFunctionTypeEnum;
import com.wuji.service.mapper.FormExtraFunctionMapper;
import com.wuji.service.model.entity.FormExtraFunctionEntity;
import com.wuji.service.model.info.FormExtraFunctionInfo;
import com.wuji.service.model.info.FormExtraFunctionInfoRelate;
import com.wuji.service.model.vo.FormExtraFunctionInfoPrivilegeVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.service.FormExtraFunctionInfoService;
import com.wuji.service.service.FormPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("formExtraFunctionInfoServiceImpl")
public class FormExtraFunctionInfoServiceImpl extends FormExtraFunctionServiceImpl
        implements FormExtraFunctionInfoService {

    @Autowired
    private FormExtraFunctionMapper formExtraFunctionMapper;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Override
    public List<FormExtraFunctionInfoPrivilegeVO> privilegeList(String id) {
        LambdaQueryWrapper<FormExtraFunctionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormExtraFunctionEntity::getId, id);
        queryWrapper.eq(FormExtraFunctionEntity::getFunctionType, FormExtraFunctionTypeEnum.INFO.name());
        FormExtraFunctionEntity formExtraFunctionEntity = formExtraFunctionMapper.selectOne(queryWrapper);
        FormExtraFunctionInfo formExtraFunctionInfo =
                JSONObject.parseObject(formExtraFunctionEntity.getConfig(), FormExtraFunctionInfo.class);
        List<FormExtraFunctionInfoRelate> relates = formExtraFunctionInfo.getRelates();
        List<String> formIdList =
                relates.stream().map(FormExtraFunctionInfoRelate::getFormId).collect(Collectors.toList());
        List<FormPrivilegeVO> formPrivilegeVOS =
                formPrivilegeService.getUserPrivilegeByCategory(formIdList, formExtraFunctionEntity.getApplicationId());
        Map<String, List<FormPrivilegeVO>> categoryMap =
                formPrivilegeVOS.stream().collect(Collectors.groupingBy(FormPrivilegeVO::getCategoryId));
        List<FormExtraFunctionInfoPrivilegeVO> infoPrivilegeList = new ArrayList<>();
        for (FormExtraFunctionInfoRelate formExtraFunctionInfoRelate : relates) {
            List<FormPrivilegeVO> formPrivilegeVOList = categoryMap.get(formExtraFunctionInfoRelate.getFormId());
            if (CollectionUtils.isNotEmpty(formPrivilegeVOList)) {
                FormExtraFunctionInfoPrivilegeVO formExtraFunctionInfoPrivilegeVO =
                        new FormExtraFunctionInfoPrivilegeVO();
                formExtraFunctionInfoPrivilegeVO.setName(formExtraFunctionInfoRelate.getName());
                formExtraFunctionInfoPrivilegeVO.setFormId(formExtraFunctionInfoRelate.getFormId());
                formExtraFunctionInfoPrivilegeVO.setRelateConfig(formExtraFunctionInfoRelate.getRelateConfig());
                infoPrivilegeList.add(formExtraFunctionInfoPrivilegeVO);
            }
        }
        return infoPrivilegeList;
    }
}
