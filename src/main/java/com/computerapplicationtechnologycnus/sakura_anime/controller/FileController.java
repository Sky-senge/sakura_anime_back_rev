package com.computerapplicationtechnologycnus.sakura_anime.controller;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.config.FileStorageProperties;
import com.computerapplicationtechnologycnus.sakura_anime.services.AnimeService;
import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import com.computerapplicationtechnologycnus.sakura_anime.services.VideoService;
import com.computerapplicationtechnologycnus.sakura_anime.utils.FileTypeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@Schema(description = "文件上上传下载接口")
public class FileController {
    private final FileStorageProperties fileStorageProperties;
    private final FileTypeUtil fileTypeUtil;
    private final UserService userService;
    private final AnimeService animeService;
    private final VideoService videoService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    public static final String FFmpeg_COMMAND = "ffmpeg";

    @Autowired
    public FileController(FileStorageProperties fileStorageProperties,FileTypeUtil fileTypeUtil,UserService userService,AnimeService animeService,VideoService videoService){
        this.fileStorageProperties=fileStorageProperties;
        this.fileTypeUtil=fileTypeUtil;
        this.userService=userService;
        this.animeService=animeService;
        this.videoService=videoService;
    }

    /**
     *
     * @param file 上传的头像文件，通过form-data传输
     * @param userId 自身的UID
     * @param requestHeader 从Token获取的身份验证讯息
     * @return
     */
    @Operation(description = "用户头像上传用接口")
    @PostMapping("/uploadAvatar")
    @AuthRequired(minPermissionLevel = 1)
    public ResultMessage<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            HttpServletRequest requestHeader) {
        try {
            // 验证用户ID是否有效
            if (userId == null || userId <= 0) {
                return ResultMessage.message(false, "用户ID无效！");
            }
            String usernameFromToken = (String) requestHeader.getAttribute("username"); // 从请求中获取 username
            logger.info("用户名："+usernameFromToken);
            Long uidFromDatabase = userService.findUIDByUsername(usernameFromToken); //从数据库获取UID并对比是否本人操作
            if(uidFromDatabase==null){
                return ResultMessage.message(false,"无法查询到指定用户，请更新Token！");
            }
            if(!uidFromDatabase.equals(userId)){
                return ResultMessage.message(false,"修改头像失败，请本人登录！");
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
//            String uniqueFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;
            String uniqueFilename = "avatar_" + userId + fileExtension;
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

    /**
     *
     * @param file 上传的头像文件，通过form-data传输
     * @param userId  欲修改目标的UID
     * @return
     */
    @Operation(description = "管理员头像修改用接口")
    @PostMapping("/modAvatar")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<String> modAvatar(
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
//            String uniqueFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;
            String uniqueFilename = "avatar_" + userId + fileExtension;
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

    /**
     * 动漫资源文件上传用接口
     * @param file  文件部分，二进制数据
     * @param animeId 动漫对应ID
     * @param episodes 第几集，剧场版直接写1
     * @return
     */
    @Operation(description = "动漫资源文件上传用接口，仅限管理员使用")
    @PostMapping("/uploadAnime")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<String> uploadAnime(@RequestParam("file") MultipartFile file,
                                             @RequestParam("animeId") Long animeId,
                                             @RequestParam("episodes")Long episodes) {
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                return ResultMessage.message(false, "文件不能为空！");
            }
            // 验证文件类型是否为视频
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !fileTypeUtil.isVideoFile(originalFilename)) {
                return ResultMessage.message(false, "仅支持上传视频文件（mp4, mkv, avi, mov）！");
            }
            // 获取文件上传路径
            String uploadDirPath = fileStorageProperties.getUploadDir() + "anime/";
            logger.info("本次上传路径: " + uploadDirPath);
            // 检查目录是否存在，不存在则创建
            File dir = new File(uploadDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一文件名（使用时间戳）
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = "anime_" + System.currentTimeMillis() + fileExtension;
            // 保存文件
            File uploadFile = new File(uploadDirPath + uniqueFilename);
            file.transferTo(uploadFile);
            // 启动异步转码任务（转码视频到m3u8格式）
            String videoFilePath = uploadDirPath + uniqueFilename;
            String m3u8OutputPath = uploadDirPath + uniqueFilename + "/m3u8/";
//            convertToM3U8Stream(videoFilePath, m3u8OutputPath, uniqueFilename); // 【已弃用】转码视频
            videoService.convertVideoToM3u8(videoFilePath); // 转码视频
            // 文件唯一名称对应ID路径保存到数据库，去掉mp4之类的扩展名
            String videoName=uniqueFilename.substring(0,uniqueFilename.lastIndexOf('.'));
            animeService.updatePathById(animeId, episodes, videoName);
            return ResultMessage.message(true, "上传完成，转码中。文件名："+uniqueFilename);
        } catch (Exception e) {
            logger.error("视频上传失败", e);
            return ResultMessage.message(false, "视频上传失败！请联系管理员。", e.getMessage());
        }
    }

    /**
     * 动漫字幕文件上传用接口
     * 【注意！】需要先上传了视频资源，才能上传字幕
     *
     * @param file  文件部分，二进制数据
     * @param animeId 动漫对应ID
     * @param episodes 第几集，剧场版直接写1
     * @return
     */
    @Operation(description = "动漫资源文件上传用接口，仅限管理员使用")
    @PostMapping("/uploadSubtitle")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<String> uploadSubtitle(@RequestParam("file") MultipartFile file,
                                             @RequestParam("animeId") Long animeId,
                                             @RequestParam("episodes")Long episodes) {
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                return ResultMessage.message(false, "文件不能为空！");
            }
            // 验证文件类型是否为字幕
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !fileTypeUtil.isSubtitleFile(originalFilename)) {
                return ResultMessage.message(false, "仅支持上传字幕文件（txt, ass, vtt, srt）！");
            }
            // 获取文件上传路径
            String uploadDirPath = fileStorageProperties.getUploadDir() + "anime/";
            logger.info("本次上传路径: " + uploadDirPath);
            // 检查目录是否存在，不存在则创建
            File dir = new File(uploadDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 查询
            // 获取文件上传路径
            String uniqueAnimeMark = animeService.getAnimePathByIdAndEpodies(animeId,episodes);
            String uniqueAnimeDir = uploadDirPath+uniqueAnimeMark+"/";
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = "playlist" + fileExtension;
            // 保存文件
            File uploadFile = new File(uniqueAnimeDir + uniqueFilename);
            file.transferTo(uploadFile);
            return ResultMessage.message(true, "字幕上传完成。动漫标识ID："+uniqueAnimeMark);
        } catch (Exception e) {
            logger.error("字幕上传失败", e);
            return ResultMessage.message(false, "字幕上传失败！请联系管理员。", e.getMessage());
        }
    }

    /**
     * 动漫封面上传用接口，仅限管理员使用，默认在第一集的路径内，cover.jpg/png等等
     * 【注意！】需要先上传了视频资源，才能上传封面
     *
     * @param file 上传的头像文件，通过form-data传输
     * @param animeId 对应动漫的ID
     * @return
     */
    @Operation(description = "动漫封面上传用接口")
    @PostMapping("/uploadCover")
    @AuthRequired(minPermissionLevel = 0)
    public ResultMessage<String> uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam("animeId") Long animeId) {
        try {
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
            String uniqueAnimeDir = animeService.getAnimePathByIdAndEpodies(animeId,1L);
            if(uniqueAnimeDir.isBlank()){
                return ResultMessage.message(false, "封面上传失败，未创建对应资源！");
            }
            String uploadDir = fileStorageProperties.getUploadDir() + "anime/"+uniqueAnimeDir+"/";
            logger.info("本次上传路径: " + uploadDir);

            // 检查目录是否存在，不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成名称
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                    : "";
            String coverFilename = "cover" + fileExtension;
            // 保存文件
            File uploadFile = new File(uploadDir + coverFilename);
            file.transferTo(uploadFile);
            return ResultMessage.message(uniqueAnimeDir + coverFilename,true, "封面上传成功！");
        } catch (Exception e) {
            logger.error("封面上传失败", e);
            return ResultMessage.message(false, "封面上传失败！请联系管理员。", e.getMessage());
        }
    }

    /**
     * 提供封面文件
     *
     * @param requirements 目录路径参数
     * @return 封面文件响应
     */
    @Operation(description = "提供封面文件的接口")
    @GetMapping("/getCover/{requirements}")
    public ResponseEntity<byte[]> getCover(
            @PathVariable String requirements) {
        try {
            // 获取目录路径
            String directoryPath = fileStorageProperties.getUploadDir() + "anime/" + requirements;
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("目录不存在: " + requirements).getBytes());
            }
            // 搜索文件
            File coverFile = null;
            for (String extension : new String[]{"jpg", "jpeg","png"}) {
                File file = new File(directory, "cover." + extension);
                if (file.exists() && file.isFile()) {
                    coverFile = file;
                    break;
                }
            }
            if (coverFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("封面文件不存在".getBytes());
            }
            // 修改 Content-Type
            String contentType = Files.probeContentType(coverFile.toPath());
            if (contentType == null) { //如果无法确定，就丢这个
                contentType = "application/octet-stream";
            }
            // 读取文件内容
            InputStream inputStream = new FileInputStream(coverFile);
            byte[] content = inputStream.readAllBytes();
            inputStream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add("Content-Disposition", "inline; filename=\"" + coverFile.getName() + "\"");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("读取封面文件失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("读取文件失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 轮播图上传功能
     *
     * @param file 轮播图
     * @return
     */
    @Operation(description = "轮播图上传功能")
    @PostMapping("/uploadCarousel")
    @AuthRequired(minPermissionLevel = 0) //仅管理员可上传
    public ResultMessage<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                return ResultMessage.message(false,"文件不能为空！");
            }
            // 验证文件类型是否为图片
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResultMessage.message(false,"仅支持上传图片文件！");
            }
            // 获取上传目录
            String uploadDir = fileStorageProperties.getUploadDir() + "carousel/";
            logger.info("文件上传路径: " + uploadDir);
            // 检查目录是否存在，不存在则创建
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean dirsCreated = dir.mkdirs();
                if (!dirsCreated) {
                    return ResultMessage.message(false,"无法创建文件目录，请联系管理员！");
                }
            }
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                    : "";
            String uniqueFilename = UUID.randomUUID() + fileExtension;
            // 保存文件
            File destinationFile = new File(uploadDir + uniqueFilename);
            file.transferTo(destinationFile);
            logger.info("文件上传成功: " + uniqueFilename);
            return ResultMessage.message(true,"轮播图上传成功，存储路径: " + uniqueFilename);
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return ResultMessage.message(false,"文件上传失败，请联系管理员！");
        }
    }

    /**
     * 获取轮播图文件列表
     * 获取carousel目录下所有图片文件列表
     *
     * @return ResponseEntity<ResultMessage<List<String>>>
     */
    @Operation(description = "获取轮播图文件列表")
    @GetMapping("/getCarouselList")
    public ResponseEntity<ResultMessage<List<String>>> listCarouselImages() {
        try {
            // 获取上传目录
            String uploadDir = fileStorageProperties.getUploadDir() + "carousel/";
            File dir = new File(uploadDir);

            if (!dir.exists() || !dir.isDirectory()) {
                return ResponseEntity.ok(ResultMessage.message(false, "轮播图目录不存在或无效！"));
            }
            // 获取所有图片文件名称列表
            String[] imageExtensions = {".jpg", ".jpeg", ".png"};
            List<String> imageFiles = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                    .filter(file -> file.isFile() && Arrays.stream(imageExtensions).anyMatch(ext -> file.getName().toLowerCase().endsWith(ext)))
                    .map(File::getName)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ResultMessage.message(imageFiles, true, "获取轮播图成功！"));
        } catch (Exception e) {
            logger.error("获取轮播图失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultMessage.message(false, "获取轮播图失败，请联系管理员！"));
        }
    }

    /**
     * 提供轮播图文件
     *
     * @param requirements 目录路径参数
     * @return 封面文件响应
     */
    @Operation(description = "提供轮播图文件的接口")
    @GetMapping("/getCarouse/{requirements}")
    public ResponseEntity<byte[]> getCarouse(
            @PathVariable String requirements) {
        try {
            // 获取目录路径
            String directoryPath = fileStorageProperties.getUploadDir() + "carousel/";
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("目录不存在: " + requirements).getBytes());
            }
            // 搜索文件
            File carouseFile = null;
            for (String extension : new String[]{"jpg", "jpeg","png"}) {
                File file = new File(directory, requirements);
                if (file.exists() && file.isFile()) {
                    carouseFile = file;
                    break;
                }
            }
            if (carouseFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("轮播图文件不存在".getBytes());
            }
            // 修改 Content-Type
            String contentType = Files.probeContentType(carouseFile.toPath());
            if (contentType == null) { //如果无法确定，就丢这个
                contentType = "application/octet-stream";
            }
            // 读取文件内容
            InputStream inputStream = new FileInputStream(carouseFile);
            byte[] content = inputStream.readAllBytes();
            inputStream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.add("Content-Disposition", "inline; filename=\"" + carouseFile.getName() + "\"");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("读取轮播图文件失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("读取文件失败: " + e.getMessage()).getBytes());
        }
    }

    @Operation(description = "动漫资源获取接口")
    @GetMapping("/getVideo/{requirements}/playlist.m3u8")
    public ResponseEntity<byte[]> getM3U8(@PathVariable String requirements) {
        try {
            // 获取 m3u8 文件路径
            String m3u8FilePath = fileStorageProperties.getUploadDir() + "anime/" + requirements + "/playlist.m3u8";
            File m3u8File = new File(m3u8FilePath);

            if (!m3u8File.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("文件不存在: " + m3u8FilePath).getBytes());
            }

            // 读取文件内容
            byte[] content = Files.readAllBytes(m3u8File.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"));
            headers.add("Content-Disposition", "inline; filename=\"playlist.m3u8\"");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("读取 m3u8 文件失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("读取文件失败: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 提供 ts等资源文件
     *
     * @param requirements 视频路径参数
     * @param tsFileName   ts 文件名（其实只要是资源都可以，包括ass等字幕）
     * @return 文件响应
     */
    @Operation(description = "提供TS文件的接口")
    @GetMapping("/getVideo/{requirements}/{tsFileName}")
    public ResponseEntity<byte[]> getTsFile(
            @PathVariable String requirements,
            @PathVariable String tsFileName) {
        try {
            // 获取 ts 文件路径
            String tsFilePath = fileStorageProperties.getUploadDir() + "anime/" + requirements + "/" + tsFileName;
            File tsFile = new File(tsFilePath);

            if (!tsFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("文件不存在: " + tsFilePath).getBytes());
            }

            // 读取文件内容
            InputStream inputStream = new FileInputStream(tsFile);
            byte[] content = inputStream.readAllBytes();
            inputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Content-Disposition", "inline; filename=\"" + tsFileName + "\"");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("读取 ts 文件失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("读取文件失败: " + e.getMessage()).getBytes());
        }
    }

}
