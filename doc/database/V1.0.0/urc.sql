/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2018/6/11 16:43:16                           */
/*==============================================================*/


drop table if exists system_parameter;

drop table if exists urc_auth_way;

drop table if exists urc_data_rule;

drop table if exists urc_data_rule_filed;

drop table if exists urc_data_rule_obj;

drop table if exists urc_data_rule_sys;

drop table if exists urc_data_rule_templ;

drop table if exists urc_entity;

drop table if exists urc_expression;

drop table if exists urc_field;

drop table if exists urc_operation_log;

drop table if exists urc_organization;

drop table if exists urc_permission;

drop table if exists urc_person;

drop table if exists urc_person_org;

drop table if exists urc_role;

drop table if exists urc_role_permission;

drop table if exists urc_sql;

drop table if exists urc_user;

drop table if exists urc_user_permission_cache;

drop table if exists urc_user_role;

/*==============================================================*/
/* Table: system_parameter                                      */
/*==============================================================*/
create table system_parameter
(
   id                   bigint not null auto_increment comment '主键',
   parameter_name       varchar(64) not null default '' comment '参数名称',
   parameter_value      varchar(1024) not null default '' comment '参数值',
   is_delete             tinyint unsigned not null default '1' comment '物理标识, 1:正常;0: 删除',
   remark               varchar(256) not null comment '备注',
   create_by            varchar(100) default '' comment '创建人',
   create_time          datetime(3) comment '创建时间',
   modified_by          varchar(100) default '' comment '更新人',
   modified_time        datetime(3) comment '更新时间',
   primary key (id)
);

alter table system_parameter comment '系统参数配置表';

