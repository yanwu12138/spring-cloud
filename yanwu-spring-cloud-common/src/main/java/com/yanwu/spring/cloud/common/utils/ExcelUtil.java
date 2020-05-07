package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.extern.slf4j.Slf4j;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2018-10-11 18:55.
 * <p>
 * description: Excel工具类，提供Excel07导出功能和Excel导入功能
 */
@Slf4j
public class ExcelUtil {

    /**
     * 组装Excel的方法, 通过该方法传入标题栏和数据体得到一个Excel07文件
     * 只组装Excel07，不做Excel03的处理
     *
     * @param head     标题栏
     * @param contents 数据集合
     * @return excel
     */
    public static SXSSFWorkbook assembleExcel(List<String> head, List<List<String>> contents) {
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
     * 读取Excel文件内容
     *
     * @param file  Excel文件
     * @param index sheet位置
     * @return 文件内容
     * @throws IOException e
     */
    public static List<List<String>> analysisExcel(Part file, Integer index) throws IOException {
        List<List<String>> result = new ArrayList<>();
        String fileName = file.getSubmittedFileName();
        // ----- 检查文件后缀, 是否是Excel
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (!FileType.EXCEL_07.getSuffix().equals(fileSuffix) && !FileType.EXCEL_03.getSuffix().equals(fileSuffix)) {
            return result;
        }
        // ----- 读取文件
        boolean flag = FileType.EXCEL_07.getSuffix().equals(fileSuffix);
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = flag ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(index);
            if (sheet == null) {
                return result;
            }
            Iterator<Row> row = sheet.rowIterator();
            // ----- 去除标题头
            if (!row.hasNext()) {
                return result;
            }
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
