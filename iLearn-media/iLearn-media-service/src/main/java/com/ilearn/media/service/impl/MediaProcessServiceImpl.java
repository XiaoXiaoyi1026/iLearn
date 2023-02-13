package com.ilearn.media.service.impl;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.media.mapper.MediaFilesMapper;
import com.ilearn.media.mapper.MediaProcessHistoryMapper;
import com.ilearn.media.mapper.MediaProcessMapper;
import com.ilearn.media.model.po.MediaFiles;
import com.ilearn.media.model.po.MediaProcess;
import com.ilearn.media.model.po.MediaProcessHistory;
import com.ilearn.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ilearn.media.utils.MediaServiceUtils.*;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 媒体处理服务实现
 * @date 2/12/2023 5:02 PM
 */
@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    private MediaProcessMapper mediaProcessMapper;

    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    void setMediaProcessMapper(MediaProcessMapper mediaProcessMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
    }

    @Autowired
    void setMediaProcessHistoryMapper(MediaProcessHistoryMapper mediaProcessHistoryMapper) {
        this.mediaProcessHistoryMapper = mediaProcessHistoryMapper;
    }

    @Autowired
    void setMediaFilesMapper(MediaFilesMapper mediaFilesMapper) {
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Override
    public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selectByShard(shardTotal, shardIndex, count);
    }

    @Override
    public void saveProcessedStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 先查询任务, 判断任务状态(成功/失败)
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            log.error("任务[taskId={}]不存在", taskId);
        } else {
            if ("3".equals(status)) {
                // 任务失败, 更新状态
                mediaProcess.setStatus(status);
                mediaProcess.setErrorMessage(errorMsg);
                mediaProcessMapper.updateById(mediaProcess);
            } else if ("2".equals(status)) {
                MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
                if (mediaFiles == null) {
                    log.error("文件[fileId={}]不存在", fileId);
                    ILearnException.cast("查找更新前文件失败, 请重试.");
                }
                // 更新源文件表的信息
                String fileType = getMimeTypeByFileExtension(getFileExtension(url));
                String fileName = getFileName(url);
                mediaFiles.setUrl(url);
                // 由于url开始字符为'/', 所以从下标1的位置开始截取, 第2个'/'往后的字符串为filePath
                mediaFiles.setFilePath(url.substring(url.indexOf("/", 1) + 1));
                mediaFiles.setFileType(fileType);
                mediaFiles.setFilename(fileName);
                mediaFiles.setChangeDate(LocalDateTime.now());
                mediaFilesMapper.updateById(mediaFiles);
                // 更新处理信息
                mediaProcess.setStatus(status);
                MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
                BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
                mediaProcessHistory.setUrl(url);
                mediaProcessHistory.setFinishDate(LocalDateTime.now());
                // 如果处理成功, 将待处理表的记录删除
                mediaProcessMapper.deleteById(taskId);
                // 将处理成功的添加到历史表
                mediaProcessHistoryMapper.insert(mediaProcessHistory);
            } else {
                log.error("Invalid update status: {}", status);
                ILearnException.cast("状态码异常, 更新视频处理状态失败.");
            }
        }
    }
}
