-- App user collected profile for admin user management.
-- Run this once on the RuoYi database before using /system/appUser.

create table if not exists `tcm_app_user_profile` (
  `id` bigint(20) not null auto_increment comment '主键',
  `user_id` bigint(20) not null comment 'sys_user.user_id',
  `gender` varchar(20) default '' comment '采集性别',
  `age` int default null comment '采集年龄',
  `voice_file` varchar(500) default '' comment '声音文件路径',
  `tongue_image` varchar(500) default '' comment '舌苔图片路径',
  `face_image` varchar(500) default '' comment '面部图片路径',
  `remark` varchar(500) default '' comment '备注',
  `create_time` datetime default null comment '创建时间',
  `update_time` datetime default null comment '更新时间',
  primary key (`id`),
  unique key `uk_tcm_app_user_profile_user` (`user_id`),
  key `idx_tcm_app_user_profile_update_time` (`update_time`)
) engine=innodb default charset=utf8mb4 comment='App用户采集资料表';
