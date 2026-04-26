-- 更新skin_test表结构
-- 将test_date字段类型从DATE改为VARCHAR(255)，用于存储图片路径
-- 添加score字段，用于存储测试总分

ALTER TABLE `skin_test` 
ADD COLUMN `score` INT DEFAULT NULL COMMENT '测试总分',
MODIFY COLUMN `test_date` VARCHAR(255) DEFAULT NULL COMMENT '测试图片路径';

-- 检查修改后的表结构
DESCRIBE `skin_test`;
