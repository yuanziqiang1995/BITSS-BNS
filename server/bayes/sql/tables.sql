-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.19 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 bayes 的数据库结构
CREATE DATABASE IF NOT EXISTS `bayes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bayes`;

-- 导出  表 bayes.datasets 结构
CREATE TABLE IF NOT EXISTS `datasets` (
  `id` varchar(50) NOT NULL,
  `type` int NOT NULL COMMENT '1:普通离散网络',
  `location` varchar(300) DEFAULT NULL,
  `del_flag` smallint DEFAULT '0',
  `user_id` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `format` varchar(50) DEFAULT NULL,
  `description` mediumtext,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 数据导出被取消选择。

-- 导出  表 bayes.discrete_model 结构
CREATE TABLE IF NOT EXISTS `discrete_model` (
  `model_id` varchar(50) NOT NULL,
  `model_name` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` mediumtext,
  `location` mediumtext,
  `user_id` varchar(50) NOT NULL,
  `del_flag` tinyint(1) DEFAULT '0',
  `dataset_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`model_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 数据导出被取消选择。

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
