/*
 Navicat Premium Data Transfer

 Source Server         : oms-dev
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : 192.168.201.211:3306
 Source Schema         : oms

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 15/10/2019 10:16:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for yks_task
-- ----------------------------
DROP TABLE IF EXISTS `yks_task`;
CREATE TABLE `yks_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) unsigned DEFAULT NULL,
  `task_title` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_desc` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_group` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trigger_start_time` datetime(3) DEFAULT NULL,
  `cron_expression` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_enabled` tinyint(3) unsigned DEFAULT NULL,
  `bean_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bean_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bean_method_param` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creator` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
  `created_time` datetime(3) NOT NULL COMMENT '创建时间',
  `modifier` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改人',
  `modified_time` datetime(3) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `uk_task_id` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务';

SET FOREIGN_KEY_CHECKS = 1;
