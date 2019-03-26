/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50511
Source Host           : localhost:3306
Source Database       : onebox

Target Server Type    : MYSQL
Target Server Version : 50511
File Encoding         : 65001

Date: 2019-03-24 07:02:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `fileshare`
-- ----------------------------
DROP TABLE IF EXISTS `fileshare`;
CREATE TABLE `fileshare` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `owner` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `filename` varchar(1000) DEFAULT NULL,
  `filetype` varchar(200) DEFAULT NULL,
  `filepath` varchar(1000) DEFAULT NULL,
  `sharedate` varchar(1000) DEFAULT NULL,
  `downloadtimes` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fileshare
-- ----------------------------
INSERT INTO `fileshare` VALUES ('a55a2710bdd845c4bb2da886173d31be', 'user', '27b0', 'Android-Login.png', 'png', '/Uploads/Android-Login.png', '1553307415020', '0');

-- ----------------------------
-- Table structure for `userlist`
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
INSERT INTO `userlist` VALUES ('user', '123456', 'C:\\Users\\20875426\\7ccsmgpr_project\\DesktopClient\\onebox\\target\\onebox\\file\\user');
INSERT INTO `userlist` VALUES ('user1', '123456', 'C:\\Users\\20875426\\7ccsmgpr_project\\DesktopClient\\onebox\\target\\onebox\\file\\user1');
INSERT INTO `userlist` VALUES ('user2', '123456', 'C:\\Users\\20875426\\onebox\\target\\onebox\\file\\user2');
