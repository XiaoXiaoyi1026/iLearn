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
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体服务实现类
 * @date 2023/2/3 15:47
 */
@Service
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFilesMapper mediaFilesMapper;

    private final MinioClient minioClient;

    @Value("${minio.bucket.files}")
    private String filesBucket;

    @Value("${minio.bucket.video}")
    private String videoBucket;

    @Autowired
    MediaFileServiceImpl(MediaFilesMapper mediaFilesMapper, MinioClient minioClient) {
        this.mediaFilesMapper = mediaFilesMapper;
        this.minioClient = minioClient;
    }

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, @NotNull PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

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
    @Transactional(rollbackFor = Throwable.class)
    public UploadFileResponseDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] fileDataBytes, String folder, String objectName) {
        UploadFileResponseDto uploadFileResponse = null;
        if (folder == null) {
            // 按照年月日进行生成
            folder = this.getFileDateFolder(new Date());
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
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileDataBytes)) {
            // 上传到MinIO
            minioClient.putObject(PutObjectArgs.builder().bucket(filesBucket).object(objectName)
                    /*
                      InputStream stream 输入流
                      long objectSize 对象大小
                      long partSize 分片大小
                      -1代表最小分片大小: 5M
                      最大分片大小: 5T
                      分片数量最多不超过10000
                     */
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(uploadFileParamsDto.getContentType()).build());
            // 将文件保存至数据库, 先根据文件的md5值获取文件
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMD5);
            if (mediaFiles == null) {
                mediaFiles = new MediaFiles();
                BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
                mediaFiles.setId(fileMD5);
                mediaFiles.setFileId(fileMD5);
                mediaFiles.setCompanyId(companyId);
                mediaFiles.setBucket(filesBucket);
                mediaFiles.setFilePath(objectName);
                mediaFiles.setUrl("/" + filesBucket + "/" + objectName);
                mediaFiles.setStatus("1");
                mediaFiles.setAuditStatus(ObjectAuditStatus.NOT_YET);
                // 插入文件表
                mediaFilesMapper.insert(mediaFiles);
            }
            // 准备返回数据
            uploadFileResponse = new UploadFileResponseDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResponse);
        } catch (Exception e) {
            e.printStackTrace();
            ILearnException.cast("上传失败!因为: " + e.getMessage());
        }
        return uploadFileResponse;
    }

    /**
     * 根据日期得到文件的存放路径
     *
     * @param date 日期
     * @return 存储的目录
     */
    private String getFileDateFolder(Date date) {
        // 日期格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        // 获取当前日期时间
        String dateString = simpleDateFormat.format(date);
        return dateString + '/';
    }
}
