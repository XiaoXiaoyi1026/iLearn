package com.ilearn.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MediaConvert2MP4Util extends MediaConvertUtil {

    /**
     * ffmpeg安装位置
     */
    String ffmpegPath;
    /**
     * 要转换的媒体文件位置
     */
    String mediaPath;
    /**
     * 转换后的mp4文件名
     */
    String targetFileName;
    /**
     * 转换后的存放路径
     */
    String targetPath;

    public MediaConvert2MP4Util(String ffmpegPath, String mediaPath, String targetFileName, String targetPath) {
        super(ffmpegPath);
        this.ffmpegPath = ffmpegPath;
        this.mediaPath = mediaPath;
        this.targetFileName = targetFileName;
        this.targetPath = targetPath;
    }

    /**
     * 清除已生成的文件
     *
     * @param filePath 要删除的文件路径
     */
    private void clearFile(String filePath) {
        // 删除原来已经生成的m3u8及ts文件
        File sourseFile = new File(filePath);
        if (sourseFile.exists() && sourseFile.isFile()) {
            if (sourseFile.delete()) {
                log.info("删除文件成功");
            }
        }
    }

    /**
     * 视频编码，生成mp4文件
     *
     * @return 成功返回success，失败返回控制台日志
     */
    public String generateMp4() {
        // 清除已生成的mp4
        // clear_mp4(mp4folder_path+mp4_name);
        clearFile(targetPath);
        /*
        ffmpeg.exe -i  lucene.avi -c:v libx264 -s 1280x720 -pix_fmt yuv420p -b:a 63k -b:v 753k -r 18 .\lucene.mp4
         */
        List<String> command = new ArrayList<>();
        // command.add("D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add(ffmpegPath);
        command.add("-i");
        // command.add("D:\\BaiduNetdiskDownload\\test1.avi");
        command.add(mediaPath);
        command.add("-c:v");
        command.add("libx264");
        // 覆盖输出文件
        command.add("-y");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        // command.add(mp4folder_path  + mp4_name );
        command.add(targetPath);
        String outstring = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            // 将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            outstring = waitFor(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Boolean check_video_time = this.check_video_time(video_path, mp4folder_path + mp4_name);
        Boolean check_video_time = this.checkFileTime(mediaPath, targetPath);
        if (!check_video_time) {
            return outstring;
        } else {
            return "success";
        }
    }

    public static void main(String[] args) throws IOException {
        // ffmpeg的路径
        String ffmpegPath = "E:\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe";
        // 源avi视频的路径
        String sourceFilePath = "D:\\Download\\Media\\Video\\20220212-133833235.avi";
        // 转换后mp4文件的名称
        String targetFileName = "20220212-133833235.mp4";
        // 转换后mp4文件的路径
        String targetPath = "D:\\Download\\Media\\Video\\20220212-133833235.mp4";
        // 创建工具类对象
        MediaConvert2MP4Util videoUtil = new MediaConvert2MP4Util(ffmpegPath, sourceFilePath, targetFileName, targetPath);
        // 开始视频转换，成功将返回success
        String s = videoUtil.generateMp4();
        System.out.println(s);
    }
}