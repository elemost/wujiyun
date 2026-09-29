package com.wuji.service.utils;

import com.wuji.common.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class ExcelUtils {

    public static void writeValue(int column, Row row, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        // log.info("value: " + value + " row: " + row.getRowNum() + " column " + column);
    }

    public static CellRangeAddress getMergedCellValue(List<CellRangeAddress> mergedRegions, Cell cell) {
        for (CellRangeAddress range : mergedRegions) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return range;
            }
        }
        return null;
    }

    public static CellRangeAddress getMergedCellValue(List<CellRangeAddress> mergedRegions, int i, int j) {
        for (CellRangeAddress range : mergedRegions) {
            if (range.isInRange(i, j)) {
                return range;
            }
        }
        return null;
    }


    public static boolean cellIsMerged(List<CellRangeAddress> mergedRegions, Cell cell) {
        for (CellRangeAddress range : mergedRegions) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }

    public static Object getCellValue(Cell cell) {
        Object val = "";
        try {
            if (cell != null) {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // POI Excel 日期格式转换
                        val = DateUtil.getJavaDate((Double) val);
                        val = TimeUtils.formatDateTime((Date) val);
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

            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }
}
