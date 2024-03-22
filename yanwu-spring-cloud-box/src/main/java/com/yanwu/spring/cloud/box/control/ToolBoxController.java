package com.yanwu.spring.cloud.box.control;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author XuBaofeng.
 * @date 2024/3/22 12:27.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/tool/")
public class ToolBoxController {
    private static final Random RANDOM = new Random();
    private static final Integer DEFAULT_LENGTH = 24;
    private static final String PASSWORD_KEY = "J3mqGev7!SlytCTLEzc1g-ZYxD=QIRWkKUjF+d9hi4a6B8pPwr5AMo2fV^nNsbuH0X";

    @RequestHandler
    @PostMapping("randomPassword")
    public Result<String> randomPassword(@RequestBody BaseParam<Integer> param) {
        Integer size = param.getData();
        if (size == null || size < DEFAULT_LENGTH) {
            size = DEFAULT_LENGTH;
        }
        StringBuilder buffer = new StringBuilder();
        do {
            int index = RANDOM.nextInt(PASSWORD_KEY.length());
            buffer.append(PASSWORD_KEY.charAt(index));
            size--;
        } while (size > 0);
        return Result.success(buffer.toString());
    }

    @RequestHandler
    @PostMapping("stampToTime")
    public Result<String> stampToTime(@RequestBody BaseParam<Long> param) {
        Long timestamp = param.getData();
        if (timestamp == null || timestamp <= 0) {
            timestamp = System.currentTimeMillis();
        }
        String timeStr = DateUtil.toTimeStr(timestamp, DateUtil.DateFormat.YYYY_MM_DD_HH_MM_SS);
        return Result.success(timeStr);
    }

    @RequestHandler
    @PostMapping("timeToStamp")
    public Result<Long> timeToStamp(@RequestBody BaseParam<String> param) throws Exception {
        String timeStr = param.getData();
        if (StringUtils.isBlank(timeStr)) {
            BaseParam<Long> localtime = new BaseParam<>();
            timeStr = stampToTime(localtime).getData();
        }
        Long timestamp = DateUtil.toTimeLong(timeStr, DateUtil.DateFormat.YYYY_MM_DD_HH_MM_SS);
        return Result.success(timestamp);
    }

    @RequestHandler
    @PostMapping(value = "jsonToExcel")
    public ResponseEntity<Resource> jsonToExcel(MultipartHttpServletRequest request) throws Exception {
        List<MultipartFile> fileList = request.getMultiFileMap().get("file");
        if (CollectionUtils.isEmpty(fileList)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<MultipartFile> anyOptional = fileList.parallelStream().findAny();
        if (!anyOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String fileContent = new String(anyOptional.get().getBytes(), StandardCharsets.UTF_8);
        Set<String> fieldSet = JsonUtil.findAllField(fileContent);
        if (CollectionUtils.isEmpty(fieldSet)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        SXSSFWorkbook workbook = ExcelUtil.assembleExcelByNode(new ArrayList<>(fieldSet), JsonUtil.toJsonNode(fileContent));
        String fileName = "jsonToExcel" + DateUtil.toTimeStr(System.currentTimeMillis(), DateUtil.DateFormat.YYYYMMDDHHMMSS) + FileType.EXCEL_07.getSuffix();
        return ExcelUtil.exportExcel(workbook, fileName);
    }


    @PostMapping(value = "excelToJson")
    public Result<String> excelToJson(MultipartHttpServletRequest request) {
        List<MultipartFile> fileList = request.getMultiFileMap().get("file");
        if (CollectionUtils.isEmpty(fileList)) {
            return Result.failed("文件为空");
        }
        return Result.success("文件不为空");
    }

}
