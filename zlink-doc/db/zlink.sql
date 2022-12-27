/*
 Navicat Premium Data Transfer

 Source Server         : 超融合-MySQL
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : 192.168.52.154:3306
 Source Schema         : zlink

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 26/12/2022 11:26:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for job_datasource_type
-- ----------------------------
DROP TABLE IF EXISTS `job_datasource_type`;
CREATE TABLE `job_datasource_type`  (
                                        `database_type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源类型',
                                        `jdbc_driver_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'jdbc driver class',
                                        PRIMARY KEY (`database_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据源类型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of job_datasource_type
-- ----------------------------
INSERT INTO `job_datasource_type` VALUES ('mysql', 'com.mysql.cj.jdbc.Driver');
INSERT INTO `job_datasource_type` VALUES ('postgresql', 'org.postgresql.Driver');

-- ----------------------------
-- Table structure for job_flink_conf
-- ----------------------------
DROP TABLE IF EXISTS `job_flink_conf`;
CREATE TABLE `job_flink_conf`  (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                   `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '集群名字',
                                   `model` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'flink 集群模式',
                                   `ip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
                                   `port` int NULL DEFAULT NULL COMMENT '端口',
                                   `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'System 创建时间',
                                   `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'System 更新时间',
                                   PRIMARY KEY (`id`, `name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'flink 配置信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of job_flink_conf
-- ----------------------------
INSERT INTO `job_flink_conf` VALUES (3, '测试1', 'standalone', 'localhost', 8081, '2022-12-20 16:30:27', '2022-12-22 15:27:13');
INSERT INTO `job_flink_conf` VALUES (4, '测试2', 'standalone', 'localhost', 8081, '2022-12-20 16:31:36', '2022-12-22 15:27:14');

-- ----------------------------
-- Table structure for job_flink_model
-- ----------------------------
DROP TABLE IF EXISTS `job_flink_model`;
CREATE TABLE `job_flink_model`  (
                                    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `flink_model` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '集群模式',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'flink 集群模式' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of job_flink_model
-- ----------------------------
INSERT INTO `job_flink_model` VALUES (1, 'standalone');
INSERT INTO `job_flink_model` VALUES (2, 'yarn');
INSERT INTO `job_flink_model` VALUES (3, 'k8s');

-- ----------------------------
-- Table structure for job_jdbc_datasource
-- ----------------------------
DROP TABLE IF EXISTS `job_jdbc_datasource`;
CREATE TABLE `job_jdbc_datasource`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                        `database_type` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源类型',
                                        `database_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据源名称',
                                        `user_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                                        `password` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                                        `jdbc_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jdbc url',
                                        `jdbc_driver_class` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'jdbc驱动类',
                                        `comments` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                        `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'System 创建时间',
                                        `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'System 更新时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'jdbc数据源配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of job_jdbc_datasource
-- ----------------------------
INSERT INTO `job_jdbc_datasource` VALUES (9, 'postgresql', 'dubhe_pro', 'zmt_yunwei', 'ZMT*yu9n8wei%', 'jdbc:postgresql://gp-bp106208z48z67f0zo-master.gpdbmaster.rds.aliyuncs.com:5432/dubhe', 'org.postgresql.Driver', '这是一个备注', '2022-11-26 21:38:53', '2022-11-26 21:38:53');
INSERT INTO `job_jdbc_datasource` VALUES (10, 'mysql', 'edb-pro', 'dev_zhaoshuo', '$m!hQ!X&j%nZa#KnB', 'jdbc:mysql://rm-bp13s2nco94n09o32vo.mysql.rds.aliyuncs.com:3306/zmtdata_ordercenter', 'com.mysql.cj.jdbc.Driver', '这是一个备注', '2022-11-26 21:38:53', '2022-11-26 21:38:53');
INSERT INTO `job_jdbc_datasource` VALUES (12, 'mysql', 'app_pro', 'dev_zhaoshuo', '$m!hQ!X&j%nZa#KnB', 'jdbc:mysql://pro-myjk-slave-01.mysql.rds.aliyuncs.com:3306/myjk_content', 'com.mysql.cj.jdbc.Driver', '这是一个备注', '2022-11-26 21:38:53', '2022-11-26 21:38:53');
INSERT INTO `job_jdbc_datasource` VALUES (13, 'mysql', 'bighealth-pro', 'zs', 'aa', 'jdbc:mysql://rm-bp1de47f853t5bh16.mysql.rds.aliyuncs.com:3306/bighealth', 'com.mysql.cj.jdbc.Driver', '这是一个备注', '2022-11-26 21:38:54', '2022-11-26 21:38:54');
INSERT INTO `job_jdbc_datasource` VALUES (16, 'mysql', 'aaa', 'root', '123456', 'jdbc:mysql://192.168.52.154:3306/zlink', 'com.mysql.cj.jdbc.Driver', 'a', '2022-11-27 16:02:03', '2022-11-27 16:02:10');
INSERT INTO `job_jdbc_datasource` VALUES (18, 'mysql', 'bbb', 'root', '123456', 'jdbc:mysql://192.168.52.154:3306/zlink', 'com.mysql.cj.jdbc.Driver', '', '2022-11-27 16:32:46', '2022-11-27 16:32:46');
INSERT INTO `job_jdbc_datasource` VALUES (19, 'mysql', 'ccc', 'root', '123456', 'jdbc:mysql://192.168.52.154:3306/zlink', 'com.mysql.cj.jdbc.Driver', 'asdasdsa', '2022-11-27 16:33:27', '2022-11-27 16:33:27');
INSERT INTO `job_jdbc_datasource` VALUES (20, 'mysql', '小程序', 'dev_zhaoshuo', '$m!hQ!X&j%nZa#KnB', 'jdbc:mysql://pro-spark-shop-master-public.mysql.rds.aliyuncs.com:3306/shop_analysis', 'com.mysql.cj.jdbc.Driver', '', '2022-12-05 18:55:28', '2022-12-05 18:55:28');
INSERT INTO `job_jdbc_datasource` VALUES (21, 'mysql', '巨量', 'rds_admin', '1mSGYtKdLylI_2a4_1ghFI$J3WJ7', 'jdbc:mysql://106.120.188.210:3306/lianshan_rds_1740199848264712', 'com.mysql.cj.jdbc.Driver', '', '2022-12-13 16:44:57', '2022-12-13 16:44:57');
INSERT INTO `job_jdbc_datasource` VALUES (22, 'postgresql', 'gp_dev', 'gpadmin', 'yk3ysjtsws', 'jdbc:postgresql://8.136.151.87:2345/dubhe', 'org.postgresql.Driver', '', '2022-12-25 14:59:48', '2022-12-25 14:59:48');

-- ----------------------------
-- Table structure for job_sys_menus
-- ----------------------------
DROP TABLE IF EXISTS `job_sys_menus`;
CREATE TABLE `job_sys_menus`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `parent_id` int NULL DEFAULT NULL COMMENT '用户名',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                                  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                                  `sort` int NULL DEFAULT NULL,
                                  `role_id` int NULL DEFAULT 0 COMMENT '角色ID',
                                  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_sys_menus
-- ----------------------------
INSERT INTO `job_sys_menus` VALUES (1, 0, '配置中心', 'conf-center', 2, 0, '2022-11-26 19:00:44', '2022-12-20 13:56:00');
INSERT INTO `job_sys_menus` VALUES (2, 0, '首页', 'home', 1, 0, '2022-11-26 19:01:10', '2022-12-13 15:53:17');
INSERT INTO `job_sys_menus` VALUES (3, 0, '元数据中心', 'metadata', 3, 0, '2022-11-28 16:34:29', '2022-11-28 17:05:42');
INSERT INTO `job_sys_menus` VALUES (4, 0, 'cdc', 'cdc', 4, 0, '2022-12-06 14:56:44', '2022-12-06 14:56:44');
INSERT INTO `job_sys_menus` VALUES (5, 0, 'flink 任务列表', 'flinktask', 5, 0, '2022-12-12 11:32:40', '2022-12-12 11:32:40');
INSERT INTO `job_sys_menus` VALUES (6, 1, 'flink 配置中心', 'flinkconf', 2, 0, '2022-12-20 09:51:00', '2022-12-20 09:51:24');
INSERT INTO `job_sys_menus` VALUES (7, 1, '数据源中心', 'datasource', 1, 0, '2022-12-20 13:54:24', '2022-12-20 13:54:24');

-- ----------------------------
-- Table structure for job_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `job_sys_user`;
CREATE TABLE `job_sys_user`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
                                 `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
                                 `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
                                 `role_id` int NULL DEFAULT 0 COMMENT '角色ID',
                                 `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `delete_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效  0有效  1无效',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10008 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of job_sys_user
-- ----------------------------
INSERT INTO `job_sys_user` VALUES (10003, 'admin', '123456', '超级用户23', 1, '2017-10-30 11:52:38', '2022-11-26 17:53:02', 0);
INSERT INTO `job_sys_user` VALUES (10004, 'user', '123456', '莎士比亚', 2, '2017-10-30 16:13:02', '2022-11-26 17:53:02', 0);
INSERT INTO `job_sys_user` VALUES (10005, 'aaa', '123456', 'abba', 1, '2017-11-15 14:02:56', '2022-11-26 17:53:02', 0);
INSERT INTO `job_sys_user` VALUES (10007, 'test', '123456', '就看看列表', 3, '2017-11-22 16:29:41', '2022-11-26 17:53:03', 0);

SET FOREIGN_KEY_CHECKS = 1;
