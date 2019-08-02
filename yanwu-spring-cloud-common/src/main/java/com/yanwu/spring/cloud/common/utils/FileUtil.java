package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.TimeStringFormat;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.core.exception.ExceptionDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author XuBaofeng.
 * @date 2018/6/12.
 */
@Slf4j
public class FileUtil {
    /**
     * 将sourceFilePath目录下所有文件打包:
     * 名称为: fileName到zipFilePath目录下
     *
     * @param sourceFilePath
     * @param zipFilePath
     * @param fileName
     * @return
     * @throws Exception
     */
    public static void fileToZip(String sourceFilePath, String zipFilePath, String fileName) throws Exception {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            log.info("{} >>>> is not exists", sourceFilePath);
            throw new BusinessException(ExceptionDefinition.FILE_PATH_NOT_EXISTS.code, ExceptionDefinition.FILE_PATH_NOT_EXISTS.key);
        }
        File file = new File(zipFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
        if (zipFile.exists()) {
            // ===== zip文件存在, 将原有文件删除
            zipFile.delete();
        }
        // ===== zip文件不存在, 打包
        pushZip(sourceFile, sourceFilePath, zipFile);
    }

    private static void pushZip(File sourceFile, String sourceFilePath, File zipFile) throws Exception {
        FileInputStream fis;
        BufferedInputStream bis = null;
        FileOutputStream fos;
        ZipOutputStream zos = null;
        try {
            File[] sourceFiles = sourceFile.listFiles();
            if (sourceFiles == null || sourceFiles.length < 1) {
                log.info("{} >>>> is null", sourceFilePath);
            } else {
                fos = new FileOutputStream(zipFile);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                byte[] bytes = new byte[1024 * 10];
                for (int i = 0; i < sourceFiles.length; i++) {
                    // ===== 创建ZIP实体，并添加进压缩包  
                    ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                    zos.putNextEntry(zipEntry);
                    // ===== 读取待压缩的文件并写进压缩包里  
                    fis = new FileInputStream(sourceFiles[i]);
                    bis = new BufferedInputStream(fis, 10240);
                    int read = 0;
                    while ((read = bis.read(bytes, 0, 10240)) != -1) {
                        zos.write(bytes, 0, read);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != zos) {
                    zos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // ===== 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.info("delete directory [ {} ] success！", fileName);
                return true;
            } else {
                log.info("delete directory [ {} ] failed！", fileName);
                return false;
            }
        } else {
            log.info("delete directory failed: directory >> [ {} ] non-existent！", fileName);
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // ===== 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // ===== 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            log.info("delete directory failed: directory >> [ {} ] non-existent！", dir);
            return false;
        }
        boolean flag = true;
        // ===== 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // ===== 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else if (files[i].isDirectory()) {
                // ===== 删除子目录
                flag = FileUtil.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            log.info("delete directory failed！");
            return false;
        }
        // ===== 删除当前目录
        if (dirFile.delete()) {
            log.info("delete directory [ {} ] success！", dir);
            return true;
        } else {
            return false;
        }
    }

    public static String getFileNameByType(String fileName, FileType fileType) throws Exception {
        CheckParamUtil.checkStringNotBlank(fileName);
        StringBuffer buffer = new StringBuffer("downloadExcel");
        buffer.append(DataUtil.getTimeString(System.currentTimeMillis(), TimeStringFormat.YYYY_MM_DD1));
        switch (fileType) {
            case WORD:
                buffer.append(".docx");
                break;
            case EXCEL:
                buffer.append(".xlsx");
                break;
            case PPT:
                buffer.append(".pptx");
                break;
            case PDF:
                buffer.append(".pdf");
                break;
            case SQL:
                buffer.append(".sql");
                break;
            case TXT:
                buffer.append(".txt");
                break;
            case JSON:
                buffer.append(".json");
                break;
            default:
                break;
        }
        return buffer.toString();
    }

    public static FileType getFileTypeByName(String fileName) throws Exception {
        CheckParamUtil.checkStringNotBlank(fileName);
        if (fileName.contains(".")) {
            String[] split = fileName.split("\\.");
            if (ArrayUtil.isNotEmpty(split)) {
                switch (split[split.length - 1].toLowerCase()) {
                    case "doc":
                        return FileType.WORD;
                    case "docx":
                        return FileType.WORD;
                    case "xls":
                        return FileType.EXCEL;
                    case "xlsx":
                        return FileType.EXCEL;
                    case "ppt":
                        return FileType.PPT;
                    case "pptx":
                        return FileType.PPT;
                    case "pdf":
                        return FileType.PDF;
                    case "json":
                        return FileType.JSON;
                    case "sql":
                        return FileType.SQL;
                    case "txt":
                        return FileType.TXT;
                    default:
                        return FileType.OTHERS;
                }
            }
        }
        return FileType.OTHERS;
    }

    public static String getNameByFileName(String fileName) {
        CheckParamUtil.checkStringNotBlank(fileName);
        String name = "";
        if (fileName.contains(".")) {
            name = fileName.split("\\.")[0];
        }
        return name;
    }

    public static ResponseEntity<Resource> exportFile(String filePath, String fileName) throws Exception {
        FileSystemResource file = new FileSystemResource(filePath);
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDisposition)
                .body(new InputStreamResource(file.getInputStream()));
    }
}
