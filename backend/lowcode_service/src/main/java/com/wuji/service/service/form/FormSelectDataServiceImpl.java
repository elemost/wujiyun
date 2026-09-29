package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormSelectData;
import com.wuji.service.constant.Constants;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormExtraFunctionTitleService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.MongoDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormSelectDataServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoDbService mongoDbService;

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.SELECT_DATA.getFieldType();
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        JSONObject dataRegion = formConfigCommon.getDataRegion();
        if (dataRegion == null) {
            return;
        }
        Object object = dataRegion.get("formId");
        if (object == null) {
            return;
        }
        String formId = object.toString();
        if (formId != null &&
                (formId.startsWith(Constants.AGGREGATE_TABLE) || formId.startsWith(Constants.FAC_PREFIX))) {
            return;
        }
        FormVO formVO = formService.info(formId, info.getApplicationId());
        List<String> uuidList = new ArrayList<>();
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            Object value = instValue.get(formConfigCommon.getName());
            if (value instanceof String) {
                instValue.put(formConfigCommon.getName(), null);
                continue;
            }
            FormSelectData formSelectData = instValue.getObject(formConfigCommon.getName(), FormSelectData.class);
            if (formSelectData != null) {
                uuidList.add(formSelectData.getUuid());
            }
        }
        List<LowcodeDataDomain> lowcodeDataDomains = mongoDbService.getByUuidList(uuidList, formVO.getTableName());
        formExtraFunctionTitleService.buildTitle(lowcodeDataDomains, formVO.getConfig(), formVO.getId(),
                formVO.getApplicationId());
        Map<String, String> dataTitleMap = lowcodeDataDomains.stream()
                .collect(Collectors.toMap(LowcodeDataDomain::getUuid, LowcodeDataDomain::getDataTitle));
        for (LowcodeDataVO lowcodeDataVO : lowcodeDataList) {
            JSONObject instValue = lowcodeDataVO.getInstValue();
            FormSelectData formSelectData = instValue.getObject(formConfigCommon.getName(), FormSelectData.class);
            if (formSelectData != null) {
                formSelectData.setLabel(dataTitleMap.get(formSelectData.getUuid()));
                instValue.put(formConfigCommon.getName(), JSONObject.toJSON(formSelectData));
            }
        }
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        formImportCheckResultVO.setValue(null);
        return formImportCheckResultVO;
    }
}
