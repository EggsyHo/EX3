/*
 Navicat Premium Data Transfer

 Source Server         : Software
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : localhost:3306
 Source Schema         : experiment

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 25/03/2022 11:17:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for journal
-- ----------------------------
DROP TABLE IF EXISTS `journal`;
CREATE TABLE `journal`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `operation` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `command` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 582 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for knapsack
-- ----------------------------
DROP TABLE IF EXISTS `knapsack`;
CREATE TABLE `knapsack`  (
  `m` int(11) NULL DEFAULT NULL,
  `n` int(11) NULL DEFAULT NULL,
  `weight` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `value` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `filename` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