/*==============================================================*/
/* Table: urc_auth_way                                          */
/*==============================================================*/
create table urc_auth_way
(
   id                   bigint not null auto_increment comment '主键',
   auth_way_id          bigint comment 'auth_way_id',
   sys_key              char(3) comment 'sys_key',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_auth_way comment '授权方式定义';

/*==============================================================*/
/* Table: urc_data_rule                                         */
/*==============================================================*/
create table urc_data_rule
(
   id                   bigint not null auto_increment comment '主键',
   data_rule_id         bigint comment '数据权限id',
   user_name            varchar(50) default '' comment '域账号',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_data_rule comment '用户-数据权限关系表';

/*==============================================================*/
/* Table: urc_data_rule_filed                                   */
/*==============================================================*/
create table urc_data_rule_filed
(
   id                   bigint not null auto_increment comment '主键',
   data_rule_obj_id     bigint comment 'FK: urc_data_rule_obj.id',
   field_code           varchar(32) default '' comment 'field_code',
   entity_code          varchar(32) default '' comment 'entity_code',
   clause_type          tinyint unsigned comment '取值:select/where',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_data_rule_filed comment '配置字段用于select OR where';

/*==============================================================*/
/* Table: urc_data_rule_obj                                     */
/*==============================================================*/
create table urc_data_rule_obj
(
   id                   bigint not null auto_increment comment '主键',
   data_rule_obj_id     bigint comment 'data_rule_obj_id',
   auth_way_id          bigint comment 'FK:urc_auth_way.id',
   entity_code          varchar(32) default '' comment 'entity_code',
   obj_name             varchar(100) default '' comment 'obj_name',
   sort_idx             tinyint comment 'sort_idx',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_data_rule_obj comment '可授权的数据权限对象';

/*==============================================================*/
/* Table: urc_data_rule_sys                                     */
/*==============================================================*/
create table urc_data_rule_sys
(
   id                   bigint not null comment '主键',
   data_rule_sys_id     bigint comment '业务主键',
   data_rule_id         bigint comment 'FK:data_rule_id',
   sys_key              char(3) comment 'sys_key',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_data_rule_sys comment '数据权限sys';

/*==============================================================*/
/* Table: urc_data_rule_templ                                   */
/*==============================================================*/
create table urc_data_rule_templ
(
   id                   bigint not null auto_increment comment '主键',
   templ_id             bigint comment 'templ_id',
   templ_name           varchar(100) default '' comment 'templ_name',
   user_name            varchar(100) default '' comment 'user_name',
   remark               varchar(2000) default '' comment '备注',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_data_rule_templ comment '数据权限模板';

/*==============================================================*/
/* Table: urc_entity                                            */
/*==============================================================*/
create table urc_entity
(
   id                   bigint not null auto_increment comment '主键',
   entity_code          varchar(32) comment '实体code',
   entity_name          varchar(50) default '' comment '名称',
   remark               varchar(250) default '' comment '备注',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_entity comment '实体定义表';

/*==============================================================*/
/* Table: urc_expression                                        */
/*==============================================================*/
create table urc_expression
(
   id                   bigint not null comment 'PK',
   sql_id               bigint comment 'FK:urc_sql.id',
   field_code           varchar(32) default '' comment '字段code',
   entity_code          varchar(32) default '' comment '实体code',
   oper                 varchar(10) default '' comment '操作符',
   oper_values          text comment '值',
   parent_expression_id bigint comment 'parent_expression_id',
   is_and               tinyint unsigned comment '子级条件关系符:0-or,1-and',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_expression comment '支持条件表达式任意and/or组合';

/*==============================================================*/
/* Table: urc_field                                             */
/*==============================================================*/
create table urc_field
(
   id                   bigint not null auto_increment comment '主键',
   field_code           varchar(32) default '' comment '字段code',
   field_name           varchar(50) default '' comment '名称',
   entity_code          varchar(32) comment 'FK:urc_entity.entity_code',
   data_type            varchar(50) default '' comment '字段类型',
   maxlength            int comment '最大长度',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_field comment '字段定义表';

/*==============================================================*/
/* Table: urc_operation_log                                     */
/*==============================================================*/
create table urc_operation_log
(
   id                   bigint not null auto_increment comment 'id',
   target_table         varchar(40) default '' comment '目标类',
   target_identify      varchar(50) default '' comment '目标定义',
   target_name          varchar(50) default '' comment '目标名称',
   target_result        varchar(10) default '' comment '结果',
   description          varchar(100) default '' comment '描述',
   operator_time        datetime(3) comment '操作时间',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_operation_log comment '操作日志表';

/*==============================================================*/
/* Table: urc_organization                                      */
/*==============================================================*/
create table urc_organization
(
   id                   bigint not null auto_increment comment '主键',
   ding_org_id          varchar(15) default '' comment '钉钉组织id',
   org_name             varchar(50) default '' comment '部门名称',
   full_id_path         varchar(250) default '' comment '根部门到当前部门的id，分隔符为/',
   full_name_path       varchar(500) default '' comment '根部门到当前部门的名称 ，分隔符为/',
   org_level            tinyint unsigned comment '层级，根为1',
   parent_ding_org_id   varchar(15) default '' comment '父部门',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_organization comment '钉钉部门表';

/*==============================================================*/
/* Index: index_org                                             */
/*==============================================================*/
create index index_org on urc_organization
(
   ding_org_id
);

/*==============================================================*/
/* Table: urc_permission                                        */
/*==============================================================*/
create table urc_permission
(
   id                   bigint not null auto_increment comment '主键',
   sys_name             char(255) comment '系统名称',
   sys_key              char(3) comment '系统key',
   sys_context          text comment '功能权限json',
   create_by            varchar(100) default '' comment '创建人',
   create_time          datetime(3) comment '创建时间',
   modified_by          varchar(100) default '' comment '更新人',
   modified_time        datetime(3) comment '更新时间',
   primary key (id)
);

alter table urc_permission comment '功能权限表';

/*==============================================================*/
/* Table: urc_person                                            */
/*==============================================================*/
create table urc_person
(
   id                   bigint not null auto_increment comment '主键',
   person_name          varchar(255) comment '钉钉姓名',
   gender               tinyint unsigned comment '取值：0-女,1-男,2-未知',
   phone_num            varchar(11) default '' comment '手机号',
   ding_user_id         varchar(100) default '' comment '钉钉号在当前企业中的userId',
   ding_id              varchar(50) default '' comment '钉钉号Id',
   ding_unionid         varchar(100) default '' comment '和openid值相同',
   birthday             date comment '钉钉上的生日',
   join_date            date comment '钉钉上的入职日期',
   leave_date           date comment '离职日期',
   job_number           varchar(20) default '' comment '工号',
   email                varchar(150) default '' comment '邮箱',
   position             varchar(100) default '' comment '职位',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_person comment '钉钉人员表';

/*==============================================================*/
/* Table: urc_person_org                                        */
/*==============================================================*/
create table urc_person_org
(
   id                   bigint not null auto_increment comment '主键',
   ding_org_id          varchar(15) default '' comment 'FK:urc_organization.ding_org_id',
   ding_user_id         varchar(100) default '' comment 'FK:urc_person.ding_user_id',
   create_by            varchar(100) default '' comment '创建人',
   create_time          datetime(3) comment '创建时间',
   modified_by          varchar(100) default '' comment '更新人',
   modified_time        datetime(3) comment '更新时间',
   primary key (id)
);

alter table urc_person_org comment '人员-部门关系表';

/*==============================================================*/
/* Table: urc_role                                              */
/*==============================================================*/
create table urc_role
(
   id                   bigint not null auto_increment comment '主键',
   role_id              bigint comment '角色Id',
   role_name            varchar(50) default '' comment '角色名称',
   is_active            tinyint unsigned comment '是否启用',
   is_authorizable      tinyint unsigned comment '是否管理员角色',
   is_forever           tinyint unsigned comment '是否永久有效',
   effective_time       datetime(3) comment '有效开始时间',
   expire_time          datetime(3) comment '有效结束时间',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   remark               varchar(100) default '' comment '备注',
   primary key (id)
);

alter table urc_role comment '角色表';

/*==============================================================*/
/* Table: urc_role_permission                                   */
/*==============================================================*/
create table urc_role_permission
(
   id                   bigint not null auto_increment comment '主键',
   role_id              bigint comment '角色id',
   sys_key              char(3) comment '业务系统key',
   selected_context     text comment '功能权限json',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_role_permission comment '角色-功能权限关系表';

/*==============================================================*/
/* Table: urc_sql                                               */
/*==============================================================*/
create table urc_sql
(
   id                   bigint not null auto_increment comment '主键',
   sql_id               bigint comment 'sql_id',
   data_rule_sys_id     bigint comment 'FK:data_rule_sys_id',
   entity_code          varchar(32) default '' comment 'entity_code',
   hidden_fields        varchar(2000) comment '隐藏的fieldCodeArr',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(32) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(32) default '' comment '更新人',
   primary key (id)
);

alter table urc_sql comment 'urc_sql';

/*==============================================================*/
/* Table: urc_user                                              */
/*==============================================================*/
create table urc_user
(
   id                   bigint not null auto_increment comment '主键',
   user_name            varchar(100) default '' comment '域账号',
   is_active            tinyint unsigned comment '是否启用',
   active_time          datetime(3) comment '启用时间',
   ding_user_id         varchar(100) default '' comment 'FK:urc_person.ding_user_id',
   create_by            varchar(100) default '' comment '创建人',
   create_time          datetime(3) comment '创建时间',
   modified_by          varchar(100) default '' comment '更新人',
   modified_time        datetime(3) comment '更新时间',
   primary key (id)
);

alter table urc_user comment '用户域账号表';

/*==============================================================*/
/* Table: urc_user_permission_cache                             */
/*==============================================================*/
create table urc_user_permission_cache
(
   id                   bigint not null auto_increment comment '主键',
   user_name            varchar(100) default '' comment '域账号',
   sys_key              char(3) comment 'sys_key',
   permission_version   char(32) comment '版本号',
   user_context         text comment '功能权限json',
   create_time          datetime(3) comment '创建时间',
   create_by            varchar(100) default '' comment '创建人',
   modified_time        datetime(3) comment '更新时间',
   modified_by          varchar(100) default '' comment '更新人',
   primary key (id)
);

alter table urc_user_permission_cache comment '功能权限版本缓存表';

/*==============================================================*/
/* Table: urc_user_role                                         */
/*==============================================================*/
create table urc_user_role
(
   id                   bigint not null auto_increment comment '主键',
   role_id              bigint comment '角色id',
   user_name            varchar(100) default '' comment '域账号',
   create_by            varchar(100) default '' comment '创建人',
   create_time          datetime(3) comment '创建时间',
   modified_by          varchar(100) default '' comment '更新人',
   modified_time        datetime(3) comment '更新时间',
   primary key (id)
);

alter table urc_user_role comment '用户-角色关系表';

