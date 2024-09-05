package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yanwu.spring.cloud.common.core.common.Encoding;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.Part;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XuBaofeng.
 * @date 2018-10-11 18:55.
 * <p>
 * description: Excel工具类，提供Excel07导出功能和Excel导入功能
 */
@Slf4j
@SuppressWarnings("unused")
public class ExcelUtil {
    public static final String CELL_NULL_VALUE = "--";

    private ExcelUtil() {
        throw new UnsupportedOperationException("ExcelUtil should never be instantiated");
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
                try {
                    Object value = ObjectUtil.fieldValue(content, head.get(j));
                    createCell(row, j, value == null ? CELL_NULL_VALUE : value);
                } catch (Exception e) {
                    createCell(row, j, CELL_NULL_VALUE);
                }
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
                    createCell(row, j, JsonUtil.pathText(content, head.get(j)));
                }
            });
        } else if (contents.isObject()) {
            Row row = sheet.createRow(index.getAndIncrement());
            for (int j = 0; j < head.size(); j++) {
                createCell(row, j, JsonUtil.pathText(contents, head.get(j)));
            }
        }
        return workbook;
    }

    /**
     * 创建表头
     *
     * @param workbook 单元格
     * @param sheet    单元格
     * @param heads    表头
     * @param index    角标
     */
    private static void createHead(SXSSFWorkbook workbook, Sheet sheet, List<String> heads, AtomicInteger index) {
        Row titleRow = sheet.createRow(index.getAndIncrement());
        for (int i = 0; i < heads.size(); i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(heads.get(i));
            cell.setCellStyle(getHeadCellStyle(workbook));
        }
    }

    /**
     * 创建数据域
     *
     * @param row   行
     * @param index 角标
     * @param value 值
     */
    private static void createCell(Row row, int index, Object value) {
        Cell cell = row.createCell(index);
        if (value == null) {
            cell.setCellValue(CELL_NULL_VALUE);
            return;
        }
        if (value instanceof Integer || value instanceof Long) {
            cell.setCellValue((long) value);
        } else if (value instanceof Double || value instanceof Float) {
            cell.setCellValue((double) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        } else if (value instanceof String) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue(value.toString());
        }
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
     * 将Excel写到本地磁盘
     *
     * @param workbook excel
     * @param filepath 文件路径
     * @throws Exception e
     */
    public static void writeExcel(SXSSFWorkbook workbook, String filepath) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            try (InputStream inputStream = new ByteArrayInputStream(out.toByteArray())) {
                FileUtil.deleteFile(filepath);
                FileUtil.write(inputStream, filepath);
            }
        } finally {
            workbook.dispose();
        }
    }

    /***
     * 读取Excel文件内容
     * @param filepath Excel文件绝对路径
     * @throws IOException e
     */
    public static <T> List<T> analysisExcel(String filepath, Class<T> clazz) throws IOException {
        if (StringUtils.isBlank(filepath)) {
            return Collections.emptyList();
        }
        return analysisExcel(new File(filepath), clazz);
    }

    /***
     * 读取Excel文件内容
     * @param path Excel文件绝对路径
     * @throws IOException e
     */
    public static <T> List<T> analysisExcel(Path path, Class<T> clazz) throws IOException {
        if (path == null) {
            return Collections.emptyList();
        }
        return analysisExcel(path.toFile(), clazz);
    }

    /***
     * 读取Excel文件内容
     * @param file Excel文件
     * @throws IOException e
     */
    public static <T> List<T> analysisExcel(File file, Class<T> clazz) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            return Collections.emptyList();
        }
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return analysisExcel(file.getName(), inputStream, clazz);
        }
    }

    /***
     * 读取Excel文件内容
     * @param file Excel文件
     * @return 文件内容
     * @throws IOException e
     */
    public static <T> List<T> analysisExcel(Part file, Class<T> clazz) throws IOException {
        String fileName = file.getSubmittedFileName();
        try (InputStream inputStream = file.getInputStream()) {
            return analysisExcel(fileName, inputStream, clazz);
        }
    }

    /***
     * 读取Excel文件内容
     * @param fileName    Excel文件名
     * @param inputStream Excel文件输入流
     * @throws IOException e
     */
    private static <T> List<T> analysisExcel(String fileName, InputStream inputStream, Class<T> clazz) throws IOException {
        List<T> contents = new ArrayList<>();
        // ----- 读取文件
        try (Workbook workbook = analysisWorkbook(fileName, inputStream)) {
            if (workbook == null) {
                return Collections.emptyList();
            }
            Sheet sheet = workbook.getSheetAt((0));
            if (sheet == null) {
                return Collections.emptyList();
            }
            Iterator<Row> row = sheet.rowIterator();
            if (!row.hasNext()) {
                return Collections.emptyList();
            }
            List<String> heads = new ArrayList<>();
            try {
                while (row.hasNext()) {
                    Row rowNext = row.next();
                    if (CollectionUtils.isEmpty(heads)) {
                        // ----- 读取表头
                        Iterator<Cell> cellIterator = rowNext.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            heads.add(cellStringValue(cell));
                        }
                    } else {
                        // ----- 读取数据域
                        ObjectNode nodes = JsonUtil.getMapper().createObjectNode();
                        for (int headIndex = 0; headIndex < heads.size(); headIndex++) {
                            Cell cell = rowNext.getCell(headIndex);
                            String field = heads.get(headIndex);
                            String value = cellStringValue(cell);
                            nodes.put(field, value);
                        }
                        // ----- 处理每一行Excel加到返回对象列表当中
                        contents.add(JsonUtil.convertObject(nodes, clazz));
                    }
                }
            } catch (Exception e) {
                log.error("analysis excel failed. row: {}", JsonUtil.toString(row), e);
            }
            return contents;
        }
    }

    private static Workbook analysisWorkbook(String filename, InputStream inputStream) throws IOException {
        // ----- 检查文件后缀, 是否是Excel
        if (StringUtils.isEmpty(filename)) {
            return null;
        }
        String fileSuffix = filename.substring(filename.lastIndexOf("."));
        FileType excelType = FileType.getTypeBySuffix(fileSuffix);
        if (excelType == null) {
            return null;
        }
        return FileType.EXCEL_07.getSuffix().equals(fileSuffix) ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
    }

    /**
     * 读取单元格内容并转换成string
     *
     * @param cell 单元格
     * @return 值
     */
    private static String cellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String value;
        CellType anEnum = cell.getCellType();
        switch (anEnum) {
            case NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case _NONE:
                value = CELL_NULL_VALUE;
                break;
            case STRING:
            default:
                value = cell.getStringCellValue();
        }
        return CELL_NULL_VALUE.equalsIgnoreCase(value) ? null : value;
    }

}
