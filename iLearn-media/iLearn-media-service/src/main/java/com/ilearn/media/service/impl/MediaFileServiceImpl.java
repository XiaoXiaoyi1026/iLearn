package com.ilearn.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ilearn.base.dictionary.ObjectAuditStatus;
import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.model.PageParams;
import com.ilearn.base.model.PageResult;
import com.ilearn.media.mapper.MediaFilesMapper;
import com.ilearn.media.model.dto.QueryMediaParamsDto;
import com.ilearn.media.model.dto.UploadFileParamsDto;
import com.ilearn.media.model.dto.UploadFileResponseDto;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.service.MediaFileService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体服务实现类
 * @date 2023/2/3 15:47
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFilesMapper mediaFilesMapper;

    private final MinioClient minioClient;

    private MediaFileService mediaFileService;

    @Value("${minio.bucket.files}")
    private String filesBucket;

    @Value("${minio.bucket.video}")
    private String videoBucket;

    @Autowired
    MediaFileServiceImpl(MediaFilesMapper mediaFilesMapper, MinioClient minioClient) {
        this.mediaFilesMapper = mediaFilesMapper;
        this.minioClient = minioClient;
    }

    /**
     * Spring官方推荐, 使用setter注入解决循环依赖问题
     * 因为基于setter注入的指会在被调用时注入, 和在构造器注入时加上@Lazy是一样的效果
     *
     * @param mediaFileService 媒体服务
     */
    @Autowired
    void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, @NotNull PageParams pageParams, @NotNull QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        String filename = queryMediaParamsDto.getFilename();
        String fileType = queryMediaParamsDto.getFileType();
        queryWrapper.like(filename != null && !"".equals(filename), MediaFiles::getFilename, filename);
        queryWrapper.eq(fileType != null && !"".equals(fileType), MediaFiles::getFileType, fileType);
        queryWrapper.eq(queryMediaParamsDto.getAuditStatus() != null, MediaFiles::getAuditStatus, queryMediaParamsDto.getAuditStatus());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public UploadFileResponseDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] fileDataBytes, String folder, String objectName) {
        UploadFileResponseDto uploadFileResponse;
        if (folder == null) {
            // 按照年月日进行生成
            folder = this.getDateFolder(new Date());
        } else if (folder.indexOf('/') < 0) {
            folder += '/';
        }
        String filename = uploadFileParamsDto.getFilename();
        // 获取文件的MD5值
        String fileMD5 = DigestUtils.md5DigestAsHex(fileDataBytes);
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
        this.saveMedia2MinIO(fileDataBytes, filesBucket, objectName);
        /* 由于saveMedia2DataBase是被this指针调用的, 不被spring代理, 所以不在Spring的管辖范围, 此处发生了事务失效
         解决方法: 使用被Spring管理的mediaFileService对象来调用该方法 */
        MediaFiles mediaFiles = mediaFileService.saveMedia2DataBase(companyId, uploadFileParamsDto, fileMD5, filesBucket, objectName);
        // 准备返回数据
        uploadFileResponse = new UploadFileResponseDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResponse);
        return uploadFileResponse;
    }

    /**
     * 上传媒体文件到MinIO
     *
     * @param fileDataBytes 文件的字节数组
     * @param bucketName    存储的目标桶的名称
     * @param objectName    文件的全路径
     */
    private void saveMedia2MinIO(byte[] fileDataBytes, String bucketName, String objectName) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileDataBytes)) {
            // 获取文件类型, 默认为未知的二进制流信息
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (objectName.contains(".")) {
                // 取出其中的扩展名
                String extension = objectName.substring(objectName.indexOf("."));
                // 根据扩展名得到mimeType
                ContentInfo contentInfo = ContentInfoUtil.findExtensionMatch(extension);
                if (contentInfo != null) {
                    contentType = contentInfo.getMimeType();
                }
            }
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    /*
                      InputStream stream 输入流 long objectSize 对象大小 long partSize 分片大小
                      -1代表最小分片大小: 5M 最大分片大小: 5T 分片数量最多不超过10000
                     */
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType).build());
        } catch (Exception e) {
            // 记录日志
            log.error("上传文件失败, 因为: {}", e.getMessage());
            e.printStackTrace();
            ILearnException.cast("Upload failed, please try again.");
        }
    }

    @NotNull
    @Transactional(rollbackFor = Throwable.class)
    public MediaFiles saveMedia2DataBase(Long companyId, UploadFileParamsDto uploadFileParamsDto, String id, String bucketName, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(id);
            mediaFiles.setFileId(id);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucketName);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setUrl("/" + bucketName + "/" + objectName);
            LocalDateTime now = LocalDateTime.now();
            mediaFiles.setCreateDate(now);
            mediaFiles.setChangeDate(now);
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus(ObjectAuditStatus.NOT_YET);
            mediaFilesMapper.insert(mediaFiles);
        }
        return mediaFiles;
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
}
