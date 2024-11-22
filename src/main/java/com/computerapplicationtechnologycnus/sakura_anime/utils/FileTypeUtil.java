package com.computerapplicationtechnologycnus.sakura_anime.utils;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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

}
