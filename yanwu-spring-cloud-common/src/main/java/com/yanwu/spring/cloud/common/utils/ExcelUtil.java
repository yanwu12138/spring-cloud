package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.yanwu.spring.cloud.common.core.common.Encoding;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XuBaofeng.
 * @date 2018-10-11 18:55.
 * <p>
 * description: Excel工具类，提供Excel07导出功能和Excel导入功能
 */
@SuppressWarnings("unused")
public class ExcelUtil {

    private ExcelUtil() {
        throw new UnsupportedOperationException("ExcelUtil should never be instantiated");
    }

    /**
     * 组装Excel的方法, 通过该方法传入标题栏和数据体得到一个Excel07文件
     * 只组装Excel07，不做Excel03的处理
     *
     * @param head     标题栏
     * @param contents 数据集合
     * @return excel
     */
    public static <T> SXSSFWorkbook assembleExcelByList(List<String> head, List<T> contents) {
        // ---- 创建excel文件
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // ---- 创建工作簿
        Sheet sheet = workbook.createSheet();
        // ---- 设置默认列宽
        sheet.setDefaultColumnWidth(15);
        // ---- 创建标题行
        AtomicInteger index = new AtomicInteger();
        createHead(workbook, sheet, head, index);
        // ---- 创建数据行
        contents.forEach(content -> {
            Row row = sheet.createRow(index.getAndIncrement());
            for (int j = 0; j < head.size(); j++) {
                row.createCell(j).setCellValue(JsonUtil.pathText(JsonUtil.toJsonNode(JsonUtil.toString(content)), head.get(j)));
            }
        });
        return workbook;
    }

    /**
     * 组装Excel的方法, 通过该方法传入标题栏和数据体得到一个Excel07文件
     * 只组装Excel07，不做Excel03的处理
     *
     * @param head     标题栏
     * @param contents 数据集合
     * @return excel
     */
    public static SXSSFWorkbook assembleExcelByNode(List<String> head, JsonNode contents) {
        // ---- 创建excel文件
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        // ---- 创建工作簿
        Sheet sheet = workbook.createSheet();
        // ---- 设置默认列宽
        sheet.setDefaultColumnWidth(15);
        // ---- 创建标题行
        AtomicInteger index = new AtomicInteger();
        createHead(workbook, sheet, head, index);
        // ---- 创建数据行
        if (contents.isArray()) {
            contents.forEach(content -> {
                Row row = sheet.createRow(index.getAndIncrement());
                for (int j = 0; j < head.size(); j++) {
                    row.createCell(j).setCellValue(JsonUtil.pathText(content, head.get(j)));
                }
            });
        } else if (contents.isObject()) {
            Row row = sheet.createRow(index.getAndIncrement());
            for (int j = 0; j < head.size(); j++) {
                row.createCell(j).setCellValue(JsonUtil.pathText(contents, head.get(j)));
            }
        }
        return workbook;
    }

    private static void createHead(SXSSFWorkbook workbook, Sheet sheet, List<String> head, AtomicInteger index) {
        Row titleRow = sheet.createRow(index.getAndIncrement());
        for (int i = 0; i < head.size(); i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(head.get(i));
            cell.setCellStyle(getHeadCellStyle(workbook));
        }
    }

    /**
     * 组装标题头
     *
     * @param value 标题头
     * @return 标题头
     */
    public static List<String> assembleHead(String... value) {
        Assert.isTrue(ArrayUtil.isNotEmpty(value), "Excel headers cannot be empty");
        return Arrays.asList(value);
    }

    /**
     * 导出Excel
     *
     * @param workbook excel
     * @param fileName 文件名
     * @return 输出
     * @throws Exception e
     */
    public static ResponseEntity<Resource> exportExcel(SXSSFWorkbook workbook, String fileName) throws Exception {
        ByteArrayResource resource;
        // ===== response输出excel
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            resource = new ByteArrayResource(out.toByteArray());
        } finally {
            workbook.dispose();
        }
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(fileName, Encoding.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header(HttpHeaders.CONTENT_ENCODING, Encoding.UTF_8)
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDisposition)
                .body(resource);
    }

    /**
     * 读取Excel文件内容
     *
     * @param file Excel文件
     * @return 文件内容
     * @throws IOException e
     */
    public static List<String> analysisHead(Part file) throws IOException {
        List<String> result = new ArrayList<>();
        Boolean excelType = analysisExcelType(file);
        if (excelType == null) {
            return Collections.emptyList();
        }
        // ----- 读取文件
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = excelType ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return result;
            }
            Iterator<Row> row = sheet.rowIterator();
            if (!row.hasNext()) {
                return result;
            }
            // ----- 读取表头
            Iterator<Cell> cell = row.next().cellIterator();
            List<String> rowList = new ArrayList<>();
            while (cell.hasNext()) {
                result.add(cell.next().getStringCellValue());
            }
            return result;
        }
    }

    /**
     * 读取Excel文件内容
     *
     * @param file  Excel文件
     * @param index sheet位置
     * @return 文件内容
     * @throws IOException e
     */
    public static List<List<String>> analysisExcel(Part file, Integer index) throws IOException {
        List<List<String>> result = new ArrayList<>();
        Boolean excelType = analysisExcelType(file);
        if (excelType == null) {
            return Collections.emptyList();
        }
        // ----- 读取文件
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = excelType ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(index);
            if (sheet == null) {
                return result;
            }
            Iterator<Row> row = sheet.rowIterator();
            if (!row.hasNext()) {
                return result;
            }
            // ----- 去除表头
            row.next();
            // ----- 读取数据域
            while (row.hasNext()) {
                Iterator<Cell> cell = row.next().cellIterator();
                List<String> rowList = new ArrayList<>();
                while (cell.hasNext()) {
                    rowList.add(cell.next().getStringCellValue());
                }
                result.add(rowList);
            }
            return result;
        }
    }

    private static Boolean analysisExcelType(Part file) {
        // ----- 检查文件后缀, 是否是Excel
        String fileName = file.getSubmittedFileName();
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        FileType excelType = FileType.getTypeBySuffix(fileSuffix);
        return excelType == null ? null : FileType.EXCEL_07.getSuffix().equals(fileSuffix);
    }

    /**
     * 设置单元格样式
     *
     * @param workbook excel
     * @return 样式
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
