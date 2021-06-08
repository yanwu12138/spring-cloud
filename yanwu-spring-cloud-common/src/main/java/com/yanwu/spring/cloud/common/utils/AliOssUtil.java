package com.yanwu.spring.cloud.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.yanwu.spring.cloud.common.core.enums.OssFileTypeEnum;
import com.yanwu.spring.cloud.common.pojo.OssProperties;
import com.yanwu.spring.cloud.common.pojo.OssResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    public static final String SEPARATOR = "/";

    private AliOssUtil() {
        throw new UnsupportedOperationException("AliOssUtil should never be instantiated");
    }

    /**
     * 上传本地文件到OSS
     *
     * @param properties OSS 相关配置
     * @param type       文件类型 [@SEE: OssFileTypeEnum]
     * @param filePath   本地文件路径
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public static OssResult<String> upload(OssProperties properties, OssFileTypeEnum type, String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return OssResult.failed("OSS upload failed: filePath is blank.");
        }
        return upload(properties, type, new File(filePath));
    }

    /**
     * 上传本地文件到OSS
     *
     * @param properties OSS 相关配置
     * @param type       文件类型 [@SEE: OssFileTypeEnum]
     * @param file       文件
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public static OssResult<String> upload(OssProperties properties, OssFileTypeEnum type, File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            return OssResult.failed("OSS upload failed: file is not exists or file is not file.");
        }
        try (InputStream is = new FileInputStream(file)) {
            return upload(properties, is, type, file.getName());
        }
    }

    /**
     * 上传本地文件到OSS, 该方法不会释放 is 资源
     *
     * @param properties OSS 相关配置
     * @param is         输入流
     * @param type       文件类型 [@SEE: OssFileTypeEnum]
     * @param fileName   本地文件名称
     * @return 上传结果
     */
    public static OssResult<String> upload(OssProperties properties, InputStream is, OssFileTypeEnum type, String fileName) {
        OSS ossClient = null;
        try {
            ossClient = buildClient(properties);
            if (ossClient == null) {
                return OssResult.failed("OSS properties configuration error. properties: " + properties.toString());
            }
            return upload(ossClient, properties.getBucket(), is, type, fileName);
        } finally {
            closeClient(ossClient);
        }
    }

    /**
     * 上传本地文件到OSS, 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param type      文件类型 [@SEE: OssFileTypeEnum]
     * @param filePath  本地文件路径
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public static OssResult<String> upload(OSS ossClient, String bucket, OssFileTypeEnum type, String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            return OssResult.failed("OSS upload failed: filePath is blank.");
        }
        return upload(ossClient, bucket, type, new File(filePath));
    }

    /**
     * 上传本地文件到OSS, 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param type      文件类型 [@SEE: OssFileTypeEnum]
     * @param file      文件
     * @return 上传结果
     * @throws Exception Exception.class
     */
    public static OssResult<String> upload(OSS ossClient, String bucket, OssFileTypeEnum type, File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            return OssResult.failed("OSS upload failed: file is not exists or file is not file.");
        }
        try (InputStream is = new FileInputStream(file)) {
            return upload(ossClient, bucket, is, type, file.getName());
        }
    }

    /**
     * 上传本地文件到OSS, 该方法不会释放 ossClient && is 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param is        输入流
     * @param type      文件类型 [@SEE: OssFileTypeEnum]
     * @param fileName  本地文件名称
     * @return 上传结果
     */
    public static OssResult<String> upload(OSS ossClient, String bucket, InputStream is, OssFileTypeEnum type, String fileName) {
        if (StringUtils.isBlank(bucket)) {
            return OssResult.failed("OSS upload failed: bucket is blank.");
        }
        String urlPath = randomFilePath(type, fileName);
        ossClient.putObject(bucket, urlPath, is);
        return checkExist(exist(ossClient, bucket, urlPath)) ? OssResult.success(urlPath) : OssResult.failed();
    }

    /**
     * 删除OSS文件
     *
     * @param properties OSS 相关配置
     * @param fileUrl    OSS fileUrl
     * @return data: [true: 删除成功; false: 删除失败]
     */
    public static OssResult<Boolean> delete(OssProperties properties, String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return OssResult.failed("OSS delete failed: fileUrl is blank.");
        }
        OSS ossClient = null;
        try {
            ossClient = buildClient(properties);
            if (ossClient == null) {
                return OssResult.failed("OSS properties configuration error. properties: " + properties.toString());
            }
            return delete(ossClient, properties.getBucket(), fileUrl);
        } finally {
            closeClient(ossClient);
        }
    }

    /**
     * 删除OSS文件, 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param fileUrl   OSS fileUrl
     * @return data: [true: 删除成功; false: 删除失败]
     */
    public static OssResult<Boolean> delete(OSS ossClient, String bucket, String fileUrl) {
        if (StringUtils.isBlank(bucket)) {
            return OssResult.failed("OSS delete failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return OssResult.failed("OSS delete failed: fileUrl is blank.");
        }
        ossClient.deleteObject(bucket, fileUrl);
        return checkExist(exist(ossClient, bucket, fileUrl)) ? OssResult.success(Boolean.FALSE) : OssResult.success(Boolean.TRUE);
    }

    /**
     * 批量删除OSS文件
     *
     * @param properties OSS 相关配置
     * @param fileUrls   OSS fileUrls
     */
    public static OssResult<List<String>> deletes(OssProperties properties, List<String> fileUrls) {
        if (CollectionUtils.isEmpty(fileUrls)) {
            return OssResult.failed(fileUrls, "OSS deletes failed: fileUrls is empty.");
        }
        OSS ossClient = null;
        try {
            ossClient = buildClient(properties);
            if (ossClient == null) {
                return OssResult.failed("OSS properties configuration error. properties: " + properties.toString());
            }
            return deletes(ossClient, properties.getBucket(), fileUrls);
        } finally {
            closeClient(ossClient);
        }
    }

    /**
     * 批量删除OSS文件, 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param fileUrls  OSS fileUrls
     * @return 未被删除掉的文件
     */
    public static OssResult<List<String>> deletes(OSS ossClient, String bucket, List<String> fileUrls) {
        if (StringUtils.isBlank(bucket)) {
            return OssResult.failed("OSS deletes failed: bucket is blank.");
        }
        if (CollectionUtils.isEmpty(fileUrls)) {
            return OssResult.failed(fileUrls, "OSS deletes failed: fileUrls is empty.");
        }
        ossClient.deleteObjects(new DeleteObjectsRequest(bucket).withKeys(fileUrls));
        List<String> result = new ArrayList<>();
        fileUrls.forEach(url -> {
            if (checkExist(exist(ossClient, bucket, url))) {
                result.add(url);
            }
        });
        return result.size() < fileUrls.size() ? OssResult.success(result) : OssResult.failed(result);
    }

    /**
     * 根据OSS fileUrls判断文件是否存在
     *
     * @param properties OSS 相关配置
     * @param fileUrl    OSS fileUrls
     * @return [true: 存在; false: 不存在]
     */
    public static OssResult<Boolean> exist(OssProperties properties, String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return OssResult.failed("OSS exist failed: fileUrl is blank.");
        }
        OSS ossClient = null;
        try {
            ossClient = buildClient(properties);
            if (ossClient == null) {
                return OssResult.failed("OSS properties configuration error. properties: " + properties.toString());
            }
            return exist(ossClient, properties.getBucket(), fileUrl);
        } finally {
            closeClient(ossClient);
        }
    }

    /**
     * 根据OSS fileUrls判断文件是否存在, 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param fileUrl   OSS fileUrls
     * @return [true: 存在; false: 不存在]
     */
    public static OssResult<Boolean> exist(OSS ossClient, String bucket, String fileUrl) {
        if (StringUtils.isBlank(bucket)) {
            return OssResult.failed("OSS exist failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return OssResult.failed("OSS exist failed: fileUrl is blank.");
        }
        return OssResult.success(ossClient.doesObjectExist(bucket, fileUrl));
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     *
     * @param properties OSS 相关配置
     * @param fileUrl    OSS fileUrl
     * @param targetPath 本地文件路径
     * @throws Exception Exception.class
     */
    public static OssResult<Void> download(OssProperties properties, String fileUrl, String targetPath) throws Exception {
        return download(properties, fileUrl, new File(targetPath));
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件]
     *
     * @param properties OSS相关配置
     * @param fileUrl    OSS fileUrl
     * @param file       本地文件
     * @throws Exception Exception.class
     */
    public static OssResult<Void> download(OssProperties properties, String fileUrl, File file) throws Exception {
        OSS ossClient = null;
        try {
            ossClient = buildClient(properties);
            if (ossClient == null) {
                return OssResult.failed("OSS properties configuration error. properties: " + properties.toString());
            }
            return download(ossClient, properties.getBucket(), fileUrl, file);
        } finally {
            closeClient(ossClient);
        }
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件], 该方法不会释放 ossClient 资源
     *
     * @param ossClient  OSS 客户端
     * @param bucket     OSS 桶
     * @param fileUrl    OSS fileUrl
     * @param targetPath 本地文件路径
     * @throws Exception Exception.class
     */
    public static OssResult<Void> download(OSS ossClient, String bucket, String fileUrl, String targetPath) throws Exception {
        return download(ossClient, bucket, fileUrl, new File(targetPath));
    }

    /**
     * 下载OSS文件到本地 [如果本地文件已存在, 则直接覆盖本地文件], 该方法不会释放 ossClient 资源
     *
     * @param ossClient OSS 客户端
     * @param bucket    OSS 桶
     * @param fileUrl   OSS fileUrl
     * @param file      本地文件
     */
    public static OssResult<Void> download(OSS ossClient, String bucket, String fileUrl, File file) throws Exception {
        if (StringUtils.isBlank(bucket)) {
            return OssResult.failed("OSS download failed: bucket is blank.");
        }
        if (StringUtils.isBlank(fileUrl)) {
            return OssResult.failed("OSS download failed: targetPath is blank.");
        }
        if (file.exists() && file.isDirectory()) {
            file = new File((file.getPath() + SEPARATOR + fileUrl.substring(fileUrl.lastIndexOf(SEPARATOR))));
        }
        if (file.exists() && file.isFile() && !FileUtil.deleteFile(file)) {
            return OssResult.failed("OSS download failed: delete file error.");
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return OssResult.failed("OSS download failed: parent mkdir error.");
        }
        if (!file.createNewFile()) {
            return OssResult.failed("OSS download failed: create new file error.");
        }
        if (!checkExist(exist(ossClient, bucket, fileUrl))) {
            return OssResult.failed("OSS download failed: remote file does not exist. fileUrl: " + fileUrl);
        }
        ossClient.getObject(new GetObjectRequest(bucket, fileUrl), file);
        return OssResult.success();
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
     * OSS客户端释放资源
     *
     * @param ossClient OSS客户端
     */
    private static void closeClient(OSS ossClient) {
        if (ossClient != null) {
            ossClient.shutdown();
        }
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
        if (StringUtils.isBlank(properties.getEndpoint())) {
            return false;
        }
        return StringUtils.isNotBlank(properties.getBucket());
    }

    /**
     * 根据文件名获取一个随机的文件路径
     *
     * @param fileName 文件名
     * @return 随机文件路径
     */
    private static String randomFilePath(OssFileTypeEnum type, String fileName) {
        LocalDateTime date = LocalDateTime.now();
        return type.getType() + SEPARATOR + date.getYear() + SEPARATOR + filling(date.getMonthValue()) + SEPARATOR +
                filling(date.getDayOfMonth()) + SEPARATOR + filling(date.getHour()) + filling(date.getMinute()) +
                filling(date.getSecond()) + filling(date.getNano()) + "_" + fileName;
    }

    /**
     * 校验是否 exist() 函数结果
     *
     * @param exist exist() 函数结果
     * @return [true: 成功; false: 失败]
     */
    private static boolean checkExist(OssResult<Boolean> exist) {
        return exist.getStatus() && exist.getData();
    }

}
