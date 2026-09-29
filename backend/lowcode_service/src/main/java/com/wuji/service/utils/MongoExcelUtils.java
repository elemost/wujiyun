package com.wuji.service.utils;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.service.constant.Constants;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.vo.ExcelDataVO;
import com.wuji.service.model.vo.ExcelImportVO;
import com.wuji.service.model.vo.ExcelLineDataVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MongoExcelUtils {
    public static void drawHeader(int headerRow, List<FormMongoDbExportDomain> formMongoDbExportDomainList, Sheet sheet,
                                  int maxColumn) {
        for (int i = 0; i <= headerRow; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j <= maxColumn; j++) {
                row.createCell(j);
            }
        }
        for (FormMongoDbExportDomain formMongoDbExportDomain : formMongoDbExportDomainList) {
            Row row = sheet.getRow(formMongoDbExportDomain.getRow());
            ExcelUtils.writeValue(formMongoDbExportDomain.getColumn(), row, formMongoDbExportDomain.getLabel());
            if (!Objects.equals(formMongoDbExportDomain.getRow(), formMongoDbExportDomain.getMaxRow()) ||
                    !Objects.equals(formMongoDbExportDomain.getColumn(), formMongoDbExportDomain.getMaxColumn())) {
                sheet.addMergedRegion(
                        new CellRangeAddress(formMongoDbExportDomain.getRow(), formMongoDbExportDomain.getMaxRow(),
                                formMongoDbExportDomain.getColumn(), formMongoDbExportDomain.getMaxColumn()));
            }
            List<FormMongoDbExportDomain> children = formMongoDbExportDomain.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                for (FormMongoDbExportDomain child : children) {
                    Row childRow = sheet.getRow(child.getRow());
                    ExcelUtils.writeValue(child.getColumn(), childRow, child.getLabel());
                }
            }
        }
    }

    public static List<ExcelImportVO> buildHeader(List<ExcelLineDataVO> excelLineDataVOList,
                                                  Map<String, List<FormConfigCommon>> labelMap, Integer headerLine) {
        ExcelLineDataVO excelLineDataVO = excelLineDataVOList.get(headerLine);
        List<ExcelDataVO> excelDataList = excelLineDataVO.getExcelDataList();
        List<ExcelImportVO> excelImportVOS = new ArrayList<>();
        int i = 0;
        while (i < excelDataList.size()) {
            ExcelDataVO excelDataVO = excelDataList.get(i);
            Object value = excelDataVO.getValue();
            if (value == null || StringUtils.isEmpty(value.toString())) {
                ExcelImportVO excelImportVO = setExcelImport(new FormConfigCommon(), i, value);
                excelImportVOS.add(excelImportVO);
                int colSpan = excelDataVO.getColSpan() == null ? 1 : excelDataVO.getColSpan();
                i = i + colSpan;
                continue;
            }
            List<FormConfigCommon> formConfigCommons = labelMap.getOrDefault(value.toString(), new ArrayList<>());
            if (excelDataVO.getRowSpan() == null || excelDataVO.getColSpan() < excelDataVO.getRowSpan()) {
                FormConfigCommon formConfigCommon = formConfigCommons.stream()
                        .filter(c -> !Constants.SUB_FORM_TYPE.equals(c.getType()) &&
                                !FormFieldTypeEnum.notImport().contains(c.getType())).findFirst().orElse(null);
                if (formConfigCommon != null) {
                    formConfigCommons.remove(formConfigCommon);
                    labelMap.put(value.toString(), formConfigCommons);
                    ExcelImportVO excelImportVO = setExcelImport(formConfigCommon, i, value);
                    excelImportVOS.add(excelImportVO);
                } else {
                    ExcelImportVO excelImportVO = setExcelImport(new FormConfigCommon(), i, value);
                    excelImportVOS.add(excelImportVO);
                }
            } else if (excelDataVO.getColSpan() > excelDataVO.getRowSpan()) {
                FormConfigCommon formConfigCommon =
                        formConfigCommons.stream().filter(c -> Constants.SUB_FORM_TYPE.equals(c.getType())).findFirst()
                                .orElse(null);
                if (formConfigCommon != null) {
                    formConfigCommons.remove(formConfigCommon);
                    labelMap.put(value.toString(), formConfigCommons);
                    ExcelImportVO excelImportVO = setExcelImport(formConfigCommon, i, value);
                    buildSub(excelLineDataVOList, excelImportVO, formConfigCommon, excelDataVO, headerLine);
                    excelImportVOS.add(excelImportVO);
                } else {
                    ExcelImportVO excelImportVO = setExcelImport(new FormConfigCommon(), i, value);
                    excelImportVOS.add(excelImportVO);
                    buildSub(excelLineDataVOList, excelImportVO, new FormConfigCommon(), excelDataVO, headerLine);
                }
            }
            int colSpan = excelDataVO.getColSpan() == null ? 1 : excelDataVO.getColSpan();
            i = i + colSpan;
        }
        return excelImportVOS;
    }

    private static void buildSub(List<ExcelLineDataVO> excelLineDataVOList, ExcelImportVO parent,
                                 FormConfigCommon parentFormConfig, ExcelDataVO parentData, Integer headerLine) {
        ExcelLineDataVO excelLineDataVO = excelLineDataVOList.get(headerLine + 1);
        List<ExcelDataVO> excelDataList = excelLineDataVO.getExcelDataList();
        List<ExcelImportVO> excelImportVOList = new ArrayList<>();
        List<FormConfigCommon> columns =
                parentFormConfig.getColumns() == null ? new ArrayList<>() : parentFormConfig.getColumns();
        for (int j = parent.getCol(); j < parent.getCol() + parentData.getColSpan(); j++) {
            ExcelDataVO excelDataVO = excelDataList.get(j);
            FormConfigCommon formConfigCommon =
                    columns.stream().filter(c -> excelDataVO.getValue().equals(c.getLabel())).findFirst().orElse(null);
            columns.remove(formConfigCommon);
            if (formConfigCommon != null) {
                ExcelImportVO excelImportVO = setExcelImport(formConfigCommon, j, excelDataVO.getValue());
                excelImportVOList.add(excelImportVO);
            } else {
                ExcelImportVO excelImportVO = setExcelImport(new FormConfigCommon(), j, excelDataVO.getValue());
                excelImportVOList.add(excelImportVO);
            }
        }
        parent.setExcelImportList(excelImportVOList);
    }

    private static ExcelImportVO setExcelImport(FormConfigCommon formConfigCommon, int j, Object value) {
        ExcelImportVO excelImportVO = new ExcelImportVO();
        excelImportVO.setKey(formConfigCommon.getName());
        excelImportVO.setCol(j);
        excelImportVO.setValue(value);
        excelImportVO.setType(formConfigCommon.getType());
        excelImportVO.setFormConfigCommon(formConfigCommon);
        return excelImportVO;
    }

    public static int getMaxCol(Sheet sheet) {
        int maxCol = 0;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                maxCol = Math.max(maxCol, row.getLastCellNum());
            }
        }
        return maxCol;
    }

    public static boolean getExcelLineData(Sheet sheet, List<CellRangeAddress> mergedRegions,
                                           List<ExcelLineDataVO> excelLineDataVOList) {
        int maxCol = MongoExcelUtils.getMaxCol(sheet);
        boolean existSubForm = false;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            ExcelLineDataVO excelLineDataVO = new ExcelLineDataVO();
            excelLineDataVO.setRowNum(i);
            List<ExcelDataVO> excelDataVOList = new ArrayList<>();
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            for (int j = 0; j < maxCol; j++) {
                Cell cell = row.getCell(j);
                ExcelDataVO excelDataVO = new ExcelDataVO();
                existSubForm = isExistSubForm(mergedRegions, i, j, excelDataVO, existSubForm);
                if (cell != null) {
                    Object value = ExcelUtils.getCellValue(cell);
                    excelDataVO.setValue(value);
                }
                excelDataVOList.add(excelDataVO);
            }
            excelLineDataVO.setExcelDataList(excelDataVOList);
            excelLineDataVOList.add(excelLineDataVO);
        }
        return existSubForm;
    }

    private static boolean isExistSubForm(List<CellRangeAddress> mergedRegions, int i, int j, ExcelDataVO excelDataVO,
                                          boolean existSubForm) {
        CellRangeAddress mergedCellValue = ExcelUtils.getMergedCellValue(mergedRegions, i, j);
        if (mergedCellValue != null) {
            if (mergedCellValue.getFirstColumn() == j && mergedCellValue.getFirstRow() == i) {
                excelDataVO.setRowSpan(mergedCellValue.getLastRow() - mergedCellValue.getFirstRow() + 1);
                excelDataVO.setColSpan(mergedCellValue.getLastColumn() - mergedCellValue.getFirstColumn() + 1);
                if (excelDataVO.getColSpan() > excelDataVO.getRowSpan()) {
                    existSubForm = true;
                }
            }
        }
        return existSubForm;
    }
}
