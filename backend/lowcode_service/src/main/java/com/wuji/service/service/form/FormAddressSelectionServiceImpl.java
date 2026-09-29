package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.CityCache;
import com.wuji.admin.model.vo.CityVO;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormAddress;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.utils.ExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormAddressSelectionServiceImpl extends FormCommonServiceImpl implements FormDataService {

    @Override
    public String fieldType() {
        return FormFieldTypeEnum.ADDRESS_SELECTION.getFieldType();
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        FormAddress formAddress = instValue.getObject(formConfigCommon.getName(), FormAddress.class);
        String value = null;
        if (formAddress != null) {
            value = formAddress.getFullAddress() + formAddress.getDetailedAddress();
        }
        putValue(instValue, value, formId, formConfigCommon);
    }

    @Override
    public FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                                   SystemAllDataNameVO importCheck) {
        FormImportCheckResultVO formImportCheckResultVO = new FormImportCheckResultVO();
        if (value == null) {
            return formImportCheckResultVO;
        }
        FormAddress formAddress = new FormAddress();
        formAddress.setFullAddress(value.toString());
        formAddress.setDetailedAddress(value.toString());
        formImportCheckResultVO.setValue(JSONObject.parseObject(JSONObject.toJSONString(formAddress)));
        return formImportCheckResultVO;
    }

    @Override
    public FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                               SystemAllDataNameVO importCheck) {
        FormAddress formAddress = JSONObject.parseObject(JSONObject.toJSONString(value), FormAddress.class);
        if (formAddress == null) {
            return new FormSyncCheckResultVO();
        }
        FormSyncCheckResultVO formSyncCheckResultVO = new FormSyncCheckResultVO();
        List<CityVO> cityList = CityCache.getCity();
        if (StringUtils.isNotEmpty(formAddress.getProvince())) {
            String province = formAddress.getProvince();
            formAddress.setProvince(province);
            CityVO provinceVO = matchCity(cityList, province);
            if (provinceVO == null) {
                formSyncCheckResultVO.setErrorMessage("省级地址错误：" + province);
                return formSyncCheckResultVO;
            } else {
                formAddress.setProvince(provinceVO.getCityName());
                formAddress.setLabel(provinceVO.getCityName());
                formAddress.setValue(provinceVO.getCode().toString());
            }
            if (StringUtils.isNotEmpty(formAddress.getCity())) {
                String city = formAddress.getCity();
                CityVO cityVO = matchCity(provinceVO.getChildren(), city);
                if (cityVO == null) {
                    formSyncCheckResultVO.setErrorMessage("市级地址错误：" + city);
                    return formSyncCheckResultVO;
                } else {
                    formAddress.setCity(cityVO.getCityName());
                    formAddress.setLabel(cityVO.getCityName());
                    formAddress.setValue(cityVO.getCode().toString());
                }
                if (StringUtils.isNotEmpty(formAddress.getDistrict())) {
                    String district = formAddress.getDistrict();
                    CityVO districtVO = matchCity(cityVO.getChildren(), district);
                    if (districtVO == null) {
                        formSyncCheckResultVO.setErrorMessage("区级地址错误：" + district);
                        return formSyncCheckResultVO;
                    } else {
                        formAddress.setDistrict(districtVO.getCityName());
                        formAddress.setLabel(districtVO.getCityName());
                        formAddress.setValue(districtVO.getCode().toString());
                    }
                }
            }
        }
        String fullName = formAddress.getProvince();
        formAddress.setLabel(formAddress.getLabel());
        if (StringUtils.isNotEmpty(formAddress.getCity())) {
            fullName = fullName + "/" + formAddress.getCity();
            formAddress.setLabel(formAddress.getCity());
            if (StringUtils.isNotEmpty(formAddress.getDistrict())) {
                fullName = fullName + "/" + formAddress.getDistrict();
                formAddress.setLabel(formAddress.getDistrict());
            }
        }
        formAddress.setFullAddress(fullName);
        formSyncCheckResultVO.setValue(formAddress);
        return formSyncCheckResultVO;
    }

    private CityVO matchCity(List<CityVO> cityList, String city) {
        if (CollectionUtils.isEmpty(cityList)) {
            return null;
        }
        for (CityVO cityVO : cityList) {
            if (cityVO.getCityName().contains(city)) {
                return cityVO;
            }
        }
        return null;
    }

    @Override
    public void dealWhileExport(int column, Row row, JSONObject instValue,
                                FormMongoDbExportDomain formMongoDbExportDomain, SystemAllDataVO systemAllDataVO) {
        FormAddress formAddress = instValue.getObject(formMongoDbExportDomain.getName(), FormAddress.class);
        if (formAddress != null) {
            if (StringUtils.isNotEmpty(formAddress.getDetailedAddress())) {
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row,
                        formAddress.getFullAddress().replaceAll(" ", "") + "/" + formAddress.getDetailedAddress());
            } else {
                ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row,
                        formAddress.getFullAddress().replaceAll(" ", ""));
            }
        }
    }

    @Override
    public String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO) {
        if (CollectionUtils.isEmpty(values)) {
            return "";
        }
        Object address = values.get(0);
        if (address == null) {
            return "";
        }
        if (address instanceof String) {
            return address.toString();
        }
        FormAddress formAddress = JSONObject.parseObject(JSONObject.toJSONString(address), FormAddress.class);
        if (formAddress == null) {
            return "";
        }
        return formAddress.getFullAddress();
    }
}
