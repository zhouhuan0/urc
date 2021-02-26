ALTER TABLE `urcenter`.`urc_permission` 
ADD COLUMN `sys_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0:erp系统 1:FBA系统 ' AFTER `modified_time`,
ADD COLUMN `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用' AFTER `sys_type`,
ADD COLUMN `remark` varchar(2000) NULL COMMENT '备注' AFTER `status`;


CREATE TABLE `urcenter`.`urc_system_administrator`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_key` char(3) NULL COMMENT '系统key',
  `user_name` varchar(100) NULL DEFAULT '' COMMENT '账号',
  `type` tinyint(4) NULL COMMENT '管理员类型 1:功能管理员  2:数据管理员',
  `creator` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `modified_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_sys_key_user_name_type`(`sys_key`, `user_name`, `type`) USING BTREE,
  INDEX `idx_user_name`(`user_name`) USING BTREE,
  INDEX `idx_sys_key`(`sys_key`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='各系统管理员表';


ALTER TABLE `urcenter`.`urc_role` 
ADD COLUMN `role_type` tinyint(3) NOT NULL DEFAULT 1 COMMENT '类型 1:角色  2:岗位' AFTER `remark`,
ADD COLUMN `position_modified_time` datetime(3) NULL COMMENT '岗位更新时间' AFTER `role_type`,
ADD INDEX `idx_role_type`(`role_type`) USING BTREE;


CREATE TABLE `permit_item_position` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `permit_key` varchar(150) NOT NULL,
  `position_id` bigint(20) DEFAULT NULL COMMENT '岗位id',
  `creator` varchar(50) DEFAULT NULL,
  `created_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `modifier` varchar(50) DEFAULT NULL,
  `modified_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permit_key_position_id` (`permit_key`,`position_id`) USING BTREE,
  KEY `idx_modified_time` (`modified_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='岗位权限详情明细表';


CREATE TABLE `urcenter`.`urc_position_group`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NULL COMMENT '权限组id',
  `position_id` bigint(20) DEFAULT NULL COMMENT '岗位id',
  `group_name` varchar(500) NULL COMMENT '权限组名称',
  `is_delete` tinyint(3) NOT NULL DEFAULT 0,
  `creator` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `modified_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  INDEX `idx_group_name`(`group_name`) USING BTREE,
  INDEX `idx_create_by`(`creator`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='岗位权限组表';


CREATE TABLE `urcenter`.`urc_group_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NULL COMMENT '权限组id',
  `sys_key` char(3) DEFAULT NULL COMMENT '业务系统key',
  `selected_context` text COMMENT '功能权限json',
  `creator` varchar(50) DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `modified_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id_sys_key` (`group_id`,`sys_key`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='岗位权限组功能权限表';



#新加定时任务
INSERT INTO urcenter.yks_task (id,task_id, task_title, task_desc, task_group, task_name, trigger_start_time, cron_expression, is_enabled, bean_name, bean_method, bean_method_param, creator, created_time, modifier, modified_time) VALUES (120,120, '新员工授权', '新员工授权', 'taskScheduler', 'commonPermissionTask', now(), '0 0 12 * * ?', 1, 'commonPermissionTask', 'doTask', null, '', now(), '', now());
#配置数据
INSERT INTO urcenter.yks_prop_setting (prop_key, prop_value, remark, create_by, create_time, modified_by, modified_time) VALUES ('ROLE_NAME', '通用角色', '用户中心', 'wensheng', now(), 'wensheng', now());



ALTER TABLE `urcenter`.`urc_permission`
MODIFY COLUMN `sys_name` char(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子系统名称' AFTER `id`,
ADD COLUMN `sys_type_name` varchar(255) NULL COMMENT '系统类型名称' AFTER `sys_type`;

ALTER TABLE `urcenter`.`urc_user`
ADD COLUMN `avatar` varchar(255) NULL COMMENT '钉钉头像链接' AFTER `mobile`;
