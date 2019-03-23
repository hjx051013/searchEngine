/*
 Navicat Premium Data Transfer

 Source Server         : aliyun
 Source Server Type    : MySQL
 Source Server Version : 50723
 Source Host           : 47.100.50.2:3306
 Source Schema         : ruanjian1

 Target Server Type    : MySQL
 Target Server Version : 50723
 File Encoding         : 65001

 Date: 23/03/2019 17:05:42
*/

DROP database if EXISTS `ruanjian`;
CREATE database `ruanjian`;
USE `ruanjian`;

-- ----------------------------
-- Table structure for dict_tb1
-- ----------------------------
DROP TABLE IF EXISTS `dict_tb1`;
CREATE TABLE `dict_tb1` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `word` varchar(255) NOT NULL,
  `result` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_1` (`word`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60545 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for url_tb
-- ----------------------------
DROP TABLE IF EXISTS `url_tb`;
CREATE TABLE `url_tb` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `content` mediumtext,
  `pointing` text,
  `pr` float DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `text` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index2` (`url`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
