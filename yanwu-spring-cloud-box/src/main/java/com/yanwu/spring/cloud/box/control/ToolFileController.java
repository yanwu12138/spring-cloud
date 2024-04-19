package com.yanwu.spring.cloud.box.control;

import com.yanwu.spring.cloud.box.data.mapper.YanwuFileMapper;
import com.yanwu.spring.cloud.box.data.model.YanwuFile;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.pojo.BaseParam;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author XuBaofeng.
 * @date 2024/4/18 10:56.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("/file/")
public class ToolFileController {
    private static final String PARENT_DIR = "/home/photo/";
    public static final String URL_PATH = "http://114.55.74.43:12138/";
    private static final AtomicBoolean INIT_LOCK = new AtomicBoolean(Boolean.FALSE);

    @javax.annotation.Resource
    private YanwuFileMapper fileMapper;
    @javax.annotation.Resource
    private Executor commonsExecutors;

    @RequestHandler
    @GetMapping("init")
    public Result<Void> init() {
        File rootDir = new File(PARENT_DIR);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return Result.failed();
        }
        File[] files = rootDir.listFiles();
        if (files == null || files.length == 0) {
            return Result.failed();
        }
        if (INIT_LOCK.get()) {
            return Result.failed();
        }
        INIT_LOCK.set(Boolean.TRUE);
        commonsExecutors.execute(() -> {
            for (File item : files) {
                if (item == null) {
                    continue;
                }
                readFile(item);
            }
            INIT_LOCK.set(Boolean.FALSE);
        });
        return Result.success();
    }

    private void readFile(File file) {
        if (file.isFile()) {
            try {
                String fileMark = FileUtil.calcFileMd5(file);
                YanwuFile yanwuFile = fileMapper.selectByMark(file.getAbsolutePath(), fileMark);
                if (yanwuFile != null) {
                    log.error("init file failed. because file mark is exists, file: {}, mark: {}", file.getAbsolutePath(), fileMark);
                    return;
                }
                YanwuFile instance = new YanwuFile();
                instance.setYear(readFileYear(file.getName()));
                instance.setMonth(readFileMonth(file.getName()));
                instance.setPath(file.getAbsolutePath());
                instance.setUrl(file.getAbsolutePath().replace("/home/", URL_PATH));
                instance.setMark(fileMark);
                instance.setType(FileUtil.getSuffix(file.getName()));
                fileMapper.insert(instance);
            } catch (Exception e) {
                log.error("init file failed. file: {}", file.getAbsolutePath(), e);
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File item : files) {
                if (item != null) {
                    readFile(item);
                }
            }
        }
    }

    private static String readFileYear(String filename) {
        String[] split = filename.split(DateUtil.DASHES);
        return split[0];
    }

    private static String readFileMonth(String filename) {
        String[] split = filename.split(DateUtil.DASHES);
        return split[1];
    }

    @RequestHandler
    @PostMapping("allYear")
    public Result<List<String>> allYear() {
        List<String> allYear = fileMapper.allYear();
        return CollectionUtils.isNotEmpty(allYear) ? Result.success(allYear) : Result.success(Collections.emptyList());
    }

    @RequestHandler
    @PostMapping(value = {"allMonth", "allMonth/{year}"})
    public Result<List<String>> allMonth(@PathVariable(value = "year", required = false) String year) {
        if (StringUtils.isEmpty(year)) {
            return Result.success(Collections.emptyList());
        }
        List<String> allMonth = fileMapper.allMonth(year);
        return CollectionUtils.isNotEmpty(allMonth) ? Result.success(allMonth) : Result.success(Collections.emptyList());
    }

    @RequestHandler
    @PostMapping(value = {"last", "last/{id}"})
    public Result<FindFileVO> last(@PathVariable(value = "id", required = false) Long fileId, @RequestBody(required = false) BaseParam<FindFileVO> param) {
        if (fileId == null || fileId <= 0L) {
            fileId = Long.MAX_VALUE;
        }
        String year = param == null || param.getData() == null ? null : param.getData().getYear();
        String month = param == null || param.getData() == null ? null : param.getData().getMonth();
        YanwuFile last = lastFile(fileId, year, month);
        if (last == null) {
            long maxId = Long.MAX_VALUE;
            last = lastFile(maxId, year, month);
        }
        return last != null && last.getId() != null ? Result.success(JsonUtil.convertObject(last, FindFileVO.class)) : Result.failed();
    }

    private YanwuFile lastFile(Long fileId, String year, String month) {
        if (StringUtils.isNoneBlank(year) && StringUtils.isNoneBlank(month)) {
            return fileMapper.lastByYearAndMonth(fileId, year, month);
        } else if (StringUtils.isNoneBlank(year)) {
            return fileMapper.lastByIdAndYear(fileId, year);
        } else if (StringUtils.isNoneBlank(month)) {
            return fileMapper.lastByIdAndMonth(fileId, month);
        } else {
            return fileMapper.lastById(fileId);
        }
    }

    @RequestHandler
    @PostMapping(value = {"next", "next/{id}"})
    public Result<FindFileVO> next(@PathVariable(value = "id", required = false) Long fileId, @RequestBody(required = false) BaseParam<FindFileVO> param) {
        if (fileId == null) {
            fileId = 0L;
        }
        String year = param == null || param.getData() == null ? null : param.getData().getYear();
        String month = param == null || param.getData() == null ? null : param.getData().getMonth();
        YanwuFile next = nextFile(fileId, year, month);
        if (next == null) {
            long minId = -1L;
            next = nextFile(minId, year, month);
        }
        return next != null && next.getId() != null ? Result.success(JsonUtil.convertObject(next, FindFileVO.class)) : Result.failed();
    }

    private YanwuFile nextFile(Long fileId, String year, String month) {
        if (StringUtils.isNoneBlank(year) && StringUtils.isNoneBlank(month)) {
            return fileMapper.nextByYearAndMonth(fileId, year, month);
        } else if (StringUtils.isNoneBlank(year)) {
            return fileMapper.nextByIdAndYear(fileId, year);
        } else if (StringUtils.isNoneBlank(month)) {
            return fileMapper.nextByIdAndMonth(fileId, month);
        } else {
            return fileMapper.nextById(fileId);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class FindFileVO implements Serializable {
        private static final long serialVersionUID = -4452800943719601968L;
        private String year;
        private String month;
        private String path;
        private String url;
        private String mark;
        private String type;
    }

}
