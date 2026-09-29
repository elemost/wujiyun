package com.wuji.service.service;

import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;

import java.util.List;

public interface FormFunctionDataService {

    String fieldType();

    /**
     * 返回列表额外的字段处理
     *
     * @param lowcodeDataList
     * @param formDataExtraParameterRequest
     * @param info
     */
    void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                            FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info);
}
