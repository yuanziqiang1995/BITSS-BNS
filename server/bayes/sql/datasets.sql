/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : bayes

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 03/12/2019 20:07:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for datasets
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets` (
  `id` varchar(50) NOT NULL,
  `type` int(11) NOT NULL COMMENT '1:普通离散网络',
  `location` varchar(50) DEFAULT NULL,
  `del_flag` smallint(6) DEFAULT '0',
  `user_id` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of datasets
-- ----------------------------
BEGIN;
INSERT INTO `datasets` VALUES ('123', 1, 'logstash.md', 0, '1234', '2019-12-03 19:54:58');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
