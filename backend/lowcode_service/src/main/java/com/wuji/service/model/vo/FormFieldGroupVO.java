package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormFieldGroupVO {
    private List<FormFieldGroupInfoVO> groupInfos;

    private List<LowcodeDataVO> datas;

}
