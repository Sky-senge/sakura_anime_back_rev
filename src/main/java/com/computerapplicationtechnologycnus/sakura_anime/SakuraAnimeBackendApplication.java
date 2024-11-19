package com.computerapplicationtechnologycnus.sakura_anime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan  // 确保启用 WebFilter
public class SakuraAnimeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SakuraAnimeBackendApplication.class, args);
    }

}
