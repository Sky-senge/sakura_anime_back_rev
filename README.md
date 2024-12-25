# sakura_anime_backend

樱花动漫(毕设)-后端

## 运行需求/Requirements

Java 17，

Maven

已经配置好的ffmpeg，可以到[这里下载](https://www.ffmpeg.org/download.html)

MariaDB 10.11 并导入数据库，数据库默认名为```sakura_anime```

#### 注意：

**application.properties现在不建议受Git更新，避免由于开发组成员更新导致的启动问题。取而代之的是同目录下的** ```application.properties模板.temp``` **文件，它会提供一个很好的模板，下载之后请根据它来配置你的properties文件。**

为了确保你的git会排除掉这个配置文件，请你先在克隆/下载项目的目录下，执行一行Git命令，确保排除它。

```bash
git update-index --assume-unchanged src/main/resources/application.properties
```

如果有需要确实要更新它，那么请替换为以下参数

```bash
git update-index --no-assume-unchanged
```

**Tips:**

1. 如果你打开你的IDE，发现代码或者配置存在乱码，请把工作区的文件编码都改成UTF-8，有的环境默认是ISO-8859-1或其他不正确的编码导致显示/保存错误。请在工作前务必修改为正确的UTF-8，避免后续麻烦。

2. 请记得配置上述排除application.properties文件的Git追踪。

3. 由于项目涉及视频编解码，建议在拥有NVIDIA GPU或者强大算力CPU的计算机运行。

4. 使用Postman或新增样例请使用9.31.28版本，没有可以在[这里下载](https://github.com/Radium-bit/postman_noLogin_backup/releases/tag/9.31.28)。

5. 如有可能，建议遵照`Create_SQL_User.sql`的规范创建一个用于管理项目数据库的，拥有最低权限的数据库用户，以保障安全性。

## API文档：

最新的API文档请直接导入Postman的测试用例作为参考。

因此建议直接参考Postman中的接口请求，分类也做好了。

# 运行说明：

## 在IDE中运行

1. 配置OpenJDK，版本为 17(LTS)

2. 安装并配置Apache Maven 3.9.6

3. 使用IDE打开项目，建议使用`IntelliJ IDEA 2024`

4. 安装FFMPEG，并配置环境变量

5. 安装MariaDB 10.11 并导入数据库`sakura_anime_sample.sql`

6. 根据模板`Create_SQL_User.sql`创建合适的运行时用户（建议）

7. 根据模板，配置`application.properties`

8. 进入IDE，运行主类选择为`SakuraAnimeBackendApplication`

## 编译为Jar运行

1. 配置OpenJDK，版本为 17(LTS)

2. 安装并配置Apache Maven 3.9.6

3. 使用IDE打开项目，建议使用`IntelliJ IDEA 2024`（建议）

4. 安装FFMPEG，并配置环境变量

5. 安装MariaDB 10.11 并导入数据库`sakura_anime_sample.sql`

6. 根据模板`Create_SQL_User.sql`创建合适的运行时用户（建议）

7. 运行，启动数据库

8. 根据模板，配置`application.properties`

9. 定位到项目根目录，运行命令`mvn clean package`

10. 如命令正常运行，则打开项目下的`./target`子目录

11. 提取其中编译好的的`Sakura_Anime-0.1.0-ALPHA.jar`文件和配置好的`application.properties`到合适位置

12. 执行命令`java -jar 应用程序.jar --spring.config.location=file:/配置文件完整路径`   以下是一个例子：
    
    `java -jar ./target/Sakura_Anime-0.1.0-ALPHA.jar --spring.config.location=file:/D:/Sakura_Anime/application.properties`

13. 如运行成功，通过配置好的端口即可访问数据。