/*
 Navicat MySQL Data Transfer

 Source Server         : MySql-Local
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : onebox

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 13/03/2019 22:38:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_file
-- ----------------------------
DROP TABLE IF EXISTS `tbl_file`;
CREATE TABLE `tbl_file` (
  `id` int(64) NOT NULL COMMENT 'ID',
  `uid` int(64) DEFAULT NULL COMMENT '用户ID',
  `fid` int(64) DEFAULT NULL COMMENT '文件夹id【Folder】',
  `name` varchar(64) DEFAULT NULL COMMENT '文件名称',
  `path` varchar(255) DEFAULT NULL COMMENT '文件全路径',
  `version` varchar(255) DEFAULT NULL COMMENT '文件版本',
  `uuid` int(11) DEFAULT NULL COMMENT '修改人id',
  `unum` int(11) DEFAULT NULL COMMENT '历史更新数',
  `utime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `ctime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件管理表';

SET FOREIGN_KEY_CHECKS = 1;
