-- 1. 修改skin_test表，添加score字段，修改test_date字段
ALTER TABLE `skin_test` 
ADD COLUMN `score` INT DEFAULT NULL COMMENT '测试总分',
MODIFY COLUMN `test_date` VARCHAR(255) DEFAULT NULL COMMENT '测试图片路径';

-- 2. 添加微信小程序配置项到sys_config表
INSERT IGNORE INTO `sys_config` (`config_key`, `config_value`, `remark`) VALUES
('wx_app_id', '', '微信小程序AppID'),
('wx_app_secret', '', '微信小程序AppSecret');
