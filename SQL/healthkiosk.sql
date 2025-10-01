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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
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
  `version` varchar(10) NOT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
  `type` enum('1','2') NOT NULL,
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '下载量',
  PRIMARY KEY (`version`,`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES ('1.0',NULL,'1','2025-09-14 17:14:10',1);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目号',
  `name` varchar(20) NOT NULL COMMENT '项目名称',
  `use` int(11) NOT NULL DEFAULT '1' COMMENT '0-disable,1-able',
  `description` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'tempor',1,'体温'),(2,'alcohol',1,'酒精'),(3,'ecg',1,'心电'),(4,'spo2',1,'血氧'),(5,'ppg',1,'光电容积脉搏波'),(6,'blood_sys',1,'收缩压'),(7,'blood_dia',1,'舒张压'),(8,'blood_hr',1,'心率'),(9,'tire',1,'疲劳程度');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','admin',2,'男','1757753904644.jpg','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',180,65,5,'ZntTALWJ2M2MpTnOBOtYJSm76KGWzfN3p5OrU+hdO5Q4mpL6W7DFmC73Av+2diMP','v1:429ca99f1adb243b70017eae55ed8967dd6077f53d030f83300583e269ca0fd5','v1','2025-09-20 14:08:59',NULL),('show1',NULL,NULL,NULL,NULL,'show1',NULL,NULL,0,NULL,NULL,NULL,'2025-09-23 16:08:58',NULL),('show2',NULL,NULL,NULL,NULL,'show1',NULL,NULL,0,NULL,NULL,NULL,'2025-09-24 16:09:00',NULL),('show3',NULL,NULL,NULL,NULL,'show1',NULL,NULL,1,NULL,NULL,NULL,'2025-09-23 16:09:01',NULL),('testAdd','testAdd',1,'男',NULL,'c0f0e0a7b26d1cdf4f3f3f1b210405135438f6f68a2145bc917d92076de8408a',NULL,1,1,NULL,NULL,NULL,'2025-09-20 17:37:09',NULL),('user','user',NULL,'男',NULL,'04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb',NULL,NULL,4,NULL,NULL,NULL,'2025-09-20 16:09:02',NULL),('user1','user1',NULL,NULL,NULL,'0a041b9462caa4a31bac3567e0b6e6fd9100787db2ab433d96f6d178cabfce90',NULL,NULL,3,NULL,NULL,NULL,'2025-09-12 16:09:03',NULL);
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

-- Dump completed on 2025-10-01 10:59:40
