package com.computerapplicationtechnologycnus.sakura_anime.Initializer;

import com.computerapplicationtechnologycnus.sakura_anime.config.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(FileInitializer.class);
    private static final String[] REQUIRED_DIRECTORIES = {"anime", "avatar", "carousel", "imagelibrary"};

    private final FileStorageProperties fileStorageProperties;

    public FileInitializer(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void run(String... args) {
        // 检测并初始化存储目录
        String uploadDir = fileStorageProperties.getUploadDir();
        File rootDir = new File(uploadDir);

        if (!rootDir.exists()) {
            logger.warn("正在初始化目录: {}", uploadDir);
            if (rootDir.mkdirs()) {
                logger.warn("根目录已创建: {}", uploadDir);
            } else {
                logger.warn("根目录创建失败: {}", uploadDir);
                return;
            }
        }

        for (String subDirName : REQUIRED_DIRECTORIES) {
            File subDir = new File(rootDir, subDirName);
            if (!subDir.exists()) {
                if (subDir.mkdirs()) {
                    logger.warn("子目录已创建: {}", subDir.getAbsolutePath());
                } else {
                    logger.warn("子目录创建失败: {}", subDir.getAbsolutePath());
                }
            }
        }
    }
}
