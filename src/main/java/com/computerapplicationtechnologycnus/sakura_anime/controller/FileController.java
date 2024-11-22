package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.config.FileStorageProperties;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import com.computerapplicationtechnologycnus.sakura_anime.utils.FileTypeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/files")
@Schema(description = "文件上上传下载接口")
public class FileController {
    private final FileStorageProperties fileStorageProperties;
    private final FileTypeUtil fileTypeUtil;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(FileStorageProperties fileStorageProperties,FileTypeUtil fileTypeUtil,UserService userService){
        this.fileStorageProperties=fileStorageProperties;
        this.fileTypeUtil=fileTypeUtil;
        this.userService=userService;
    }

    @Operation(description = "头像上传用接口")
    @PostMapping("/uploadAvatar")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            // 验证用户ID是否有效
            if (userId == null || userId <= 0) {
                return ResultMessage.message(false, "用户ID无效！");
            }
            // 验证文件是否为空
            if (file.isEmpty()) {
                return ResultMessage.message(false, "文件不能为空！");
            }
            // 验证文件类型是否为图片
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResultMessage.message(false, "仅支持上传图片文件！");
            }
            // 获取文件上传路径
            String uploadDir = fileStorageProperties.getUploadDir() + "avatar/";
            logger.info("本次上传路径: " + uploadDir);

            // 检查目录是否存在，不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名（使用用户ID和时间戳）
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                    : "";
            String uniqueFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;

            // 保存文件
            File uploadFile = new File(uploadDir + uniqueFilename);
            file.transferTo(uploadFile);
            // 将文件名保存到数据库
            userService.saveAvatarToDatabase(userId, uniqueFilename);
            return ResultMessage.message(uniqueFilename,true, "上传成功，文件名：" + uniqueFilename);
        } catch (Exception e) {
            logger.error("头像上传失败", e);
            return ResultMessage.message(false, "头像上传失败！请联系管理员。", e.getMessage());
        }
    }



    @Operation(description = "头像下载接口")
    @GetMapping("/getAvatar/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            // 获取文件路径
            String uploadDir = fileStorageProperties.getUploadDir() + "avatar/";
            File file = new File(uploadDir + filename);
            // 检查文件是否存在
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            // 转到流传输
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileContent = StreamUtils.copyToByteArray(fileInputStream);
            fileInputStream.close();
            // 获取文件扩展名并设置ContentType
            String extension = fileTypeUtil.getFileExtension(filename);
            MediaType mediaType = fileTypeUtil.getMediaType(extension);
            if (mediaType == null) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM; // 默认二进制流类型
            }
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("头像下载失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
