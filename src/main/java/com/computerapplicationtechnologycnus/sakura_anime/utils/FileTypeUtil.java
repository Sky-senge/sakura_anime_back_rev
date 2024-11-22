package com.computerapplicationtechnologycnus.sakura_anime.utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class FileTypeUtil {
    public String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        return (lastIndex == -1) ? "" : filename.substring(lastIndex + 1).toLowerCase();
    }

    public MediaType getMediaType(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return null; // 未知类型
        }
    }

    /**
     * 验证文件是否为支持的视频类型
     * @param filename 文件名
     * @return 是否为视频文件
     */
    public boolean isVideoFile(String filename) {
        String[] allowedExtensions = { "mp4", "mkv", "avi", "mov" };
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return Arrays.asList(allowedExtensions).contains(fileExtension);
    }

}
