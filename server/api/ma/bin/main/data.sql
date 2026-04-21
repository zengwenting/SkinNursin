INSERT INTO `user` (`id`, `nickname`, `avatar`, `account`, `password`, `age`, `gender`, `skin_type`, `skin_goal`, `bio`, `is_delete`)
SELECT 1, 'Qingrou', '/static/image/logo-icon_small.png', 'qingrou', '123456', 24, 'female', 'combination', 'stabilize', 'Turn skincare into a soft daily ritual.', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `id` = 1);

INSERT INTO `cosmetic_item` (`user_id`, `name`, `brand`, `category`, `image_url`, `production_date`, `expire_date`, `effect_tag`, `ingredient`, `use_period`, `note`, `is_delete`)
SELECT 1, 'Cloud Hydration Serum', 'SoftLab', 'skincare', '/static/image/logo-icon.png', '2025-01-01', '2028-01-01', 'Hydration Repair', 'Hyaluronic Acid,Ceramide', 'night', 'Best for calm night routine', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cosmetic_item` WHERE `user_id` = 1 AND `name` = 'Cloud Hydration Serum');

INSERT INTO `cosmetic_item` (`user_id`, `name`, `brand`, `category`, `image_url`, `production_date`, `expire_date`, `effect_tag`, `ingredient`, `use_period`, `note`, `is_delete`)
SELECT 1, 'Morning UV Milk', 'PureSkin', 'skincare', '/static/image/logo-icon_small.png', '2025-02-15', '2027-02-15', 'Light Sunscreen', 'Niacinamide,UV Filter', 'day', 'Great before commuting', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cosmetic_item` WHERE `user_id` = 1 AND `name` = 'Morning UV Milk');

INSERT INTO `cosmetic_item` (`user_id`, `name`, `brand`, `category`, `image_url`, `production_date`, `expire_date`, `effect_tag`, `ingredient`, `use_period`, `note`, `is_delete`)
SELECT 1, 'Soft Blush Palette', 'Bloomy', 'makeup', '/static/image/blank-avatar.png', '2025-03-01', '2028-03-01', 'Fresh Glow', 'Mineral Powder', 'day', 'Tap softly on cheeks', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `cosmetic_item` WHERE `user_id` = 1 AND `name` = 'Soft Blush Palette');
