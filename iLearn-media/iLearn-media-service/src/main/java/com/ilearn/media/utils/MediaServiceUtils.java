package com.ilearn.media.utils;

import com.ilearn.base.exception.ILearnException;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体服务工具类
 * @date 2/12/2023 5:57 PM
 */
public class MediaServiceUtils {

    /**
     * 根据文件名得到扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        return extension;
    }

    public static String getFileName(@NotNull String description) {
        String fileName = description;
        if (description.contains("/")) {
            fileName = description.substring(description.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    /**
     * 获取文件的contentType(MimeType)
     *
     * @param objectName 文件名
     * @return contentType
     */
    public static String getMimeTypeByObjectName(String objectName) {
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (objectName != null) {
            // 根据扩展名得到mimeType
            mimeType = getMimeTypeByFileExtension(getFileExtension(objectName));
        } else {
            ILearnException.cast("文件名不合法");
        }
        return mimeType;
    }

    /**
     * 根据文件扩展名获取文件的mimeType
     *
     * @param extension 文件扩展名
     * @return 文件mimeType
     */
    public static String getMimeTypeByFileExtension(String extension) {
        // 默认为未知二进制流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (StringUtils.isNotEmpty(extension)) {
            // 根据扩展名得到mimeType
            ContentInfo contentInfo = ContentInfoUtil.findExtensionMatch(extension);
            if (contentInfo != null) {
                mimeType = contentInfo.getMimeType();
            }
        }
        return mimeType;
    }

    /**
     * 获取文件在MinIO上的目录
     *
     * @param fileMD5 文件MD5值
     * @return 文件所在目录
     */
    public static String getFileFolder(@NotNull String fileMD5) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(fileMD5.charAt(0)).append('/').append(fileMD5.charAt(1)).append('/').append(fileMD5).append('/'));
    }

    /**
     * 获取分块文件在MinIO上的目录
     *
     * @param sourceFileMD5 源文件MD5值
     * @return 分块文件所在目录
     */
    @NotNull
    public static String getChunkFileFolder(@NotNull String sourceFileMD5) {
        return getFileFolder(sourceFileMD5) + "chunk/";
    }

}
