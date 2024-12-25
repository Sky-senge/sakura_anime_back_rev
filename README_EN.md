# sakura_anime_backend

Sakura Anime (Graduation Project) - Backend

---

## Requirements

- **Java 17**
- **Maven**
- **FFmpeg**: Ensure FFmpeg is properly installed and configured. You can download it from [here](https://www.ffmpeg.org/download.html).
- **MariaDB 10.11**: Import the database with the default name `sakura_anime`.

---

### Notes:

**`application.properties` is no longer recommended to be tracked by Git** to avoid startup issues caused by updates from team members. Instead, use the `application.properties.template.temp` file in the same directory as a template. After downloading, configure your `application.properties` file based on this template.

To ensure your Git setup excludes this configuration file, run the following Git command in the project directory after cloning or downloading:

```bash
git update-index --assume-unchanged src/main/resources/application.properties
```

If you need to update it, replace the above with the following command:

```bash
git update-index --no-assume-unchanged src/main/resources/application.properties
```

---

### **Tips:**

1. **File Encoding:**  
   If you encounter garbled text in your IDE, set the workspace file encoding to `UTF-8`. Some environments default to `ISO-8859-1` or other incorrect encodings, leading to display and save errors. Always ensure the correct encoding is applied before working to avoid future issues.

2. **Git Exclusion:**  
   Remember to configure Git to exclude tracking of the `application.properties` file as described above.

3. **Hardware Requirements:**  
   Since this project involves video encoding/decoding, it is recommended to run it on a machine with an NVIDIA GPU or a powerful CPU.

4. **Postman for API Testing:**  
   Use Postman version `9.31.28`. If you don't have it, download it from [this link](https://github.com/Radium-bit/postman_noLogin_backup/releases/tag/9.31.28).

5. **Database Security:**  
   For improved security, follow the `Create_SQL_User.sql` template to create a low-privilege database user for managing the project database.

---

## API Documentation

The latest API documentation can be found in the Postman test cases.

It is recommended to directly refer to the categorized API requests in Postman.

---

# Instructions

## Running in your IDE

1. Configure **OpenJDK 17 (LTS)**.
2. Install and configure **Apache Maven 3.9.6**.
3. Open the project in an IDE (recommended: `IntelliJ IDEA 2024`).
4. Install FFmpeg and set up the environment variables.
5. Install MariaDB 10.11 and import the `sakura_anime_sample.sql` database.
6. Optionally, create a runtime database user based on the `Create_SQL_User.sql` template.
7. Configure the `application.properties` file based on the provided template(.temp File).
8. In the IDE, set the main class to `SakuraAnimeBackendApplication` and run the project.

---

## Building a JAR File for Deployment

1. Configure **OpenJDK 17 (LTS)**.

2. Install and configure **Apache Maven 3.9.6**.

3. Open the project in an IDE (recommended: `IntelliJ IDEA 2024`).

4. Install FFmpeg and set up the environment variables.

5. Install MariaDB 10.11 and import the `sakura_anime_sample.sql` database.

6. Optionally, create a runtime database user based on the `Create_SQL_User.sql` template.

7. Start the database server.

8. Configure the `application.properties` file based on the provided template(.temp FIle).

9. Navigate to the project root directory and run the following command:
   
   ```bash
   mvn clean package
   ```

10. If the command runs successfully, locate the compiled file in the `./target` directory.

11. Extract the compiled `Sakura_Anime-0.1.0-ALPHA.jar` file and the configured `application.properties` file to a suitable location.

12. Run the following command to start the application:
    
    ```bash
    java -jar application.jar --spring.config.location=file:/full/path/to/application.properties
    ```
    
    Example:
    
    ```shell
    java -jar ./target/Sakura_Anime-0.1.0-ALPHA.jar --spring.config.location=file:/D:/Sakura_Anime/application.properties
    ```

13. If successful, you can access the application via the configured port.
