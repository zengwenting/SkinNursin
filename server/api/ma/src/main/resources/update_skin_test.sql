-- 修改skin_test表，添加score字段
ALTER TABLE `skin_test` ADD COLUMN `score` INT(11) DEFAULT NULL COMMENT '测试总分';

-- 修改test_date字段，用于存储图片路径
ALTER TABLE `skin_test` CHANGE COLUMN `test_date` `test_date` VARCHAR(255) DEFAULT NULL COMMENT '测试日期或图片路径';
