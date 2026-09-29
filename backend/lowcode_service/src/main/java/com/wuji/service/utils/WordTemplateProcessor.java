package com.wuji.service.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.StringUtil;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.vo.FormConfigRelationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Data
@AllArgsConstructor
public class WordTemplateProcessor {

    private InputStream inputStream;

    private JSONObject instValue;

    private List<String> subFormList;

    public XWPFDocument processTemplate() {
        try {
            XWPFDocument document = new XWPFDocument(inputStream);
            exchangeParagraph(this.instValue, document);
            exchangeTable(this.instValue, document);
            if (CollectionUtils.isNotEmpty(this.subFormList)) {
                List<XWPFTable> tables = document.getTables();
                for (XWPFTable xwpfTable : tables) {
                    drawTable(xwpfTable, this.subFormList);
                }
            }
            return document;
        } catch (Exception e) {
            log.error("word下载失败", e);
            throw new ServiceException(ServiceResultCode.WORD_ANALYSIS_ERROR);
        }
    }

    private void drawTable(XWPFTable xwpfTable, List<String> subFormList) {
        FormConfigRelationVO formConfigRelationVO = checkTable(xwpfTable, subFormList);
        if (formConfigRelationVO != null) {
            String subForm = formConfigRelationVO.getSubForm();
            JSONArray jsonArray = this.instValue.getJSONArray(subForm);
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            List<JSONObject> javaList = jsonArray.toJavaList(JSONObject.class);
            WordTemplateProcessor.drawTable(javaList, formConfigRelationVO.getRow(), xwpfTable, subForm);
        }
    }

    private static FormConfigRelationVO checkTable(XWPFTable xwpfTable, List<String> subFormList) {
        List<XWPFTableRow> rows = xwpfTable.getRows();
        int i = 0;
        for (XWPFTableRow xwpfTableRow : rows) {
            List<XWPFTableCell> tableCells = xwpfTableRow.getTableCells();
            for (XWPFTableCell xwpfTableCell : tableCells) {
                String cellText = xwpfTableCell.getText();
                for (String subForm : subFormList) {
                    if (cellText.contains(subForm)) {
                        FormConfigRelationVO formConfigRelationVO = new FormConfigRelationVO();
                        formConfigRelationVO.setSubForm(subForm);
                        formConfigRelationVO.setRow(i);
                        return formConfigRelationVO;
                    }
                }
            }
            i++;
        }
        return null;
    }

