package com.yanwu.spring.cloud.box.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yanwu.spring.cloud.box.bo.EncodeStrBO;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.ExcelUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.secret.Aes128Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    private static final Integer BYTE_SIZE = 10 * 1024 * 1024;
    private static final String DEFAULT_ENCODE_SECRET = "xbf_JS.2643";
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
    public ResponseEntity<Resource> jsonToExcel(@RequestPart(name = "file") Part file) throws Exception {
        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        StringBuilder fileContent = new StringBuilder();
        byte[] bytes = new byte[BYTE_SIZE];
        try (InputStream stream = file.getInputStream()) {
            while (stream.read(bytes) >= 0) {
                fileContent.append(new String(bytes, StandardCharsets.UTF_8));
            }
        }
        Set<String> fieldSet = JsonUtil.findAllField(fileContent.toString());
        if (CollectionUtils.isEmpty(fieldSet)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        SXSSFWorkbook workbook = ExcelUtil.assembleExcelByNode(new ArrayList<>(fieldSet), JsonUtil.toJsonNode(fileContent.toString()));
        String fileName = "jsonToExcel" + DateUtil.toTimeStr(System.currentTimeMillis(), DateUtil.DateFormat.YYYYMMDDHHMMSS) + FileType.EXCEL_07.getSuffix();
        return ExcelUtil.exportExcel(workbook, fileName);
    }

    @RequestHandler
    @PostMapping(value = "excelToJson")
    public ResponseEntity<Resource> excelToJson(@RequestPart(name = "file") Part file) throws Exception {
        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<ObjectNode> result = ExcelUtil.analysisExcel(file, ObjectNode.class);
        if (CollectionUtils.isEmpty(result)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String filename = "excelToJson" + DateUtil.toTimeStr(System.currentTimeMillis(), DateUtil.DateFormat.YYYYMMDDHHMMSS) + FileType.JSON.getSuffix();
        return FileUtil.exportContents(Result.success(result), filename);
    }

    @RequestHandler
    @PostMapping("stringEncode")
    public Result<String> stringEncode(@RequestBody EncodeStrBO param) {
        if (param == null || StringUtils.isBlank(param.getSource())) {
            return Result.failed("参数错误");
        }
        if (StringUtils.isBlank(param.getSecret())) {
            param.setSecret(DEFAULT_ENCODE_SECRET);
        }
        String message = Aes128Util.encryptToStr(param.getSource(), param.getSecret());
        return Result.success(message);
    }

    @RequestHandler
    @PostMapping("stringDecode")
    public Result<String> stringDecode(@RequestBody EncodeStrBO param) {
        if (param == null || StringUtils.isBlank(param.getSource())) {
            return Result.failed("参数错误");
        }
        if (StringUtils.isBlank(param.getSecret())) {
            param.setSecret(DEFAULT_ENCODE_SECRET);
        }
        String message = Aes128Util.decryptByStr(param.getSource(), param.getSecret());
        return Result.success(message);
    }

}
