package com.ilearn.media.service.task;

import com.ilearn.base.exception.ILearnException;
import com.ilearn.base.utils.MediaConvert2MP4Util;
import com.ilearn.media.model.po.MediaProcess;
import com.ilearn.media.service.MediaFilesService;
import com.ilearn.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VideoJobHandler {

    @Value("${video-process.ffmpeg-path}")
    private String ffmpegPath;

    private MediaProcessService mediaProcessService;

    private MediaFilesService mediaFilesService;

    @Autowired
    void setMediaProcessService(MediaProcessService mediaProcessService) {
        this.mediaProcessService = mediaProcessService;
    }

    @Autowired
    void setMediaFilesService(MediaFilesService mediaFilesService) {
        this.mediaFilesService = mediaFilesService;
    }

    /**
     * 使用分片广播任务进行视频转码处理
     */
    @XxlJob("videoJobHandler")
    @Transactional(rollbackFor = Throwable.class)
    public void videoJobHandler() {
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();
        // 当前分片任务的编号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 根据分片参数获取要处理的视频信息列表, 一次处理的任务数 = CPU核心数
        List<MediaProcess> mediaProcessList = mediaProcessService.getMediaProcessList(shardTotal, shardIndex, 2);
        if (mediaProcessList == null || mediaProcessList.isEmpty()) {
            log.debug("查询待处理视频数量为0");
        } else {
            // 待处理的任务数量
            int taskNumber = mediaProcessList.size();
            // 根据任务数量创建固定数量的线程池
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(taskNumber);
            // 定义计数器, 用于记录线程池的线程数量是否创建完成
            CountDownLatch countDownLatch = new CountDownLatch(taskNumber);
            // 遍历任务列表, 将任务创建并放入线程池
            mediaProcessList.forEach(mediaProcess -> fixedThreadPool.execute(() -> {
                // 保证幂等性
                if ("2".equals(mediaProcess.getStatus())) {
                    log.info("视频已处理完成: {}", mediaProcess);
                    // 计数器-1
                    countDownLatch.countDown();
                } else {
                    // 获取视频信息
                    String bucketName = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();
                    String fileId = mediaProcess.getFileId();
                    // 准备源文件与转换后的文件
                    File originalFile, mp4File;
                    originalFile = mp4File = null;
                    try {
                        originalFile = File.createTempFile("original", null);
                        mp4File = File.createTempFile("mp4", ".mp4");
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("创建临时文件失败: {}", e.getMessage());
                        ILearnException.cast("创建临时文件失败");
                    }
                    // 定义文件信息
                    String videoPath = originalFile.getAbsolutePath();
                    String mp4Name = fileId + ".mp4";
                    String mp4Path = mp4File.getAbsolutePath();
                    try {
                        try {
                            // 从MinIO上下载待处理的视频到本地
                            mediaFilesService.downloadFileFromMinIO(originalFile, bucketName, filePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("从MinIO下载文件失败, 因为: {}, 文件信息: {}", e.getMessage(), mediaProcess);
                            ILearnException.cast("从MinIO下载文件失败");
                        }
                        // 将filePath中的文件扩展名变为.mp4
                        String processedFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".mp4";
                        // 调用工具类进行处理
                        MediaConvert2MP4Util videoUtil = new MediaConvert2MP4Util(ffmpegPath, videoPath, mp4Name, mp4Path);
                        // 开始进行视频转换, 转换成功返回success
                        String processResult = videoUtil.generateMp4();
                        // 处理完成后记录处理结果
                        String status, url, errorMessage;
                        url = errorMessage = null;
                        if ("success".equals(processResult)) {
                            status = "2";
                            url = "/" + bucketName + "/" + processedFilePath;
                            try {
                                // 将转码好的文件上传到MinIO
                                mediaFilesService.saveFile2MinIO(mp4Path, bucketName, processedFilePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("上传处理完成的文件到MinIO失败, 因为: {}", e.getMessage());
                                ILearnException.cast("文件处理失败");
                            }
                            // 将MinIO上的原始文件删除
                            mediaFilesService.removeObjectFromMinIO(bucketName, filePath);
                        } else {
                            status = "3";
                            errorMessage = processResult;
                        }
                        // 保存更新信息
                        try {
                            mediaProcessService.saveProcessedStatus(mediaProcess.getId(), status, fileId, url, errorMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("保存更新结果失败, 因为: {}", e.getMessage());
                            ILearnException.cast("保存更新结果失败");
                        }
                    } finally {
                        // 计数器-1
                        countDownLatch.countDown();
                        // 删除临时文件
                        if (originalFile.exists() && !originalFile.delete()) {
                            assert log != null;
                            log.error("删除临时文件失败, 因为: {}", originalFile.getAbsolutePath());
                            ILearnException.cast("删除临时文件失败");
                        }
                        if (mp4File.exists() && !mp4File.delete()) {
                            assert log != null;
                            log.error("删除临时文件失败, 因为: {}", mp4File.getAbsolutePath());
                            ILearnException.cast("删除临时文件失败");
                        }
                    }
                }
            }));
            try {
                // 阻塞等待线程创建完成, 超时时间为30分钟
                boolean await = countDownLatch.await(30, TimeUnit.MINUTES);
                if (await) {
                    log.info("视频处理计数器已经完成");
                } else {
                    log.error("视频处理计数器未完成");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("计数器中断异常, 因为: {}", e.getMessage());
                ILearnException.cast("视频处理计数器中断异常");
            }
        }
    }
}
