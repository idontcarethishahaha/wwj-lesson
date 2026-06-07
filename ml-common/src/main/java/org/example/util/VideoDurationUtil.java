package org.example.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoDurationUtil {

    /**
     * 获取视频总时长 单位：秒
     */
    public static long getVideoDurationSeconds(MultipartFile file) throws Exception {
        // 先把上传的文件转为临时File
        File tempFile = convertMultipartFileToFile(file);
        long duration = 0;

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile)) {
            grabber.start();
            // 微秒 → 秒
            duration = grabber.getLengthInTime() / 1000000;
            grabber.stop();
        } finally {
            tempFile.delete(); // 删除临时文件
        }
        return duration;
    }

    /**
     * 获取格式化时长：HH:mm:ss
     */
    public static String getVideoDurationFormat(MultipartFile file) throws Exception {
        long seconds = getVideoDurationSeconds(file);
        return formatSeconds(seconds);
    }

    // ====================== 工具方法 ======================
    private static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("tempVideo", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private static String formatSeconds(long seconds) {
        long hour = seconds / 3600;
        long minute = (seconds % 3600) / 60;
        long second = seconds % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}