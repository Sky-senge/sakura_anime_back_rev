package com.computerapplicationtechnologycnus.sakura_anime.Initializer;

import com.computerapplicationtechnologycnus.sakura_anime.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {
    private final UserService userService;
    public AdminUserInitializer(UserService userService) {
        this.userService = userService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Override
    public void run(String... args) {
        // 如果有人把管理员封了或者删了，且没有其他管理员的前提下，会自动创建一个默认账户。
        logger.info("开始检查数据库状态...");
        userService.ensureAdminUser();
    }
}

