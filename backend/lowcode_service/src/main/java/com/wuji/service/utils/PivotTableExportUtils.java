package com.wuji.service.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.model.info.FormAggregateDate;
import com.wuji.service.model.info.excel.ColumnDef;
import com.wuji.service.model.info.excel.Dept;
import com.wuji.service.model.info.excel.ScoreData;
import com.wuji.service.model.info.excel.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PivotTableExportUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void export(ScoreData data, HttpServletResponse response) {
        // 1. 构建映射：deptId -> deptName，userId -> nickName
        Map<String, String> deptMap = new HashMap<>();
        for (Dept d : data.getDepartmentList()) {
            deptMap.put(String.valueOf(d.getDeptId()), d.getDeptName());
        }
        Map<String, String> userMap = new HashMap<>();
        for (User u : data.getUserList()) {
            userMap.put(String.valueOf(u.getUserId()), u.getNickName());
        }

        // 2. 动态表头：按 columns 顺序
        List<List<String>> head = new ArrayList<>();
        for (ColumnDef col : data.getColumns()) {
            head.add(Collections.singletonList(col.getTitle()));
        }

        // 3. 组装数据行（列序与 head 一致，且做字段转换）
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String, Object> row : data.getDataList()) {
            List<Object> cell = new ArrayList<>();
            for (ColumnDef col : data.getColumns()) {
                Object v = row.get(col.getDataIndex());
                cell.add(convert(col, v, userMap, deptMap));
            }
            rows.add(cell);
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // 4. 写出到 response（文件流）
            String fileName = URLEncoder.encode("员工积分明细", "UTF-8").replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(outputStream).head(head).sheet("积分明细").doWrite(rows);
        } catch (Exception e) {
            log.error("导出失败", e);
        }
    }

    /**
     * 单元格值转换：将 JSON 字符串字段解析并映射为可读文本。
     * 通过列 title 判断，避免硬编码 dataIndex 编号，更健壮。
     */
    private static Object convert(ColumnDef col, Object value, Map<String, String> userMap,
                                  Map<String, String> deptMap) {
        // 非字符串直接返回（数字列，如 A/B/C 分、券数）
        if (!(value instanceof String)) {
            return value;
        }
        String str = (String) value;

        // 非 JSON 直接返回
        if (!str.startsWith("{")) {
            return str;
        }

        try {
            JsonNode node = MAPPER.readTree(str);
            // 员工姓名：从 {"userId":"440",...} 解析并映射 nickName
            if (str.contains("\"userId\":")) {
                String uid = node.path("userId").asText("");
                return userMap.getOrDefault(uid, uid);
            }
            // 所属部门：从 {"deptId":"2382",...} 解析并映射 deptName
            if (str.contains("\"deptId\":")) {
                String did = node.path("deptId").asText("");
                return deptMap.getOrDefault(did, did);
            }
            if (str.contains("\"year\":")) {
                FormAggregateDate formAggregateDate = JSONObject.parseObject(str, FormAggregateDate.class);
                Date date = formAggregateDate.getDate();
                return TimeUtils.formatDateTime(date, TimeUtils.TIME_DATE);
            }
            return str;
        } catch (Exception e) {
            // 解析失败时兜底返回原文，避免整列导出失败
            return str;
        }
    }
}
