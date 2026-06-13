-- Development app user for local H5/app testing.
-- Account: 13800138000
-- Password: 123456
--
-- Run this after importing database/sql/tcm_vsl.sql.

USE `tcm_vsl`;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Make local development login/register easier.
UPDATE `sys_config`
SET `config_value` = 'false'
WHERE `config_key` = 'sys.account.captchaEnabled';

UPDATE `sys_config`
SET `config_value` = 'true'
WHERE `config_key` = 'sys.account.registerUser';

SET @app_user_id = 1000;
SET @app_username = _utf8mb4'13800138000' COLLATE utf8mb4_unicode_ci;
SET @app_password = _utf8mb4'$2a$10$ISXLEuC.uLyja3T4TOZkdeCuf4iS4l/4l0W6c947SWEYmml2ySYOa' COLLATE utf8mb4_unicode_ci;

INSERT INTO `sys_user` (
  `user_id`,
  `dept_id`,
  `user_name`,
  `nick_name`,
  `user_type`,
  `email`,
  `phonenumber`,
  `sex`,
  `avatar`,
  `password`,
  `status`,
  `del_flag`,
  `login_ip`,
  `login_date`,
  `create_by`,
  `create_time`,
  `update_by`,
  `update_time`,
  `remark`
)
SELECT
  @app_user_id,
  100,
  @app_username,
  'App Test User',
  '00',
  '',
  @app_username,
  '0',
  '',
  @app_password,
  '0',
  '0',
  '127.0.0.1',
  NOW(),
  'dev-sql',
  NOW(),
  '',
  NOW(),
  'App dev test account'
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_user`
  WHERE `user_id` = @app_user_id
     OR `user_name` = @app_username
     OR `phonenumber` = @app_username
);

UPDATE `sys_user`
SET
  `dept_id` = 100,
  `user_name` = @app_username,
  `nick_name` = 'App Test User',
  `phonenumber` = @app_username,
  `password` = @app_password,
  `status` = '0',
  `del_flag` = '0',
  `update_by` = 'dev-sql',
  `update_time` = NOW(),
  `remark` = 'App dev test account'
WHERE `user_id` = @app_user_id
   OR `user_name` = @app_username
   OR `phonenumber` = @app_username;

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`)
SELECT `user_id`, 2
FROM `sys_user`
WHERE `user_name` = @app_username;
