package com.yanwu.spring.cloud.common.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xuli
 * @Date: 2018/10/19 10:18
 */
public class ExcelUtil {

    /**
     * 组装Excel的方法, 通过该方法传入标题栏和数据体得到一个Excel文件
     *
     * @param head     标题栏
     * @param contents 数据集合
     * @return
     * @throws Exception
     */
    public static SXSSFWorkbook assembleExcel(List<String> head, List<List<String>> contents) throws Exception {
        // ---- 创建excel文件
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // ---- 创建工作簿
        Sheet sheet = workbook.createSheet();
        // ---- 设置默认列宽
        sheet.setDefaultColumnWidth(15);
        // ---- 创建标题行
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < head.size(); i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(head.get(i));
            cell.setCellStyle(getHeadCellStyle(workbook));
        }
        // ---- 创建数据行
        for (int i = 0; i < contents.size(); i++) {
            List<String> content = contents.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < content.size(); j++) {
                row.createCell(j).setCellValue(content.get(j));
            }
        }
        return workbook;
    }

    /**
     * 组装标题头
     *
     * @param value
     * @return
     */
    public static List<String> assembleHead(String... value) {
        if (ArrayUtil.isEmpty(value)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String str : value) {
            if (StringUtils.isNotBlank(str)) {
                list.add(str);
            }
        }
        return list;
    }

    /**
     * 导出Excel
     *
     * @param workbook
     * @param fileName
     * @return
     * @throws Exception
     */
    public static ResponseEntity<Resource> export(SXSSFWorkbook workbook, String fileName) throws Exception {
        ByteArrayResource resource;
        // ===== response输出excel
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            resource = new ByteArrayResource(out.toByteArray());
        } finally {
            workbook.dispose();
        }
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDisposition)
                .body(resource);
    }

    /**
     * 设置单元格样式
     *
     * @param workbook
     * @return
     */
    private static CellStyle getHeadCellStyle(SXSSFWorkbook workbook) {
        CellStyle headStyle = workbook.createCellStyle();
        // ----- 水平居中
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        // ----- 设置字体加粗
        Font headFont = workbook.createFont();
        headFont.setBold(true);
        headStyle.setFont(headFont);
        return headStyle;
    }

}
