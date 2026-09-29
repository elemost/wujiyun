package com.wuji.service.service.impl;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.model.vo.FormFieldMappingVO;
import com.wuji.service.service.FormFieldService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormFieldServiceImpl implements FormFieldService {

    @Override
    public List<FormFieldMappingVO> getFieldMapping() {
        FormFieldTypeEnum[] values = FormFieldTypeEnum.values();

        List<FormFieldMappingVO> fieldMappingVOS = new ArrayList<>();
        for (FormFieldTypeEnum formFieldTypeEnum : values) {
            FormFieldMappingVO formFieldMappingVO = new FormFieldMappingVO();
            formFieldMappingVO.setFieldType(formFieldTypeEnum.getFieldType());
            formFieldMappingVO.setMappingType(formFieldTypeEnum.getMappingType());
            fieldMappingVOS.add(formFieldMappingVO);
        }
        return fieldMappingVOS;
    }
}
