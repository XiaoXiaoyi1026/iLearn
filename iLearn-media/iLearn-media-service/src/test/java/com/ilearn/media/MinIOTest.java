package com.ilearn.media;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 测试MinIO相关操作
 * @date 2/3/2023 5:17 PM
 */
public class MinIOTest {

    public static final MinioClient minioClient = MinioClient.builder()
            /* 服务访问地址 */
            .endpoint("http://localhost:9000")
            /* 访问用户名和密码 */
            .credentials("minioadmin", "minioadmin")
            .build();

    @Test
    public void testUploadObject() {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            /* 指定要上传到的桶的名称 */
                            .bucket("test-bucket")
                            /* 指定文件名, 同一个桶内不能重复 */
                            .object("心海.gif")
                            /* 指定文件路径 */
                            .filename("D:\\Download\\Media\\IMAGE\\心海.gif")
                            .build()
            );
            System.out.println("上传成功!");
        } catch (Exception e) {
            System.out.println("上传失败!");
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadObject2() {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            /* 指定要上传到的桶的名称 */
                            .bucket("test-bucket")
                            /* 可以指定多级目录 */
                            .object("image/心海.gif")
                            /* 指定文件路径 */
                            .filename("D:\\Download\\Media\\IMAGE\\心海.gif")
                            .build()
            );
            System.out.println("上传成功!");
        } catch (Exception e) {
            System.out.println("上传失败!");
            e.printStackTrace();
        }
    }

    @Test
    public void testRemoveObject() {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("test-bucket")
                            .object("image/心海.gif")
                            .build()
            );
            System.out.println("删除成功!");
        } catch (Exception e) {
            System.out.println("删除失败!");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetObject() {
        try (
                /* 过滤器输入流 */
                FilterInputStream object = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("test-bucket")
                                .object("心海.gif")
                                .build()
                );
                FileOutputStream fileOutputStream = new FileOutputStream("D:\\Download\\Media\\IMAGE\\心海(2).gif")
        ) {
            if (object != null) {
                IOUtils.copy(object, fileOutputStream);
            }
            System.out.println("下载成功!");
        } catch (Exception e) {
            System.out.println("下载失败!");
            e.printStackTrace();
        }
    }

}