    /**
     * 替换表格中的占位符
     *
     * @param replacements 转换的数据
     * @param document wordDocument
     */
    public static void exchangeTable(Map<String, Object> replacements, XWPFDocument document) {
        for (XWPFTable tbl : document.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && !text.isEmpty()) {
                                for (Map.Entry<String, Object> entry : replacements.entrySet()) {
                                    text = getText("", replacements, r, entry.getKey(), text);
                                    if (text != null) {
                                        r.setText(text, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 替换表格中的占位符渲染列表
     *
     * @param dataList 数据列表
     * @param startRow 开始的row
     * @param tbl word中的table
     */
    public static void drawTable(List<JSONObject> dataList, Integer startRow, XWPFTable tbl, String subForm) {
        // 获取模板行（第二行）
        XWPFTableRow templateRow = tbl.getRow(startRow);
        // 添加新的数据行
        int i = startRow + 1;
        for (Map<String, Object> data : dataList) {
            XWPFTableRow newRow = tbl.insertNewTableRow(i);
            copyRow(templateRow, newRow);
            i++;
            for (int colIndex = 0; colIndex < templateRow.getTableCells().size(); colIndex++) {
                XWPFTableCell templateCell = templateRow.getCell(colIndex);
                String cellText = templateCell.getText();
                List<String> keyList = checkCellTextExistKey(data.keySet(), cellText, subForm);
                if (keyList.isEmpty()) {
                    continue;
                }
                XWPFTableCell newCell = newRow.getCell(colIndex);
                int a = 0;
                for (XWPFParagraph para : templateCell.getParagraphs()) {
                    if (newCell.getParagraphs().size() <= a) {
                        continue;
                    }
                    XWPFParagraph newPara = newCell.getParagraphs().get(a);
                    int j = 0;
                    for (XWPFRun run : para.getRuns()) {
                        XWPFRun newRun = newPara.getRuns().get(j);
                        String text = run.getText(0);
                        if (text == null) {
                            newRun.addBreak();
                            continue;
                        }
                        for (String key : keyList) {
                            text = getText(subForm, data, newRun, key, text);
                        }
                        if (text != null) {
                            newRun.setText(text, 0);
                        }
                        j++;
                    }
                    a++;
                }
            }
        }
        tbl.removeRow(startRow);
    }

    private static String getText(String subForm, Map<String, Object> data, XWPFRun run, String key, String text) {
        if (text == null) {
            return null;
        }
        if (!text.contains(dealKey(subForm, key, Boolean.FALSE))) {
            return text;
        }
        if (!text.isEmpty()) {
            Object value = data.get(key);
            if (value != null) {
                if (value instanceof byte[]) {
                    if (text.contains(dealKey(subForm, key, Boolean.FALSE))) {
                        // 插入图片
                        List<String> size = StringUtil.getSize(text);
                        if (size.size() != 2) {
                            return text;
                        }
                        insertImageAfterRun(run, (byte[]) value, Integer.parseInt(size.get(0)),
                                Integer.parseInt(size.get(1)));
                        // 删除旧文本
                        run.setText("", 0);
                        return null;
                    }
                } else {
                    text = text.replace(dealKey(subForm, key, Boolean.TRUE), value.toString());
                }

            } else {
                text = text.replace(dealKey(subForm, key, Boolean.TRUE), "");
            }
        }
        return text;
    }

    private static List<String> checkCellTextExistKey(Set<String> keyList, String text, String subForm) {
        List<String> returnList = new ArrayList<>();
        for (String key : keyList) {
            if (text.contains(dealKey(subForm, key, Boolean.FALSE))) {
                returnList.add(key);
            }
        }
        return returnList;
    }

    /**
     * 替换段落里面的占位符
     *
     * @param replacements
     * @param document
     */
    public static void exchangeParagraph(Map<String, Object> replacements, XWPFDocument document) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replacePlaceholdersInParagraph(paragraph, replacements);
        }
    }

    /**
     * 在段落中替换占位符
     *
     * @param paragraph    段落对象
     * @param replacements 占位符与替换值的映射
     */
    private static void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, Object> replacements) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs != null) {
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null && !text.isEmpty()) {
                    for (Map.Entry<String, Object> entry : replacements.entrySet()) {
                        text = getText("", replacements, run, entry.getKey(), text);
                    }
                    if (text != null) {
                        run.setText(text, 0);
                    }
                }
            }
        }
    }

    // 在指定 Run 后面插入图片
    private static void insertImageAfterRun(XWPFRun refRun, byte[] bytes, int width, int height) {
        try {
            int format = XWPFDocument.PICTURE_TYPE_JPEG;
            // 读取图片
            ByteArrayInputStream bile = new ByteArrayInputStream(bytes);
            // 插入图片
            refRun.addPicture(bile, format, "文件.jpeg", 360000 / 10 * width, 360000 / 10 * height);
        } catch (Exception e) {
            log.error("word插入图片失败", e);
        }
    }

    private static String dealKey(String subForm, String key, Boolean needEnd) {
        String keyWord;
        if (StringUtils.isEmpty(subForm)) {
            keyWord = "${" + key;
        } else {
            keyWord = "${" + subForm + "." + key;
        }
        if (needEnd) {
            keyWord = keyWord + "}";
        }
        return keyWord;
    }

    private static void copyRow(XWPFTableRow sourceRow, XWPFTableRow targetRow) {
        // 复制行属性
        CTTrPr trPr = sourceRow.getCtRow().getTrPr();
        if (trPr != null) {
            targetRow.getCtRow().setTrPr(trPr);
        }

        // 复制单元格内容和格式
        List<XWPFTableCell> cells = sourceRow.getTableCells();
        for (int i = 0; i < cells.size(); i++) {
            XWPFTableCell targetCell = targetRow.createCell();
            XWPFTableCell sourceCell = sourceRow.getCell(i);
            copyCellStyle(sourceCell, targetCell);
        }
    }

    private static void copyCellStyle(XWPFTableCell sourceCell, XWPFTableCell targetCell) {
        // 复制单元格样式
        targetCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
        // targetCell.setColor(sourceCell.getColor());
        // 如果有段落，则复制段落样式
        int i = 0;
        for (XWPFParagraph p : sourceCell.getParagraphs()) {
            XWPFParagraph newP;
            if (targetCell.getParagraphs().size() > i) {
                newP = targetCell.getParagraphs().get(i);
            } else {
                newP = targetCell.addParagraph();
            }
            copyParagraphStyle(p, newP);
        }
    }

    private static void copyParagraphStyle(XWPFParagraph sourceParagraph, XWPFParagraph targetParagraph) {
        // 复制段落样式
        // targetParagraph.getCTP().set(sourceParagraph.getCTP().copy());

        // 如果有文本，则复制文本样式
        for (XWPFRun r : sourceParagraph.getRuns()) {
            XWPFRun newR = targetParagraph.createRun();
            // newR.setBold(r.isBold());
            // newR.setItalic(r.isItalic());
            // newR.setFontSize(r.getFontSize());
            // newR.setColor(r.getColor());
            // newR.setFontFamily(r.getFontFamily());
            // newR.setTextScale(r.getTextScale());
            // newR.setText(r.getText(0));
            CTRPr sourceRPr = r.getCTR().getRPr();
            if (sourceRPr != null) {
                newR.getCTR().setRPr((CTRPr) sourceRPr.copy());
            }
        }
    }
}

