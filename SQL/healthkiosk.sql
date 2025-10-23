-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: healthKiosk
-- ------------------------------------------------------
-- Server version	5.7.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `data`
--

DROP TABLE IF EXISTS `data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data` (
  `user_id` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '用户account',
  `tempor` varchar(50) DEFAULT NULL COMMENT '体温',
  `alcohol` varchar(50) DEFAULT NULL COMMENT '酒精',
  `ecg` varchar(50) DEFAULT NULL COMMENT '心电',
  `spo2` varchar(50) DEFAULT NULL COMMENT '血氧',
  `ppg` varchar(50) DEFAULT NULL COMMENT '光电容积脉搏波',
  `blood_sys` varchar(50) DEFAULT NULL COMMENT '收缩压',
  `blood_dia` varchar(50) DEFAULT NULL COMMENT '舒张压',
  `blood_hr` varchar(50) DEFAULT NULL COMMENT '心率',
  `tire` varchar(50) DEFAULT NULL COMMENT '疲劳程度',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '测试时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='体检数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data`
--

LOCK TABLES `data` WRITE;
/*!40000 ALTER TABLE `data` DISABLE KEYS */;
/*!40000 ALTER TABLE `data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device` (
  `version` varchar(50) NOT NULL COMMENT '软件版本',
  `description` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '该版本描述',
  `type` enum('1','2') NOT NULL COMMENT '软件类型(1表示release,2表示debug)',
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '下载量',
  PRIMARY KEY (`version`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='软件版本表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES ('v0.1.0-alpha','开发版','1','2025-10-08 11:51:59',2),('v0.1.1-beta','可用的版本','1','2025-10-16 15:22:02',0);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function`
--

DROP TABLE IF EXISTS `function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `function` (
  `function_id` int(11) NOT NULL AUTO_INCREMENT,
  `function_key` varchar(50) NOT NULL COMMENT 'key标识',
  `name` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '功能名称',
  `remark` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '说明',
  `parent` int(11) DEFAULT NULL COMMENT '父功能id',
  `class` enum('menu','sub','btn') NOT NULL COMMENT '类型',
  PRIMARY KEY (`function_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1 COMMENT='功能表，key对应Sidebar中各个menu的键';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function`
--

LOCK TABLES `function` WRITE;
/*!40000 ALTER TABLE `function` DISABLE KEYS */;
INSERT INTO `function` VALUES (1,'users','用户管理','用户管理模块',NULL,'menu'),(2,'users:list','用户列表',NULL,1,'sub'),(3,'devices','软件管理','软件管理模块',NULL,'menu'),(4,'devices:list','软件列表',NULL,3,'sub'),(5,'examdata','体检数据','体检数据模块',NULL,'menu'),(6,'examdata:list','体检数据趋势','体检数据趋势',5,'sub'),(7,'info','个人信息','个人信息模块',NULL,'menu'),(8,'info:selfinfo','个人信息设置','展示和修改个人信息',7,'sub'),(9,'info:selfexam','个人体检记录','查询个人体检记录',7,'sub'),(10,'item','体检项目模块','体检项目模块',NULL,'menu'),(11,'item:list','体检项目列表','体检项目列表',10,'sub'),(12,'use','使用说明','使用说明模块',NULL,'menu'),(13,'use:instruction','用户使用说明','为用户提供系统使用说明',12,'sub'),(14,'use:about','关于系统','系统基本说明',12,'sub'),(15,'use:admin','管理员使用说明','为管理员提供管理说明',12,'sub'),(16,'role','权限模块','权限模块',NULL,'menu'),(17,'role:role','角色管理','角色管理',16,'sub'),(18,'role:permission','权限分配','权限分配',16,'sub');
/*!40000 ALTER TABLE `function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '名称',
  `abbreviation` varchar(20) NOT NULL COMMENT '简称',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '0-disable,1-able',
  `description` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '描述',
  `usage` text CHARACTER SET utf8mb4 COMMENT '使用说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1 COMMENT='体检项目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'体温','tempor',1,'用于测量体表温度','<p>靠近安卓屏幕摄像头，将<strong>自动</strong>进行体温测量，并显示体表温度。</p>'),(2,'酒精','alcohol',1,'测量酒精含量','<p><br></p>'),(3,'心电','ecg',1,'',''),(4,'血氧','spo2',1,'',''),(5,'光电容积脉搏波','ppg',1,'测量脉搏','<p><br></p>'),(6,'收缩压','blood_sys',1,'',''),(7,'舒张压','blood_dia',1,'',''),(8,'心率','blood_hr',1,'',''),(9,'疲劳程度','tire',0,NULL,'<p><br></p>');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `permission_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(2) NOT NULL,
  `function_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`permission_id`) USING BTREE,
  KEY `FK_permission_role` (`role_id`) USING BTREE,
  KEY `FK_permission_function` (`function_id`) USING BTREE,
  CONSTRAINT `FK_permission_function` FOREIGN KEY (`function_id`) REFERENCES `function` (`function_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=latin1 COMMENT='各个功能对应的角色权限需求，即每个功能需要何种角色才可访问';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (2,3,1),(3,4,1),(4,5,1),(5,3,2),(6,4,2),(7,5,2),(8,0,3),(9,1,3),(10,2,3),(11,3,3),(12,4,3),(13,5,3),(14,0,4),(15,1,4),(16,2,4),(17,3,4),(18,4,4),(19,5,4),(20,2,5),(21,3,5),(22,4,5),(23,5,5),(24,2,6),(25,3,6),(26,4,6),(27,5,6),(28,1,7),(29,2,7),(30,3,7),(31,4,7),(32,5,7),(33,1,8),(34,2,8),(35,3,8),(36,4,8),(37,5,8),(38,2,9),(39,3,9),(40,4,9),(41,5,9),(42,3,10),(43,4,10),(44,5,10),(45,3,11),(46,4,11),(47,5,11),(48,0,12),(49,1,12),(50,2,12),(51,3,12),(52,4,12),(53,5,12),(54,0,13),(55,1,13),(56,2,13),(57,3,13),(58,4,13),(59,5,13),(60,0,14),(61,1,14),(62,2,14),(63,3,14),(64,4,14),(65,5,14),(66,3,15),(67,4,15),(68,5,15),(69,3,16),(70,4,16),(71,5,16),(72,3,17),(73,4,17),(74,5,17),(75,3,18),(76,4,18),(77,5,18);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role_id` int(2) NOT NULL COMMENT '角色识别标识符',
  `role_name` varchar(12) CHARACTER SET utf8mb4 NOT NULL COMMENT '角色名称',
  `remark` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '说明',
  `use` int(1) DEFAULT '1' COMMENT '是否启用',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (0,'游客','未登录情况下自动识别为本角色',1),(1,'访客','无系统认证的个人主动完成登录的用户',1),(2,'用户','系统认证的用户，如铁路司机',1),(3,'管理员','分配的系统管理员，可进行系统管理',1),(4,'超级管理员','认定的超级管理员，可进行系统管理和管理员分配',1),(5,'开发者','可对系统进行开发过程中的变更',1);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `account` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '唯一身份标识符',
  `name` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '名称',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `gender` enum('男','女') CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '性别',
  `pic` varchar(50) DEFAULT NULL COMMENT '图片的路径表示',
  `pwd` varchar(100) NOT NULL COMMENT 'sha256密码',
  `height` int(11) DEFAULT NULL COMMENT '身高',
  `weight` int(11) DEFAULT NULL COMMENT '体重',
  `role` int(11) NOT NULL COMMENT '游客0，访客1，用户2，管理员3',
  `email_enc` varchar(255) DEFAULT NULL COMMENT 'aes256邮箱',
  `email_hash` varchar(100) DEFAULT NULL COMMENT '邮箱的hash',
  `key_version` varchar(2) DEFAULT NULL COMMENT '密钥版本',
  `register_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  PRIMARY KEY (`account`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('10','10',NULL,NULL,NULL,'4a44dc15364204a80fe80e9039455cc1608281820fe2b24f1e5233ade6af1dd5',NULL,NULL,1,NULL,NULL,NULL,'2025-10-08 17:03:19',NULL),('11','testUser',NULL,NULL,NULL,'5772527c5398c3b1d9999c5a9388823c454126d8a387a32ce461ad7cfc13f656',NULL,NULL,1,NULL,NULL,NULL,'2025-10-18 19:12:41',NULL),('2','2',NULL,NULL,NULL,'d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35',NULL,NULL,2,NULL,NULL,NULL,'2025-10-04 16:34:25',NULL),('3','3',NULL,NULL,NULL,'4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce',NULL,NULL,1,NULL,NULL,NULL,'2025-10-03 16:34:31',NULL),('5','5',NULL,NULL,NULL,'ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d',NULL,NULL,1,NULL,NULL,NULL,'2025-10-04 16:34:40',NULL),('6','6',NULL,NULL,NULL,'e7f6c011776e8db7cd330b54174fd76f7d0216b612387a5ffcfb81e6f0919683',NULL,NULL,1,NULL,NULL,NULL,'2025-10-08 17:02:56',NULL),('7','7',NULL,NULL,NULL,'7902699be42c8a8e46fbbb4501726517e86b22c56a189f7625a6da49081b2451',NULL,NULL,1,NULL,NULL,NULL,'2025-10-08 17:03:04',NULL),('8','8',NULL,NULL,NULL,'2c624232cdd221771294dfbb310aca000a0df6ac8b66b696d90ef06fdefb64a3',NULL,NULL,1,NULL,NULL,NULL,'2025-10-08 17:03:07',NULL),('9','9',NULL,NULL,NULL,'19581e27de7ced00ff1ce50b2047e7a567c76b1cbaebabe5ef03f7c3017bb5b7',NULL,NULL,1,NULL,NULL,NULL,'2025-10-08 17:03:13',NULL),('admin','admin',30,'男','1757753904644.jpg','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',180,65,5,'ZntTALWJ2M2MpTnOBOtYJSm76KGWzfN3p5OrU+hdO5Q4mpL6W7DFmC73Av+2diMP','429ca99f1adb243b70017eae55ed8967dd6077f53d030f83300583e269ca0fd5','v1','2025-10-06 14:08:59',NULL),('show1','测试2',NULL,NULL,NULL,'show1',NULL,NULL,0,NULL,NULL,NULL,'2025-10-03 16:08:58',NULL),('user','user',NULL,'男',NULL,'04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb',NULL,NULL,4,NULL,NULL,NULL,'2025-10-03 16:09:02',NULL),('user2','user2',5,'男','1759821101919.png','94edf28c6d6da38fd35d7ad53e485307f89fbeaf120485c8d17a43f323deee71',1,1,2,'2NtdXvuuQsA3yzptRxuEfYnP2Lxn7sH2j6jnsOHEYcjxV4p82XjtFEYQW3GZEzIk','429ca99f1adb243b70017eae55ed8967dd6077f53d030f83300583e269ca0fd5','v1','2025-10-07 15:07:58',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-23 16:36:43
