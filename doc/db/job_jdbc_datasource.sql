/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50725 (5.7.25-log)
 Source Host           : localhost:3306
 Source Schema         : crap

 Target Server Type    : MySQL
 Target Server Version : 50725 (5.7.25-log)
 File Encoding         : 65001

 Date: 24/11/2022 10:36:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for job_jdbc_datasource
-- ----------------------------
DROP TABLE IF EXISTS `job_jdbc_datasource`;
CREATE TABLE `job_jdbc_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `datasource_name` varchar(200) NOT NULL COMMENT '数据源名称',
  `datasource` varchar(45) NOT NULL COMMENT '数据源',
  `datasource_group` varchar(200) DEFAULT 'Default' COMMENT '数据源分组',
  `database_name` varchar(45) DEFAULT NULL COMMENT '数据库名',
  `jdbc_username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `jdbc_password` varchar(200) DEFAULT NULL COMMENT '密码',
  `jdbc_url` varchar(500) NOT NULL COMMENT 'jdbc url',
  `jdbc_driver_class` varchar(200) DEFAULT NULL COMMENT 'jdbc驱动类',
  `zk_adress` varchar(200) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'enum 状态. 0:禁用;1:启用;',
  `comments` varchar(1000) DEFAULT NULL COMMENT '备注',
  `gid` int(11) NOT NULL DEFAULT '0' COMMENT 'System 组 ID',
  `enterprise_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'System 企业 ID',
  `create_uid` bigint(20) NOT NULL DEFAULT '0' COMMENT 'System 创建人',
  `create_uname` varchar(255) NOT NULL DEFAULT '' COMMENT 'System 创建人',
  `create_time` datetime NOT NULL COMMENT 'System 创建时间',
  `update_uid` bigint(20) NOT NULL DEFAULT '0' COMMENT 'System 更新人',
  `update_uname` varchar(255) NOT NULL DEFAULT '' COMMENT 'System 更新人',
  `update_time` datetime NOT NULL COMMENT 'System 更新时间',
  `delete_flag` datetime DEFAULT NULL COMMENT 'System 删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='jdbc数据源配置';

-- ----------------------------
-- Records of job_jdbc_datasource
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
