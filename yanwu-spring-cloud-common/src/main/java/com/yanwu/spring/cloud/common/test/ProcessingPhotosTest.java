package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.utils.CommandUtil;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XuBaofeng.
 * @date 2024/8/19 15:59.
 * <p>
 * description:
 */
@Slf4j
public class ProcessingPhotosTest {

    private static final String SOURCE_PATH = "/Users/xubaofeng/yanwu/file/未上传/";
    private static final String TARGET_PATH = "/Volumes/YanwuXu/照片";

    public static void main(String[] args) throws Exception {
        File sourcePath = new File(SOURCE_PATH);
        if (!sourcePath.exists() || !sourcePath.isDirectory()) {
            return;
        }
        File[] files = sourcePath.listFiles();
        if (files == null) {
            return;
        }
        final Map<String, String> duplicateMark = new HashMap<>();
        readPathDuplicateMark(new File(TARGET_PATH), duplicateMark);
        for (File sourceFile : files) {
            String itemMd5 = FileUtil.calcFileMd5(sourceFile);
            if (duplicateMark.containsKey(itemMd5)) {
                log.info("duplicate mark: {}, file: {}", duplicateMark.get(itemMd5), sourceFile.getAbsolutePath());
                continue;
            }
            Long createTime = FileUtil.readFileCreateTime(sourceFile);
            if (createTime == null || createTime <= 0) {
                createTime = System.currentTimeMillis();
            }
            LocalDateTime datetime = DateUtil.datetime(createTime);
            String fileName = sourceFile.getName();
            if (fileName.contains(" ")) {
                fileName = fileName.replaceAll(" ", "");
            }
            if (fileName.contains("(")) {
                fileName = fileName.replace("(", "_");
            }
            if (fileName.contains(")")) {
                fileName = fileName.replace(")", "");
            }
            String targetPath = String.join(File.separator, TARGET_PATH, String.valueOf(datetime.getYear()), DateUtil.filling(datetime.getMonthValue()), fileName);
            if (createPath(targetPath)) {
                String command = "mv -f " + sourceFile.getPath() + " " + targetPath;
                log.info("file: {}, command: [{}], result: [{}]", sourceFile.getPath(), command, CommandUtil.execCommand(command));
            }
        }
    }

    private static boolean createPath(String targetPath) {
        if (StringUtils.isBlank(targetPath)) {
            return false;
        }
        return FileUtil.checkDirectoryPath(new File(targetPath).getParentFile());
    }

    private static void readPathDuplicateMark(File itemFile, Map<String, String> duplicateMark) throws Exception {
        if (itemFile == null || !itemFile.exists()) {
            return;
        }
        if (itemFile.isDirectory()) {
            File[] itemFiles = itemFile.listFiles();
            if (itemFiles == null || itemFiles.length == 0) {
                return;
            }
            for (File itemChild : itemFiles) {
                readPathDuplicateMark(itemChild, duplicateMark);
            }
        } else if (itemFile.isFile()) {
            String itemMd5 = FileUtil.calcFileMd5(itemFile);
            if (StringUtils.isBlank(itemMd5)) {
                return;
            }
            if (!duplicateMark.containsKey(itemMd5)) {
                duplicateMark.put(itemMd5, itemFile.getPath());
                return;
            }
            log.info("remove duplicate file, md5: {}, source: {}, item: {}", itemMd5, duplicateMark.get(itemMd5), itemFile.getPath());
        }
    }

}
