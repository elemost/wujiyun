package com.wuji.service.utils;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FormulaEvaluatorUtils {


    public static Map<String, String> writeValue(List<DataStreamQuoteField> valList, Sheet sheet,
                                                 Map<Long, DataStreamCalculateVO> nodeIdMap) {
        Row row = sheet.createRow(1);

        Map<String, String> fieldIdMap = new HashMap<>();
        int i = 0;
        for (DataStreamQuoteField dataStreamQuoteField : valList) {
            Cell cell = row.createCell(i);
            Object value = MongoDbDataTransUtils.getValueExistMore(nodeIdMap, dataStreamQuoteField, false);
            FormFieldTypeEnum formFieldTypeEnum =
                    FormFieldTypeEnum.getByFieldType(dataStreamQuoteField.getQuoteFieldType());
            if ("文本".equals(formFieldTypeEnum.getDataType())) {
                if (value != null) {
                    cell.setCellValue(value.toString());
                } else {
                    return null;
                }
            } else if ("数字".equals(formFieldTypeEnum.getDataType())) {
                if (value != null) {
                    cell.setCellValue(Double.parseDouble(value.toString()));
                } else {
                    return null;
                }
            } else if ("时间戳".equals(formFieldTypeEnum.getDataType())) {
                if (value != null) {
                    cell.setCellValue(Long.parseLong(value.toString()));
                } else {
                    return null;
                }
            }
            fieldIdMap.put(dataStreamQuoteField.getId(), cell.getAddress().toString());
            i++;
        }
        return fieldIdMap;
    }

    public static Object getCellValue(Cell cell) {
        Object val = "";
        if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
            val = cell.getNumericCellValue();
            if (DateUtil.isCellDateFormatted(cell)) {
                // POI Excel 日期格式转换
                val = DateUtil.getJavaDate((Double) val);
                val = ((Date) val).getTime();
            } else {
                if ((Double) val % 1 != 0) {
                    val = new BigDecimal(val.toString());
                } else {
                    val = new DecimalFormat("0").format(val);
                }
            }
        } else if (cell.getCellType() == CellType.STRING) {
            val = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            val = cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.ERROR) {
            val = cell.getErrorCellValue();
        }
        return val;
    }
}
