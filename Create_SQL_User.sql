-- 该脚本用于创建一个低权限等级的SQL用户进行数据库操作
-- 避免系统被入侵后造成更大损失
-- 该用户对数据库拥有对目标数据库(sakura_anime)的基本增删改查(CRUD)权限
-- 
-- 默认数据库名：sakura_anime
-- 默认用户名：sakura_manager
-- 默认密码：manager
-- 默认主机：localhost

-- 检查并删除现有的 sakura_manager 用户（如有）
DROP USER IF EXISTS 'sakura_manager'@'localhost';

-- 创建新的 sakura_manager 用户
CREATE USER 'sakura_manager'@'localhost' IDENTIFIED BY 'manager';

-- 授予 sakura_anime 数据库的基本权限
GRANT SELECT, INSERT, UPDATE, DELETE ON sakura_anime.* TO 'sakura_manager'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
