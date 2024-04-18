package com.yanwu.spring.cloud.box.control;

import com.yanwu.spring.cloud.box.data.mapper.YanwuFileMapper;
import com.yanwu.spring.cloud.box.data.model.YanwuFile;
import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.yanwu.spring.cloud.common.utils.DateUtil.filling;

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
                YanwuFile yanwuFile = fileMapper.selectByMark(fileMark);
                if (yanwuFile != null) {
                    log.error("init file failed. because file mark is exists, file: {}, mark: {}", file.getAbsolutePath(), fileMark);
                    return;
                }
                Long createTime = FileUtil.readFileCreateTime(file);
                LocalDateTime datetime = DateUtil.datetime(createTime);
                YanwuFile instance = new YanwuFile();
                instance.setYear(String.valueOf(datetime.getYear()));
                instance.setMonth(filling(datetime.getMonthValue()));
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

    @RequestHandler
    @GetMapping("read/{id}")
    public ResponseEntity<Resource> read(@PathVariable("id") Integer fileId, HttpServletResponse response) throws Exception {
        if (fileId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        YanwuFile yanwuFile = fileMapper.selectById(fileId);
        if (yanwuFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File file = new File(yanwuFile.getPath());
        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (fileAttributes.isRegularFile()) {
            if (fileAttributes.size() < FileUtil.LIMIT_SIZE) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaTypeEnum.getContentType(yanwuFile.getType()))
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                        .body(new ByteArrayResource(Files.readAllBytes(file.toPath())));
            } else {
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaTypeEnum.getContentType(yanwuFile.getType()));
                response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
                // ----- 使用输出流往客户端写出文件
                long position = 0, length = file.length();
                try (OutputStream outputStream = response.getOutputStream()) {
                    while (position < length) {
                        int blockSize = (int) Math.min(FileUtil.SIZE, length - position);
                        byte[] bytes = FileUtil.read(file.getPath(), position, blockSize);
                        outputStream.write(bytes);
                        outputStream.flush();
                        if (FileUtil.SPEED < 1000L * bytes.length) {
                            ThreadUtil.sleep(Math.floorDiv(1000L * bytes.length, FileUtil.SPEED));
                        }
                        position += blockSize;
                    }
                }
                return ResponseEntity.ok().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Getter
    private enum MediaTypeEnum {

        PHOTO_JPG("image/jpeg", ".jpeg", ".PNG", ".jpg"),
        VIDEO_MP4("video/mp4", ".MOV", ".mp4"),
        DEFAULT_TYPE(MediaType.APPLICATION_OCTET_STREAM_VALUE),
        ;
        private final String[] fileType;
        private final String contentType;

        MediaTypeEnum(String contentType, String... fileType) {
            this.fileType = fileType;
            this.contentType = contentType;
        }

        private static String getContentType(String filename) {
            if (StringUtils.isBlank(filename)) {
                return DEFAULT_TYPE.getContentType();
            }
            for (MediaTypeEnum value : MediaTypeEnum.values()) {
                if (value.getFileType() == null) {
                    continue;
                }
                for (String item : value.getFileType()) {
                    if (filename.toUpperCase().endsWith(item.toUpperCase())) {
                        return value.getContentType();
                    }
                    if (item.toUpperCase().endsWith(filename.toUpperCase())) {
                        return value.getContentType();
                    }
                }
            }
            return DEFAULT_TYPE.getContentType();
        }

    }
}
