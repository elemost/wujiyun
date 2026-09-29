package com.wuji.service.service.form.function;

import com.wuji.service.enums.FormExtraFunctionLocationEnum;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormFunctionDataService;
import com.wuji.service.utils.FormPrivilegeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormButtonServiceImpl implements FormFunctionDataService {

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Override
    public String fieldType() {
        return "button";
    }

    @Override
    public void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                                   FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info) {
        List<FormPrivilegeVO> formPrivilegeVO = formDataExtraParameterRequest.getFormPrivilegeVO();
        List<FormExtraFunctionVO> formExtraFunctionVOList = new ArrayList<>();
        if (formDataExtraParameterRequest.getExtraButton() && formPrivilegeVO != null) {
            List<String> priviegeIdList =
                    formPrivilegeVO.stream().map(FormPrivilegeVO::getId).collect(Collectors.toList());
            if (StringUtils.isNotEmpty(formDataExtraParameterRequest.getViewId())) {
                formExtraFunctionVOList = formExtraFunctionServiceImpl.getByFormIdAndGroupId(info.getApplicationId(),
                        formDataExtraParameterRequest.getViewId(), priviegeIdList);
            } else {
                formExtraFunctionVOList =
                        formExtraFunctionServiceImpl.getByFormIdAndGroupId(info.getApplicationId(), info.getId(),
                                priviegeIdList);
            }
        }
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            // 自定义按钮
            List<FormExtraFunctionButton> functionVOList =
                    FormPrivilegeUtils.getButton(lowcodeDataVO, formExtraFunctionVOList,
                            FormExtraFunctionLocationEnum.LIST.name());
            lowcodeDataVO.setButtonList(functionVOList);
        }
    }
}
