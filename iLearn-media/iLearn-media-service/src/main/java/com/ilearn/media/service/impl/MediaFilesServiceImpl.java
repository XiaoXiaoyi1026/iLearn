package com.ilearn.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ilearn.base.dictionary.ObjectAuditStatus;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.PageRequestParams;
import com.ilearn.base.model.PageResponse;
import com.ilearn.base.model.ResponseMessage;
import com.ilearn.media.mapper.MediaFilesMapper;
import com.ilearn.media.mapper.MediaProcessMapper;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.model.po.MediaProcess;
import com.ilearn.media.service.MediaFilesService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.ilearn.media.utils.MediaServiceUtils.*;

/**
 * @author xiaoxiaoyi
 * * @version 1.0
 * * @description 媒体服务实现类
 * * @date 2023/2/3 15:47
 */
@Slf4j
@Service
public class MediaFilesServiceImpl implements MediaFilesService {

    private MediaFilesMapper mediaFilesMapper;

    private MinioClient minioClient;

    private MediaFilesService mediaFilesService;

    private MediaProcessMapper mediaProcessMapper;

    @Value("${minio.bucket.files}")
    private String filesBucket;

    @Value("${minio.bucket.video}")
    private String videoBucket;

    @Autowired
    void setMediaFilesMapper(MediaFilesMapper mediaFilesMapper) {
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Autowired
    void setMinioClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Spring官方推荐, 使用setter注入解决循环依赖问题
     * 因为基于setter注入的指会在被调用时注入, 和在构造器注入时加上@Lazy是一样的效果
     *
     * @param mediaFilesService 媒体服务
     */
    @Autowired
    void setMediaFileService(MediaFilesService mediaFilesService) {
        this.mediaFilesService = mediaFilesService;
    }

    @Autowired
    void setMediaProcessMapper(MediaProcessMapper mediaProcessMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
    }

    @Override
    public PageResponse<MediaFiles> queryMediaFiles(Long companyId, @NotNull PageRequestParams pageRequestParams, @NotNull QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        String filename = queryMediaParamsDto.getFilename();
        String fileType = queryMediaParamsDto.getFileType();
        queryWrapper.like(filename != null && !"".equals(filename), MediaFiles::getFilename, filename);
        queryWrapper.eq(fileType != null && !"".equals(fileType), MediaFiles::getFileType, fileType);
        queryWrapper.eq(queryMediaParamsDto.getAuditStatus() != null, MediaFiles::getAuditStatus, queryMediaParamsDto.getAuditStatus());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageRequestParams.getPageNo(), pageRequestParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResponse<>(list, total, pageRequestParams.getPageNo(), pageRequestParams.getPageSize());
    }

    @Override
    public UploadFileResponseDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] fileData, String folder, String objectName) {
        UploadFileResponseDto uploadFileResponse;
        if (folder == null) {
            // 按照年月日进行生成
            folder = getDateFolder(new Date());
        } else if (folder.indexOf('/') < 0) {
            folder += '/';
        }
        String filename = uploadFileParamsDto.getFilename();
        // 获取文件的MD5值
        String fileMD5 = DigestUtils.md5DigestAsHex(fileData);
        if (objectName == null) {
            if (!filename.contains(".")) {
                ILearnException.cast("Failed to upload file: " + filename + ", cause: filename invalid.");
            }
            // 使用文件的MD5值作为文件名
            filename = fileMD5 + filename.substring(filename.lastIndexOf('.'));
        }
        // 拼接文件全路径
        objectName = folder + filename;
        // 保存媒体文件到MinIO
        saveFile2MinIO(fileData, filesBucket, objectName);
        /* 由于saveMedia2DataBase是被this指针调用的, 不被spring代理, 所以不在Spring的管辖范围, 此处发生了事务失效
         解决方法: 使用被Spring管理的mediaFileService对象来调用该方法 */
        MediaFiles mediaFiles = mediaFilesService.saveFileInformation2DataBase(companyId, uploadFileParamsDto, fileMD5, filesBucket, objectName);
        // 准备返回数据
        uploadFileResponse = new UploadFileResponseDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResponse);
        return uploadFileResponse;
    }

    @NotNull
    @Transactional(rollbackFor = Throwable.class)
    public MediaFiles saveFileInformation2DataBase(Long companyId, UploadFileParamsDto uploadFileParamsDto, String fileId, String bucketName, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucketName);
            mediaFiles.setFilePath(objectName);
            // 判断图片与mp4视频可以直接设置url, 其他类型需要转码后再进行设置
            String filename = uploadFileParamsDto.getFilename();
            String mimeType = getMimeTypeByFileExtension(getFileExtension(filename));
            if (mimeType.startsWith("image") || mimeType.endsWith("mp4")) {
                mediaFiles.setUrl("/" + bucketName + "/" + objectName);
            }
            LocalDateTime now = LocalDateTime.now();
            mediaFiles.setCreateDate(now);
            mediaFiles.setChangeDate(now);
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus(ObjectAuditStatus.NOT_YET);
            mediaFilesMapper.insert(mediaFiles);

            // 如果媒体类型为avi, 则需要进行处理
            if (mimeType.equals(getMimeTypeByFileExtension(".avi"))) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                // 状态 1:未处理，2：处理成功  3处理失败
                mediaProcess.setStatus("1");
                mediaProcessMapper.insert(mediaProcess);
            }
        }
        return mediaFiles;
    }

    @Override
    public ResponseMessage<Boolean> checkFile(String sourceFileMD5) {
        // 在数据库中存在且在MinIO文件系统中才认为存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(sourceFileMD5);
        if (mediaFiles == null) {
            return ResponseMessage.success(Boolean.FALSE);
        }
        try {
            // 查看是否在文件系统中
            GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build());
            if (object == null) {
                return ResponseMessage.success(Boolean.FALSE);
            }
        } catch (Exception e) {
            return ResponseMessage.success(Boolean.FALSE);
        }
        return ResponseMessage.success(Boolean.TRUE);
    }

    @Override
    public ResponseMessage<Boolean> checkChunk(String sourceFileMD5, int chunkIndex) {
        // 得到分块文件所在目录
        String chunkFileFolder = getChunkFileFolder(sourceFileMD5);
        // 得到分块文件的路径
        String chunkFilePath = chunkFileFolder + chunkIndex;
        // 查询文件系统, 看分块文件是否存在
        try {
            // 查看是否在文件系统中
            GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder().bucket(videoBucket).object(chunkFilePath).build());
            if (object == null) {
                return ResponseMessage.success(Boolean.FALSE);
            }
        } catch (Exception e) {
            return ResponseMessage.success(Boolean.FALSE);
        }
        return ResponseMessage.success(Boolean.TRUE);
    }

    @Override
    public ResponseMessage<Boolean> uploadChunk(String sourceFileMD5, int chunkIndex, byte[] fileData) {
        // 得到分块文件所在目录
        String chunkFileFolder = getChunkFileFolder(sourceFileMD5);
        // 得到分块文件的路径
        String chunkFilePath = chunkFileFolder + chunkIndex;
        try {
            saveFile2MinIO(fileData, videoBucket, chunkFilePath);
            return ResponseMessage.success(Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传分块文件失败, 因为: {}", e.getMessage());
            ILearnException.cast("上传文件失败, 请重试");
            return ResponseMessage.validFail(Boolean.FALSE, "上传分片文件失败");
        }
    }

    @Override
    public ResponseMessage<Object> mergeChunks(Long companyId, String sourceFileMD5, int chunkTotal, @NotNull UploadFileParamsDto uploadFileParamsDto) {
        // 下载分块文件
        File[] chunkFiles = downloadChunkFilesFromMinIO(sourceFileMD5, chunkTotal);
        String filename = uploadFileParamsDto.getFilename();
        // 得到源文件的扩展名
        String fileExtension = getFileExtension(filename);
        // 创建合并后的临时文件
        File mergeFile = null;
        try {
            try {
                // 文件名以merge为前缀, extension为后缀的文件
                mergeFile = File.createTempFile("merge", fileExtension);
            } catch (IOException e) {
                e.printStackTrace();
                ILearnException.cast("创建合并文件失败, 因为: " + e.getMessage());
            }
            // 缓冲区
            byte[] buffer = new byte[1024];
            int len;
            // 合并分块
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw"); FileInputStream mergeFileInputStream = new FileInputStream(mergeFile)) {
                for (File chunkFile : chunkFiles) {
                    try (RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r")) {
                        while ((len = raf_read.read(buffer)) != -1) {
                            raf_write.write(buffer, 0, len);
                        }
                    }
                }
                // 校验文件是否和源文件一致
                if (!DigestUtils.md5DigestAsHex(mergeFileInputStream).equals(sourceFileMD5)) {
                    log.error("合并文件MD5校验未通过, 文件路径: {}; 原始文件MD5: {}", mergeFile.getAbsolutePath(), sourceFileMD5);
                    ILearnException.cast("合并文件MD5校验未通过");
                }
                // 合并文件成功, 需要删除MinIO上的分片文件
                String chunkFileFolder = getChunkFileFolder(sourceFileMD5);
                for (int chunkIndex = 0; chunkIndex < chunkTotal; chunkIndex++) {
                    removeObjectFromMinIO(videoBucket, chunkFileFolder + chunkIndex);
                }
            } catch (IOException e) {
                log.error("合并文件出错, 文件路径: {}; 原始文件MD5: {}", mergeFile.getAbsolutePath(), sourceFileMD5);
                ILearnException.cast("合并文件失败, 请重试.");
            }
            uploadFileParamsDto.setFileSize(mergeFile.length());
            uploadFileParamsDto.setContentType(getMimeTypeByFileExtension(fileExtension));
            // 合并文件上传到文件系统, objectName为文件上传到MinIO后的全路径
            String objectName = getFileFolder(sourceFileMD5) + sourceFileMD5 + fileExtension;
            saveFile2MinIO(mergeFile.getAbsolutePath(), videoBucket, objectName);
            // 将文件信息入库, 非事务方法调用事务方法
            mediaFilesService.saveFileInformation2DataBase(companyId, uploadFileParamsDto, sourceFileMD5, videoBucket, objectName);
            return ResponseMessage.success(Boolean.TRUE);
        } finally {
            // 删除所有临时文件
            for (File chunkFile : chunkFiles) {
                if (chunkFile != null && chunkFile.exists()) {
                    boolean delete = chunkFile.delete();
                    if (!delete) {
                        log.error("删除临时文件失败, 文件路径: {}", chunkFile.getAbsolutePath());
                    }
                }
            }
            if (mergeFile != null && mergeFile.exists()) {
                boolean delete = mergeFile.delete();
                if (!delete) {
                    log.error("删除合并文件失败, 文件路径: {}", mergeFile.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public ResponseMessage<String> getUrl(String fileId) {
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileId);
        if (mediaFile == null) {
            log.error("文件不存在, fileId: {}", fileId);
            ILearnException.cast("查询媒体文件失败");
        }
        String url = mediaFile.getUrl();
        if (StringUtils.isEmpty(url)) {
            log.error("文件url为空, fileId: {}", fileId);
            ILearnException.cast("文件尚未进行转码处理");
        }
        return ResponseMessage.success(url);
    }

    @Override
    public void removeObjectFromMinIO(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除对象: {} 失败", objectName);
            ILearnException.cast("删除对象: " + objectName + " 失败");
        }
    }

    /**
     * 根据日期得到文件的存放路径
     *
     * @param date 日期
     * @return 存储的目录
     */
    @NotNull
    private String getDateFolder(Date date) {
        // 日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        // 获取当前日期时间
        String dateString = simpleDateFormat.format(date);
        return dateString + '/';
    }

    /**
     * 上传媒体文件到MinIO
     *
     * @param fileData   文件的字节数组
     * @param bucketName 存储的目标桶的名称
     * @param objectName 文件的全路径
     */
    private void saveFile2MinIO(byte[] fileData, String bucketName, String objectName) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData)) {
            // 获取文件类型, 默认为未知的二进制流信息
            String contentType = getMimeTypeByObjectName(objectName);
            // InputStream stream 输入流; long objectSize 对象大小; long partSize 分片大小: -1代表最小分片大小: 5M, 最大分片大小: 5T, 分片数量最多不超过10000
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(byteArrayInputStream, byteArrayInputStream.available(), -1).contentType(contentType).build());
        } catch (Exception e) {
            // 记录日志
            log.error("上传文件失败, 因为: {}", e.getMessage());
            e.printStackTrace();
            ILearnException.cast("Upload failed, please try again.");
        }
    }

    @Override
    public void saveFile2MinIO(String filePath, String bucketName, String objectName) {
        try {
            minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).object(objectName).filename(filePath).build());
            log.debug("上传成功, 文件路径: {}", filePath);
        } catch (Exception e) {
            log.error("上传失败, 文件路径: {}", filePath);
            ILearnException.cast("文件上传失败, 文件路径: " + filePath);
        }
    }

    /**
     * 从MinIO上下载分片文件
     *
     * @param sourceFileMD5 源文件的MD5值
     * @param chunkTotal    分片数量
     * @return 分片集合
     */
    @NotNull
    @Contract(pure = true)
    private File @NotNull [] downloadChunkFilesFromMinIO(String sourceFileMD5, int chunkTotal) {
        // 分块文件所在目录
        String chunkFileFolder = getChunkFileFolder(sourceFileMD5);
        // 所有分块文件列表
        File[] chunkFiles = new File[chunkTotal];
        // 本地临时分块文件
        File localTempChunkFile = null;
        // 准备开始下载
        for (int chunkIndex = 0; chunkIndex < chunkTotal; chunkIndex++) {
            try {
                // 创建临时文件
                localTempChunkFile = File.createTempFile("chunk" + chunkIndex, null);
            } catch (IOException e) {
                e.printStackTrace();
                ILearnException.cast("创建分片临时文件失败, 因为: " + e.getMessage());
            }
            // chunkFileFolder + chunkIndex为分片文件全路径
            chunkFiles[chunkIndex] = downloadFileFromMinIO(localTempChunkFile, videoBucket, chunkFileFolder + chunkIndex);
        }
        return chunkFiles;
    }

    @Override
    public File downloadFileFromMinIO(File downloadFile, String bucketName, String objectName) {
        try (
                InputStream sourceFile = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
                // 建议与下载文件间的输出流
                FileOutputStream downloadFileOutputStream = new FileOutputStream(downloadFile)
        ) {
            // 将服务器上拉下来的分片文件输出到下载文件中
            IOUtils.copy(sourceFile, downloadFileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            ILearnException.cast("Download chunk file failed, please try again.");
        }
        return downloadFile;
    }
}