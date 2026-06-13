-- Pulse records collected from the mobile app or wearable devices.
-- Run this once on the RuoYi database before using /app/pulse or /system/pulse.

create table if not exists `tcm_pulse_record` (
  `id` bigint(20) not null auto_increment comment 'primary key',
  `user_id` bigint(20) not null comment 'sys_user.user_id',
  `device_id` varchar(100) default '' comment 'wearable device id',
  `pulse_rate` int not null comment 'pulse rate, bpm',
  `signal_quality` int default null comment 'signal quality, 0-100',
  `raw_data` text comment 'raw pulse data or json payload',
  `sampled_at` datetime not null comment 'sample time',
  `received_at` datetime not null comment 'backend receive time',
  `source` varchar(30) default 'app' comment 'data source',
  `remark` varchar(500) default '' comment 'remark',
  primary key (`id`),
  key `idx_tcm_pulse_user_time` (`user_id`, `sampled_at`),
  key `idx_tcm_pulse_device_time` (`device_id`, `sampled_at`)
) engine=innodb default charset=utf8mb4 comment='TCM pulse record';
