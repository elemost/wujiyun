package com.wuji.service.service.form.function;

import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormFunctionDataService;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.service.FlowableActivityConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormPrivilegeButtonServiceImpl implements FormFunctionDataService {

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Override
    public String fieldType() {
        return "privilege";
    }

    @Override
    public void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                                   FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info) {
        Map<String, List<FlowableActivityConfigDomain>> modelIdToMap = new HashMap<>();
        if (ApplicationCategoryCategoryTypeEnum.getFlowerFormType().contains(info.getFormType())) {
            List<String> modelIdList =
                    lowcodeDataList.stream().map(LowcodeDataVO::getModelId).collect(Collectors.toList());
            // 判断是否存在子表单
            List<FlowableActivityConfigDomain> flowableActivityConfigList =
                    flowableActivityConfigService.getByModelIdList(modelIdList, "subFlowTask");
            modelIdToMap = flowableActivityConfigList.stream()
                    .collect(Collectors.groupingBy(FlowableActivityConfigDomain::getModelId));
        }
        if (CollectionUtils.isEmpty(formDataExtraParameterRequest.getFormPrivilegeVO())) {
            return;
        }
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            // 权限按钮
            buildPrivilege(formDataExtraParameterRequest.getFormPrivilegeVO(), lowcodeDataVO, modelIdToMap);
        }
    }

    private static void buildPrivilege(List<FormPrivilegeVO> formPrivilegeVOList, LowcodeDataVO lowcodeDataVO,
                                       Map<String, List<FlowableActivityConfigDomain>> modelIdToMap) {
        List<String> viewPrivilegeList = new ArrayList<>();
        List<String> operatePrivilegeList = new ArrayList<>();
        for (FormPrivilegeVO formPrivilegeVO : formPrivilegeVOList) {
            viewPrivilegeList.addAll(formPrivilegeVO.getViewPrivilegeList());
            operatePrivilegeList.addAll(formPrivilegeVO.getOperatePrivilegeList());
        }
        viewPrivilegeList = viewPrivilegeList.stream().distinct().collect(Collectors.toList());
        operatePrivilegeList = operatePrivilegeList.stream().distinct().collect(Collectors.toList());
        if (viewPrivilegeList.contains("SUB_DATA") &&
                CollectionUtils.isEmpty(modelIdToMap.get(lowcodeDataVO.getModelId()))) {
            viewPrivilegeList.remove("SUB_DATA");
        }
        lowcodeDataVO.setViewPivilegeList(viewPrivilegeList);
        lowcodeDataVO.setOperatePivilegeList(operatePrivilegeList);
    }
}
