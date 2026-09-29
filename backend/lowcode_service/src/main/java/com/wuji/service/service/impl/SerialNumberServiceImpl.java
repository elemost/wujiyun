package com.wuji.service.service.impl;

import com.wuji.common.utils.TimeUtils;
import com.wuji.service.enums.SerialNumberTypeEnum;
import com.wuji.service.model.info.SerialNumberConfig;
import com.wuji.service.service.FormSerialNumberService;
import com.wuji.service.service.SerialNumberService;
import com.wuji.service.utils.SerialNumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SerialNumberServiceImpl implements SerialNumberService {

    @Autowired
    private FormSerialNumberService formSerialNumberService;

    @Override
    public String getSerialNumber(List<SerialNumberConfig> configList, String fieldId, String formId,
                                  String applicationId) {
        final String key = applicationId + "_" + formId + "_" + fieldId;
        StringBuilder serialNumber = new StringBuilder();
        try {
            for (SerialNumberConfig serialNumberConfig : configList) {
                SerialNumberTypeEnum serialNumberTypeEnum = SerialNumberTypeEnum.valueOf(serialNumberConfig.getType());
                switch (serialNumberTypeEnum) {
                    case DATE:
                        if (StringUtils.isEmpty(serialNumberConfig.getFormat())) {
                            serialNumberConfig.setFormat(TimeUtils.DATE_SIMPLE);
                        }
                        String dateTime = TimeUtils.formatDateTime(new Date(), serialNumberConfig.getFormat());
                        serialNumber.append(dateTime);
                        break;
                    case COUNT:
                        Integer value = null;
                        while (value == null) {
                            String dateTimeKey = SerialNumberUtils.getDateTimeKey(serialNumberConfig.getCycle());
                            value = formSerialNumberService.insertOrUpdate(dateTimeKey, key,
                                    serialNumberConfig.getInitialValue());
                            if (value == null) {
                                continue;
                            }
                            String valueString = getString(serialNumberConfig, value);
                            serialNumber.append(valueString);
                        }
                        break;
                    case FIX_CHARACTER:
                        if (serialNumberConfig.getFixedCharacter() != null) {
                            serialNumber.append(serialNumberConfig.getFixedCharacter());
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("生成编号失败", e);
        }

        return serialNumber.toString();
    }

    private static String getString(SerialNumberConfig serialNumberConfig, Integer value) {
        String valueString = value.toString();
        if (serialNumberConfig.getFixDigit()) {
            if (value.toString().length() < serialNumberConfig.getDigit()) {
                StringBuilder pattern = new StringBuilder();
                for (int i = 0; i < serialNumberConfig.getDigit(); i++) {
                    pattern.append("0");
                }
                DecimalFormat df = new DecimalFormat(pattern.toString());
                valueString = df.format(value);
            }
        }
        return valueString;
    }
}
