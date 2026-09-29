package com.wuji.service.service.form.function;

import com.wuji.admin.service.UserService;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.request.FormDataExtraParameterRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormFunctionDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormCreateNameServiceImpl implements FormFunctionDataService {

    @Autowired
    private UserService userService;

    @Override
    public String fieldType() {
        return "system_creator";
    }

    @Override
    public void dealFunctionReturn(List<LowcodeDataVO> lowcodeDataList,
                                   FormDataExtraParameterRequest formDataExtraParameterRequest, FormVO info) {
        List<String> creatorNameList =
                lowcodeDataList.stream().map(LowcodeDataVO::getCreator).collect(Collectors.toList());
        Map<Long, String> userNameMap = userService.getIdToNameMap(
                creatorNameList.stream().filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toList()));
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            if (StringUtils.isNotEmpty(lowcodeDataVO.getCreator())) {
                lowcodeDataVO.setCreatorName(userNameMap.get(Long.valueOf(lowcodeDataVO.getCreator())));
            }
            if (StringUtils.isNotEmpty(lowcodeDataVO.getStatus())) {
                lowcodeDataVO.setStatusName(FormDataStatusEnum.valueOf(lowcodeDataVO.getStatus()).getMessage());
            }
            lowcodeDataVO.setCreateTimeString(TimeUtils.formatDateTime(lowcodeDataVO.getCreateTime()));
            lowcodeDataVO.setModifyTimeString(TimeUtils.formatDateTime(lowcodeDataVO.getModifyTime()));
        }
    }
}
