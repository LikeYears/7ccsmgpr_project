/*
Navicat MySQL Data Transfer

Source Server         : 111
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : onebox

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2019-02-26 20:17:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('12345', '12345');

-- ----------------------------
-- Table structure for upload
-- ----------------------------
DROP TABLE IF EXISTS `upload`;
CREATE TABLE `upload` (
  `id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `author` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `ip_add` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of upload
-- ----------------------------
INSERT INTO `upload` VALUES ('493884', '1321', '123213', '2019-01-27 01:26:33', '/20171102\\1D9670801B48A24FAE01C527F625CAF20937AF7A\\123213\\1.txt', '0:0:0:0:0:0:0:1', '1.txt');
INSERT INTO `upload` VALUES ('543220', '123', '1232', '2019-01-27 01:52:49', '/20171102\\1D9670801B48A24FAE01C527F625CAF20937AF7A\\1232\\R.exe', '0:0:0:0:0:0:0:1', 'R.exe');
INSERT INTO `upload` VALUES ('532897', '123', '12321421', '2019-01-27 01:56:30', '/20171102\\1D9670801B48A24FAE01C527F625CAF20937AF7A\\12321421\\Kalimba.mp3', '0:0:0:0:0:0:0:1', 'Kalimba.mp3');

-- ----------------------------
-- Table structure for userlist
-- ----------------------------
DROP TABLE IF EXISTS `userlist`;
CREATE TABLE `userlist` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `directory` varchar(255) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userlist
-- ----------------------------
INSERT INTO `userlist` VALUES ('admin', '123456', '/admin');
INSERT INTO `userlist` VALUES ('test', '123456', '/test');
INSERT INTO `userlist` VALUES ('user2', '123456', 'C:\\Users\\Zhihao\\Desktop\\DesktopClient\\onebox\\target\\onebox\\file\\user2');
