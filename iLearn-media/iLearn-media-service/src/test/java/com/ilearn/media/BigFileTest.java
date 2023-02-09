package com.ilearn.media;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 测试大文件分块与合并
 * @date 2/6/2023 3:40 PM
 */
public class BigFileTest {

    /**
     * 测试文件分块传输
     */
    @Test
    public void testChunk() {
        // 准备源文件
        File sourceFile = new File("D:\\Download\\Media\\Video\\计算机导论第二次作业.flv");
        // 分块文件存放的路径
        File chunkFolder = new File("D:\\Download\\Media\\Video\\Chunk\\");
        if (!chunkFolder.exists()) {
            boolean mkdirs = chunkFolder.mkdirs();
            System.out.println(mkdirs);
        }

        // 定义分块的大小, 1M
        double chunkSize = Math.pow(1024, 2);

        // 分块的数量, 原文件大小 / 分块数
        double chunkNumber = Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        try (
                // 创建从源文件读取数据的流
                RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r")
        ) {
            // 创建缓冲区, 1M
            byte[] buffer = new byte[1024];

            File chunkFile;
            RandomAccessFile raf_write;
            int len;

            // 循环写chunkNumber次
            for (int chunkIndex = 0; chunkIndex < chunkNumber; chunkIndex++) {
                // 创建分块文件
                chunkFile = new File("D:\\Download\\Media\\Video\\Chunk\\" + chunkIndex);
                // 如果分块文件存在则直接删除
                if (chunkFile.exists()) {
                    boolean delete = chunkFile.delete();
                    System.out.println(delete);
                }
                // 向分块文件写
                boolean newFile = chunkFile.createNewFile();
                raf_write = new RandomAccessFile(chunkFile, "rw");
                if (newFile) {
                    while ((len = (raf_read.read(buffer))) != -1) {
                        // 向文件中写数据
                        raf_write.write(buffer, 0, len);
                        // 达到分块大小退出循环
                        if (chunkFile.length() >= chunkSize) {
                            break;
                        }
                    }
                }
                raf_write.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试将分块文件合并
     */
    @Test
    public void testMerge() {
        // 创建合并后的文件
        File mergeFile = new File("D:\\Download\\Media\\Video\\计算机导论第二次作业_merge.flv");
        if (mergeFile.exists()) {
            System.out.println(mergeFile.delete());
        }
        // 从分块文件的目录中读取所有的分块文件
        File chunkFolder = new File("D:\\Download\\Media\\Video\\Chunk");
        File[] chunkFiles = chunkFolder.listFiles();
        if (chunkFiles != null && chunkFiles.length > 0) {
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw")) {
                // 将分块文件按照文件名称升序排序
                Arrays.sort(chunkFiles, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
                RandomAccessFile raf_read;
                byte[] buffer = new byte[1024];
                int len;
                // 循环取出所有分片数据写入合并后的文件
                for (File chunkFile : chunkFiles) {
                    raf_read = new RandomAccessFile(chunkFile, "r");
                    while ((len = (raf_read.read(buffer))) != -1) {
                        raf_write.write(buffer, 0, len);
                    }
                    raf_read.close();
                    // 删除分块文件
                    boolean delete = chunkFile.delete();
                    System.out.println(delete);
                }
                // 验证合并后的文件和源文件是否一致
                File sourceFile = new File("D:\\Download\\Media\\Video\\计算机导论第二次作业.flv");
                // 获取MD5值进行判断
                String sourceFileMD5 = DigestUtils.md5DigestAsHex(Files.newInputStream(sourceFile.toPath()));
                String mergeFileMD5 = DigestUtils.md5DigestAsHex(Files.newInputStream(mergeFile.toPath()));
                if (sourceFileMD5.equals(mergeFileMD5)) {
                    System.out.println("合并成功!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
