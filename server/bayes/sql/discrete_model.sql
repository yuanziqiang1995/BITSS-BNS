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

 Date: 03/12/2019 14:34:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for discrete_model
-- ----------------------------
DROP TABLE IF EXISTS `discrete_model`;
CREATE TABLE `discrete_model` (
  `model_id` varchar(50) NOT NULL,
  `model_name` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` text,
  `location` text,
  `user_id` varchar(50) NOT NULL,
  `del_flag` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`model_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of discrete_model
-- ----------------------------
BEGIN;
INSERT INTO `discrete_model` VALUES ('1', 'qwe', '2019-11-17 18:35:18', '123', 'adf', '1', 0);
INSERT INTO `discrete_model` VALUES ('123', 'test', '2019-12-03 14:29:02', '123123', 'models/StaticDiscreteNetwork-123', '123', 0);
INSERT INTO `discrete_model` VALUES ('2', 'sdf', '2019-11-17 18:35:50', '123', 'q', '1', 0);
INSERT INTO `discrete_model` VALUES ('3', 'asd', '2019-12-03 00:00:00', 'qwe', '345', '2', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
