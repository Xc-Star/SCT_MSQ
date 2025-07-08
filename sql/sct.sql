/*
 Navicat Premium Dump SQL

 Source Server         : MySQLServer
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : sct

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 02/07/2025 21:25:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '123456' COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (0, 'Super Admin', '$2a$10$D9h9fYn6wcRTojbdTiDCl.LPD0uig6XNhrG/tUh7owtCA2XHE0kpi', NULL, '2025-06-08 03:28:42', '2025-07-02 21:18:30', '2025-06-13 00:11:18', NULL, b'0');
-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参数名',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参数键',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参数值',
  `type` int NULL DEFAULT NULL COMMENT '类型 1-文本 2-图片 3-开关',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, '服务器Logo', 'logo', '/profile/topic_image/2025/07/02/橙子_20250702143701A001.png', 2, NULL, '2025-07-02 14:37:02', 'test', b'0');
INSERT INTO `config` VALUES (2, '服务器名字', 'server_name', 'Sky City Server', 1, NULL, '2025-07-02 01:32:41', 'test', b'0');
INSERT INTO `config` VALUES (3, '服务器名字缩写', 'server_short_name', 'SCT', 1, NULL, '2025-07-02 14:36:39', 'test', b'0');
INSERT INTO `config` VALUES (4, '主页标题', 'main_title', '欢迎来到SCT的服务器', 1, NULL, '2025-07-02 01:32:51', 'test', b'0');
INSERT INTO `config` VALUES (5, '主页描述', 'main_description', '我们是一个生电服务器，请加群：767866296', 1, NULL, '2025-07-02 01:33:30', 'test', b'0');
INSERT INTO `config` VALUES (6, '主页背景', 'main_background', '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', 2, NULL, '2025-07-02 01:46:43', 'test', b'0');
INSERT INTO `config` VALUES (7, '问卷背景', 'msq_background', '/profile/upload/2025/07/02/108894119_p0_20250702001042A006.jpg', 2, NULL, '2025-07-02 00:10:45', 'test', b'0');
INSERT INTO `config` VALUES (8, '主页背景-手机', 'phone_main_background', '/profile/upload/2025/07/02/20250322_060824_20250702001001A005.jpg', 2, NULL, '2025-07-02 00:10:03', 'test', b'0');
INSERT INTO `config` VALUES (9, '问卷背景-手机', 'phone_msq_background', '/profile/upload/2025/07/02/20230918_210805_20250702000908A004.jpg', 2, NULL, '2025-07-02 00:09:10', 'test', b'0');
INSERT INTO `config` VALUES (10, '是否开启备货列表工具', 'stock_list_tool', '1', 3, NULL, '2025-07-02 01:31:31', 'test', b'0');
INSERT INTO `config` VALUES (11, '是否开启红石问卷', 'redstone_msq', '1', 3, NULL, '2025-07-02 01:28:55', 'test', b'0');
INSERT INTO `config` VALUES (12, '是否开启建筑问卷', 'architectural_msq', '1', 3, NULL, '2025-07-02 01:28:32', 'test', b'0');
INSERT INTO `config` VALUES (13, '是否开启后勤问卷', 'logistics_msq', '1', 3, NULL, '2025-07-02 01:28:32', 'test', b'0');
INSERT INTO `config` VALUES (14, '是否开启其他问卷', 'other_msq', '1', 3, NULL, '2025-07-02 01:28:29', 'test', b'0');
-- ----------------------------
-- Table structure for msq
-- ----------------------------
DROP TABLE IF EXISTS `msq`;
CREATE TABLE `msq`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问卷id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '问卷名字',
  `description` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '问卷描述',
  `type` tinyint NOT NULL COMMENT '类型 1-红石 2-建筑 3-后勤 4-其他',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of msq
-- ----------------------------
INSERT INTO `msq` VALUES (1, '红石审核问卷测试', '红石审核问卷测试，红石审核问卷测试，红石审核问卷测试。', 1, 1, '2025-06-09 02:11:00', '2025-06-30 10:00:53', 'test', b'0');
INSERT INTO `msq` VALUES (2, '建筑审核问卷测试', '建筑审核问卷测试，建筑审核问卷测试，建筑审核问卷测试，建筑审核问卷测试，建筑审核问卷测试，建筑审核问卷测试', 2, 1, '2025-06-09 02:12:17', '2025-06-30 10:25:13', 'test', b'0');
INSERT INTO `msq` VALUES (3, '后勤问卷测试', '后勤问卷测试，后勤问卷测试，后勤问卷测试', 3, 1, '2025-06-09 02:34:00', '2025-06-11 13:45:37', 'test', b'0');
INSERT INTO `msq` VALUES (4, '其他问卷测试', '其他问卷测试其他问卷测试其他问卷测试其他问卷测试其他问卷测试其他问卷测试其他问卷测试其他问卷测试', 4, 1, '2025-06-11 02:06:15', '2025-06-11 13:45:41', 'test', b'0');
INSERT INTO `msq` VALUES (5, '测试', '1', 4, 1, '2025-06-26 03:43:56', '2025-06-26 03:44:05', NULL, b'1');

-- ----------------------------
-- Table structure for msq_result
-- ----------------------------
DROP TABLE IF EXISTS `msq_result`;
CREATE TABLE `msq_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `msq_id` bigint NULL DEFAULT NULL COMMENT '问卷id',
  `msq_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问卷名字',
  `type` int NULL DEFAULT NULL COMMENT '类型 1-红石 2-建筑 3-后勤 4-其他',
  `respondent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回答者',
  `respondent_contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回答者联系方式',
  `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'uuid',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '问卷状态 1-未审核 2-已通过 3-未通过 4-已移出',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人id',
  `reviewer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `remover_id` bigint NULL DEFAULT NULL COMMENT '移出人id',
  `remover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '移出人',
  `remove_time` datetime NULL DEFAULT NULL COMMENT '移出时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目id',
  `msq_id` bigint NOT NULL COMMENT '问卷id',
  `msq_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '问卷名字',
  `no` int NOT NULL COMMENT '题目排序编号',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '题目类型',
  `topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '题目',
  `image_count` int NULL DEFAULT 0 COMMENT '图片数量',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选项',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topic
-- ----------------------------
INSERT INTO `topic` VALUES (-2, 0, '', 0, 'input', '请输入你的正版ID', 0, '[]', '2025-06-02 17:57:00', NULL, b'0');
INSERT INTO `topic` VALUES (-1, 0, '', 0, 'input', '请输入你的联系方式', 0, '[]', '2025-06-02 17:57:20', NULL, b'0');

-- ----------------------------
-- Table structure for topic_image
-- ----------------------------
DROP TABLE IF EXISTS `topic_image`;
CREATE TABLE `topic_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片id',
  `topic_id` bigint NOT NULL COMMENT '题目id',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '图片url',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `topic_id`(`topic_id` ASC) USING BTREE,
  CONSTRAINT `topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目图片' ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for topic_result
-- ----------------------------
DROP TABLE IF EXISTS `topic_result`;
CREATE TABLE `topic_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回答id',
  `msq_result_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '问卷名字',
  `topic_id` bigint NOT NULL COMMENT '题目id',
  `topic_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目回答',
  `topic_results` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '多选回答',
  `files` varchar(511) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问卷回答' ROW_FORMAT = Dynamic;
