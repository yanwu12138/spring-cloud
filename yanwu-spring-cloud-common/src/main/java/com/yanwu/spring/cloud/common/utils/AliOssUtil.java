package com.yanwu.spring.cloud.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.yanwu.spring.cloud.common.pojo.OssProperties;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.yanwu.spring.cloud.common.utils.DateUtil.filling;

/**
 * @author Baofeng Xu
 * @date 2020/9/24 10:08.
 * <p>
 * description: 阿里云OSS文件长传下载工具
 */
@Slf4j
@SuppressWarnings("unused")
public class AliOssUtil {

    private static OSS ossClient = null;
    private static final AliOssUtil instance = new AliOssUtil();

    private AliOssUtil() {
    }

    public static synchronized AliOssUtil init(OssProperties ossProperties) {
        if (ossClient == null) {
            ossClient = buildClient(ossProperties);
        }
        return instance;
    }

    /**
     * 上传本地文件到OSS
     *
     * @param bucket   OSS 桶
     * @param filePath 本地文件路径
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public Result<String> upload(String bucket, String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return Result.failed("OSS upload failed: filePath is blank.");
        }
        return upload(bucket, new File(filePath));
    }

    /**
     * 上传本地文件到OSS
     *
     * @param bucket OSS 桶
     * @param file   文件
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public Result<String> upload(String bucket, File file) throws Exception {
        if (!FileUtil.fileExists(file) || !file.isFile()) {
            return Result.failed("OSS upload failed: file is not exists or file is not file.");
        }
        try (InputStream is = Files.newInputStream(file.toPath())) {
            return upload(bucket, is, file.getName());
        }
    }

    /**
     * 上传本地文件到OSS
     *
     * @param bucket   OSS 桶
     * @param is       输入流
     * @param fileName 本地文件名称
     * @return 上传结果
     */
    public Result<String> upload(String bucket, InputStream is, String fileName) {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS upload failed: bucket is blank.");
        }
        String urlPath = randomFilePath(fileName);
        ossClient.putObject(bucket, urlPath, is);
        return checkExist(exist(bucket, urlPath)) ? Result.success(urlPath) : Result.failed();
    }

    /**
     * 删除OSS文件
     *
     * @param bucket  OSS 桶
     * @param fileUrl OSS fileUrl
     * @return data: [true: 删除成功; false: 删除失败]
     */
    public Result<Boolean> delete(String bucket, String fileUrl) {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS delete failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return Result.failed("OSS delete failed: fileUrl is blank.");
        }
        ossClient.deleteObject(bucket, fileUrl);
        return checkExist(exist(bucket, fileUrl)) ? Result.success(Boolean.FALSE) : Result.success(Boolean.TRUE);
    }

    /**
     * 批量删除OSS文件
     *
     * @param bucket   OSS 桶
     * @param fileUrls OSS fileUrls
     * @return 未被删除掉的文件
     */
    public Result<List<String>> deletes(String bucket, List<String> fileUrls) {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS deletes failed: bucket is blank.");
        }
        if (CollectionUtils.isEmpty(fileUrls)) {
            return Result.failed(fileUrls, "OSS deletes failed: fileUrls is empty.");
        }
        ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(fileUrls));
        List<String> result = new ArrayList<>();
        fileUrls.forEach(url -> {
            if (checkExist(exist(bucket, url))) {
                result.add(url);
            }
        });
        return result.size() < fileUrls.size() ? Result.success(result) : Result.failed(result);
    }

    /**
     * 递归删除OSS目录到本地
     *
     * @param bucket OSS 桶
     * @param prefix 待删除目录的完整路径，完整路径中不包含Bucket名称
     */
    public Result<Void> deleteDir(String bucket, String prefix) {
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(bucket).withPrefix(prefix).withMarker(nextMarker));
            if (objectListing == null) {
                return Result.failed("OSS deleteDir failed: objectListing is empty.");
            }
            if (objectListing.getObjectSummaries().isEmpty()) {
                return Result.success();
            }
            for (OSSObjectSummary item : objectListing.getObjectSummaries()) {
                try {
                    delete(bucket, item.getKey());
                } catch (Exception e) {
                    log.error("download dir failed: {}", item.getKey(), e);
                }
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        return Result.success();
    }

    /**
     * 根据OSS fileUrls判断文件是否存在
     *
     * @param bucket  OSS 桶
     * @param fileUrl OSS fileUrls
     * @return [true: 存在; false: 不存在]
     */
    public Result<Boolean> exist(String bucket, String fileUrl) {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS exist failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return Result.failed("OSS exist failed: fileUrl is blank.");
        }
        return Result.success(ossClient.doesObjectExist(bucket, fileUrl));
    }

    /**
     * 文件重命名
     *
     * @param bucket OSS 桶
     * @param source 源文件名
     * @param target 目标文件名
     */
    public Result<Boolean> rename(String bucket, String source, String target) {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS rename failed: bucket is blank.");
        }
        if (StringUtils.isBlank(source)) {
            return Result.failed("OSS rename failed: source is blank.");
        }
        if (StringUtils.isBlank(target)) {
            return Result.failed("OSS rename failed: target is blank.");
        }
        ossClient.copyObject(bucket, source, bucket, target);
        if (!exist(bucket, target).successful()) {
            return Result.failed("OSS rename failed: because copy object failed.");
        }
        ossClient.deleteObject(bucket, source);
        return checkExist(exist(bucket, source)) ? Result.success(Boolean.FALSE) : Result.success(Boolean.TRUE);
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     *
     * @param bucket     OSS 桶
     * @param fileUrl    OSS fileUrl
     * @param targetPath 本地文件路径
     * @throws Exception Exception.class
     */
    public Result<Void> download(String bucket, String fileUrl, String targetPath) throws Exception {
        return download(bucket, fileUrl, new File(targetPath));
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     *
     * @param bucket  OSS 桶
     * @param fileUrl OSS fileUrl
     * @param file    本地文件
     */
    public Result<Void> download(String bucket, String fileUrl, File file) throws Exception {
        if (StringUtils.isBlank(bucket)) {
            return Result.failed("OSS download failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return Result.failed("OSS download failed: targetPath is blank.");
        }
        if (FileUtil.fileExists(file) && file.isDirectory()) {
            file = new File((file.getPath() + File.separator + fileUrl.substring(fileUrl.lastIndexOf(File.separator))));
        }
        if (FileUtil.fileExists(file) && file.isFile() && !FileUtil.deleteFile(file)) {
            return Result.failed("OSS download failed: delete file error.");
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return Result.failed("OSS download failed: parent mkdir error.");
        }
        if (!file.createNewFile()) {
            return Result.failed("OSS download failed: create new file error.");
        }
        if (!checkExist(exist(bucket, fileUrl))) {
            return Result.failed("OSS download failed: remote file does not exist. fileUrl: " + fileUrl);
        }
        ossClient.getObject(new GetObjectRequest(bucket, fileUrl), file);
        return Result.success();
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     * 并校验文件的MD5值
     *
     * @param bucket     OSS 桶
     * @param fileUrl    OSS fileUrl
     * @param targetPath 本地文件路径
     * @param md5        文件的MD5值
     */
    public Result<Void> download(String bucket, String fileUrl, String targetPath, String md5) throws Exception {
        return download(bucket, fileUrl, new File(targetPath), md5);
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     * 并校验文件的MD5值
     *
     * @param bucket  OSS 桶
     * @param fileUrl OSS fileUrl
     * @param file    本地文件
     * @param md5     文件的MD5值
     */
    public Result<Void> download(String bucket, String fileUrl, File file, String md5) throws Exception {
        Result<Void> download = download(bucket, fileUrl, file);
        if (!download.getStatus()) {
            return download;
        }
        if (!FileUtil.checkFileMd5(file, md5)) {
            log.error("download file failed, because md5 check failed.");
            FileUtil.deleteFile(file);
            return Result.failed("file md5 value verification failed.");
        }
        log.info("download file check md5 success, file: {}, md5: {}", file.getPath(), md5);
        return download;
    }

    /**
     * 下载OSS目录到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     *
     * @param bucket    OSS 桶
     * @param prefix    待下载目录的完整路径，完整路径中不包含Bucket名称
     * @param targetDir 本地目录路径
     */
    public Result<Void> downloadDir(String bucket, String prefix, String targetDir) {
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(bucket).withPrefix(prefix).withMarker(nextMarker));
            if (objectListing == null) {
                return Result.failed("OSS downloadDir failed: objectListing is empty.");
            }
            if (objectListing.getObjectSummaries().isEmpty()) {
                return Result.success();
            }
            for (OSSObjectSummary item : objectListing.getObjectSummaries()) {
                try {
                    download(bucket, item.getKey(), (targetDir + item.getKey()));
                } catch (Exception e) {
                    log.error("download dir failed: {}", item.getKey(), e);
                }
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        return Result.success();
    }

    /**
     * 根据OSS配置获取OSS客户端
     *
     * @param properties OSS配置
     * @return OssClient.class
     */
    private static OSS buildClient(OssProperties properties) {
        return checkOssProperties(properties) ? new OSSClientBuilder().build(properties.getEndpoint(),
                properties.getAccessKeyId(), properties.getAccessKeySecret()) : null;
    }

    /**
     * 检验OSS参数
     *
     * @param properties OSS参数
     * @return [true: 合法; false: 不合法]
     */
    private static boolean checkOssProperties(OssProperties properties) {
        if (StringUtils.isBlank(properties.getAccessKeyId())) {
            return false;
        }
        if (StringUtils.isBlank(properties.getAccessKeySecret())) {
            return false;
        }
        return !StringUtils.isBlank(properties.getEndpoint());
    }

    /**
     * 根据文件名获取一个随机的文件路径
     *
     * @param fileName 文件名
     * @return 随机文件路径
     */
    private String randomFilePath(String fileName) {
        LocalDateTime date = LocalDateTime.now();
        return date.getYear() + File.separator + filling(date.getMonthValue()) + File.separator +
                filling(date.getDayOfMonth()) + File.separator + filling(date.getHour()) + filling(date.getMinute()) +
                filling(date.getSecond()) + filling(date.getNano()) + "_" + fileName;
    }

    /**
     * 校验是否 exist() 函数结果
     *
     * @param exist exist() 函数结果
     * @return [true: 成功; false: 失败]
     */
    private boolean checkExist(Result<Boolean> exist) {
        return exist.getStatus() && exist.getData();
    }

}
