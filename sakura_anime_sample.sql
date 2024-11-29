-- MariaDB dump 10.19  Distrib 10.11.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: sakura_anime
-- ------------------------------------------------------
-- Server version	10.11.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `anime`
--

DROP TABLE IF EXISTS `anime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anime` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `tags` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`tags`)),
  `description` text DEFAULT NULL,
  `rating` float(3,1) DEFAULT 0.0 CHECK (`rating` between 1 and 10),
  `release_date` timestamp NULL DEFAULT current_timestamp(),
  `file_path` longtext NOT NULL CHECK (json_valid(`file_path`)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anime`
--

LOCK TABLES `anime` WRITE;
/*!40000 ALTER TABLE `anime` DISABLE KEYS */;
INSERT INTO `anime` VALUES
(1,'秒速5厘米','[\"日系\", \"治愈\"]','新海誠執導的2007年日本動畫電影',9.5,'2024-11-18 06:44:04','[]'),
(2,'寒蝉鸣泣之时','[\"日系\",\"治愈\",\"寒蝉\"]','人坏掉，蝉在叫',1.0,'2024-11-20 17:06:31','[]'),
(3,'Angel Beats!','[\"日系\",\"治愈\",\"射击\"]','是一部由游戏制作公司Key与Aniplex联合制作的日本动画',8.5,'2024-11-20 17:06:31','[{\"episodes\":1,\"fileName\":\"anime_1732528204793\"},{\"episodes\":2,\"fileName\":\"anime_1732596771347\"}]'),
(4,'迷宫饭','[\"日系\",\"治愈\"]','反正是吃货',9.5,'2024-11-22 07:01:33','[{\"episodes\":1,\"fileName\":\"anime_1732528204793\"}]'),
(5,'败犬女主太多了','[\"日系\",\"轻小说\",\"恋爱\"]','平常担任班上背景人物的我──温水和彦，偶然目击人气女同学八奈见杏菜被男生甩掉的失恋现场',9.5,'2024-11-22 07:01:33','[{\"episodes\":1,\"fileName\":\"anime_1732528204793\"}]'),
(6,'你的名字','[\"日系\", \"浪漫\", \"青春\"]','新海诚执导的穿越与爱情故事',9.0,'2024-11-20 02:00:00','[]'),
(7,'千与千寻','[\"日系\", \"奇幻\", \"治愈\"]','宫崎骏执导的关于成长的故事',9.8,'2024-11-19 10:30:00','[]'),
(8,'进击的巨人','[\"日系\", \"动作\", \"黑暗\"]','围绕巨人与人类生存展开的故事',9.7,'2024-11-22 00:00:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204901\"}]'),
(9,'鬼灭之刃','[\"日系\", \"动作\", \"感人\"]','关于鬼猎人的冒险与情感羁绊',9.6,'2024-11-21 12:15:00','[]'),
(10,'一拳超人','[\"日系\", \"搞笑\", \"英雄\"]','埼玉老师拳拳到肉的搞笑故事',8.8,'2024-11-23 04:45:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204902\"}]'),
(11,'咒术回战','[\"日系\", \"战斗\", \"黑暗\"]','少年与咒灵的战斗之旅',9.1,'2024-11-21 08:00:00','[]'),
(12,'全职猎人','[\"日系\", \"冒险\", \"经典\"]','追寻猎人的冒险故事',9.5,'2024-11-22 02:00:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204903\"},{\"episodes\":2,\"fileName\":\"anime_1732528204793\"},{\"episodes\":3,\"fileName\":\"anime_1732528204793\"}]'),
(13,'CLANNAD','[\"日系\", \"感人\", \"治愈\"]','平凡生活中的温馨与感动',9.4,'2024-11-20 06:20:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204904\"}]'),
(14,'钢之炼金术师','[\"日系\", \"战斗\", \"感人\"]','追求真理的炼金术师兄弟故事',9.9,'2024-11-21 11:00:00','[]'),
(15,'黑执事','[\"日系\", \"哥特\", \"神秘\"]','执事与少爷之间的黑暗契约',8.5,'2024-11-20 09:00:00','[]'),
(16,'Re:从零开始的异世界生活','[\"日系\", \"穿越\", \"悬疑\"]','穿越到异世界的求生冒险',9.3,'2024-11-23 06:10:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204905\"}]'),
(17,'未来日记','[\"日系\", \"悬疑\", \"惊悚\"]','通过未来日记改变命运的故事',8.2,'2024-11-20 05:15:00','[]'),
(18,'夏目友人帐','[\"日系\", \"治愈\", \"奇幻\"]','人与妖怪的温馨故事',9.7,'2024-11-20 08:30:00','[]'),
(19,'涼宮春日的忧郁','[\"日系\", \"校园\", \"穿越\"]','一个充满科幻与奇幻的日常',8.9,'2024-11-23 10:45:00','[]'),
(20,'灌篮高手','[\"日系\", \"运动\", \"青春\"]','关于篮球与青春的经典故事',9.8,'2024-11-20 11:00:00','[]'),
(21,'名侦探柯南','[\"日系\", \"推理\", \"长篇\"]','经典推理动漫',9.4,'2024-11-22 12:00:00','[]'),
(22,'七大罪','[\"日系\", \"战斗\", \"奇幻\"]','七名骑士拯救国家的奇幻冒险',8.4,'2024-11-21 04:30:00','[]'),
(23,'死亡笔记','[\"日系\", \"悬疑\", \"推理\"]','关于死亡笔记的智斗故事',9.2,'2024-11-22 14:00:00','[]'),
(24,'东方Project','[\"日系\", \"奇幻\", \"游戏改编\"]','神秘幻想乡的冒险',8.6,'2024-11-21 07:20:00','[]'),
(25,'银魂','[\"日系\", \"搞笑\", \"战斗\"]','无厘头的搞笑与感人交织',9.3,'2024-11-22 06:10:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204906\"}]'),
(26,'我们仍未知道那天所看见的花的名字','[\"日系\", \"感人\", \"治愈\"]','关于童年与友情的感人故事',9.1,'2024-11-20 03:00:00','[]'),
(27,'刀剑神域','[\"日系\", \"游戏\", \"冒险\"]','虚拟世界中的生存挑战',8.9,'2024-11-23 03:30:00','[]'),
(28,'JOJO的奇妙冒险','[\"日系\", \"战斗\", \"经典\"]','乔斯达家族的传承与冒险',9.2,'2024-11-20 01:00:00','[]'),
(29,'紫罗兰永恒花园','[\"日系\", \"治愈\", \"感人\"]','关于爱与成长的温暖故事',9.6,'2024-11-21 09:00:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204907\"}]'),
(30,'恶魔高校D×D','[\"日系\", \"校园\", \"奇幻\"]','充满热血与奇幻的校园生活',8.0,'2024-11-22 05:00:00','[]'),
(31,'排球少年','[\"日系\", \"运动\", \"青春\"]','关于排球的青春与奋斗',9.4,'2024-11-22 03:00:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204908\"}]'),
(32,'魔法禁书目录','[\"日系\", \"奇幻\", \"战斗\"]','科学与魔法碰撞的世界',8.8,'2024-11-23 07:30:00','[]'),
(33,'魔法少女小圆','[\"日系\", \"奇幻\", \"黑暗\"]','颠覆传统的魔法少女故事',9.5,'2024-11-22 10:30:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204909\"}]'),
(34,'Fate/Zero','[\"日系\", \"战斗\", \"史诗\"]','圣杯战争的序章',9.7,'2024-11-21 13:00:00','[{\"episodes\":1,\"fileName\":\"anime_1732528204910\"}]');
/*!40000 ALTER TABLE `anime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `anime_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `anime_id` (`anime_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES
(1,1,1,'我去','2024-11-21 16:24:23'),
(2,1,2,'很好看哦www','2024-11-21 16:44:39'),
(3,1,1,'赔我眼泪！！！','2024-11-22 04:01:23'),
(4,27,3,'制作组良心发现，终于没有画质砍一刀','2024-11-24 01:14:52'),
(5,34,1,'配音演员真的太棒了！','2024-11-24 11:57:40'),
(6,1,1,'剧情太感人了，完全泪目！','2024-11-22 04:01:23'),
(7,2,2,'角色设计好棒，特别喜欢女主！','2024-11-22 06:23:45'),
(8,3,3,'这部动画的打斗场面太帅了！','2024-11-22 08:15:12'),
(9,4,1,'有点慢热，但后面真的很棒！','2024-11-22 10:35:20'),
(10,5,2,'每一集都让我笑到肚子疼，推荐！','2024-11-22 12:45:10'),
(11,6,3,'音乐特别好听，很符合剧情的氛围。','2024-11-22 14:05:33'),
(12,7,1,'画风很精致，超出了预期。','2024-11-22 15:55:44'),
(13,8,2,'虽然故事套路老，但依然好看！','2024-11-22 17:12:08'),
(14,9,3,'有点偏暗黑系，但还是很吸引人。','2024-11-22 19:15:20'),
(15,10,1,'这部动画完全是神作，强推！','2024-11-22 22:22:37'),
(16,11,2,'笑点和泪点都很到位，值得一看。','2024-11-23 00:34:25'),
(17,12,3,'人物刻画细腻，让人印象深刻。','2024-11-23 01:45:12'),
(18,13,1,'结局有点仓促，希望有第二季！','2024-11-23 03:58:47'),
(19,14,2,'每一集都让我意犹未尽。','2024-11-23 05:10:32'),
(20,15,3,'动作场面燃到爆炸！','2024-11-23 06:45:20'),
(21,16,1,'主角成长线很棒，看着很舒服。','2024-11-23 08:12:15'),
(22,17,2,'虽然节奏有点快，但情节很紧凑。','2024-11-23 09:23:40'),
(23,18,3,'笑点和梗的安排都很用心。','2024-11-23 10:35:00'),
(24,19,1,'希望出更多这样的原创动画！','2024-11-23 11:45:27'),
(25,20,2,'很久没有看到这么良心的动画了。','2024-11-23 12:57:38'),
(26,21,3,'有些细节需要多看几遍才能发现。','2024-11-23 14:12:50'),
(27,22,1,'故事真的很有深度，推荐！','2024-11-23 16:01:25'),
(28,23,2,'每一个配角都很有魅力。','2024-11-23 17:20:37'),
(29,24,3,'这部动画的设定很吸引人！','2024-11-23 19:33:40'),
(30,25,1,'剧情发展太让人意外了！','2024-11-23 21:44:12'),
(31,26,2,'人物关系的描写很真实感人。','2024-11-23 23:55:27'),
(32,27,3,'光影效果很棒，制作很用心。','2024-11-24 01:14:52'),
(33,28,1,'每一集都让我期待不已！','2024-11-24 03:22:33'),
(34,29,2,'剧情伏笔埋得很好，后面爆发很精彩。','2024-11-24 05:10:15'),
(35,30,3,'虽然是老IP，但完全不输现在的新番。','2024-11-24 06:45:20'),
(36,31,1,'希望制作组继续保持高水准！','2024-11-24 08:12:55'),
(37,32,2,'这部动画的分镜很有创意。','2024-11-24 09:25:10'),
(38,33,3,'情感的细腻描写让人代入感很强。','2024-11-24 10:45:22'),
(39,34,1,'配音演员真的太厉害了！','2024-11-24 11:57:40');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `anime_id` bigint(20) NOT NULL,
  `episodes` bigint(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `anime_id` (`anime_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE CASCADE,
  CONSTRAINT `history_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES
(1,1,1,1,'2024-11-29 03:38:04'),
(2,1,1,1,'2024-11-29 03:38:04'),
(3,2,1,1,'2024-11-29 03:38:04');
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `permission` int(11) DEFAULT 0,
  `password` varchar(255) NOT NULL,
  `display_name` varchar(50) DEFAULT NULL,
  `remarks` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,NULL,'example@example.com','admin',0,'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=','admin','This is a Admin User.'),
(2,'avatar_2_1732250228664.jpg','ff@example.com','testuser1',1,'OE/eNjbm4B4BlNKXbY8mQQrz6EblczecsaCeLwdS2Mw=','Test User','This is a test user.'),
(3,'avatar_3.jpg','ee@example.com','testuser3',1,'75K3eLr+dx6JJFuJ7LwIpEpOFmwGZZkRiB84PURz6U8=','Test User','This is a test user.');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'sakura_anime'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-29 12:44:30
