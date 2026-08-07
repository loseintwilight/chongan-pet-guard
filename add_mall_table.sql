/*
商品购买功能 - 完整数据库整合脚本（合并版）
整合文件：add_tables_mall.sql + add_tables.sql + insert_data.sql + mall_database_complete.sql
用途：组员可直接运行此文件完成数据库配置
包含：字段补充 + 新建表 + 分类数据 + 商品数据
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 一、安全添加字段（使用存储过程）
-- =============================================

DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;
DELIMITER $$
CREATE PROCEDURE `add_column_if_not_exists`(
    IN table_name_param VARCHAR(100),
    IN column_name_param VARCHAR(100),
    IN column_definition VARCHAR(500)
)
BEGIN
    DECLARE column_exists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO column_exists 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = table_name_param 
    AND COLUMN_NAME = column_name_param;
    
    IF column_exists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', table_name_param, '` ADD COLUMN `', column_name_param, '` ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- 订单表字段补充
CALL `add_column_if_not_exists`('tb_order', 'address_id', 'bigint NULL DEFAULT NULL COMMENT "收货地址id"');
CALL `add_column_if_not_exists`('tb_order', 'receiver_name', 'varchar(50) NULL DEFAULT NULL COMMENT "收货人姓名"');
CALL `add_column_if_not_exists`('tb_order', 'receiver_phone', 'varchar(20) NULL DEFAULT NULL COMMENT "收货人电话"');
CALL `add_column_if_not_exists`('tb_order', 'receiver_address', 'varchar(255) NULL DEFAULT NULL COMMENT "收货详细地址"');
CALL `add_column_if_not_exists`('tb_order', 'pay_time', 'datetime NULL DEFAULT NULL COMMENT "支付时间"');
CALL `add_column_if_not_exists`('tb_order', 'deliver_time', 'datetime NULL DEFAULT NULL COMMENT "发货时间"');
CALL `add_column_if_not_exists`('tb_order', 'receive_time', 'datetime NULL DEFAULT NULL COMMENT "收货时间"');
CALL `add_column_if_not_exists`('tb_order', 'freight_amount', 'int NULL DEFAULT 0 COMMENT "运费金额（分）"');
CALL `add_column_if_not_exists`('tb_order', 'discount_amount', 'int NULL DEFAULT 0 COMMENT "优惠金额（分）"');
CALL `add_column_if_not_exists`('tb_order', 'pay_amount', 'int NULL DEFAULT 0 COMMENT "实付金额（分）"');
CALL `add_column_if_not_exists`('tb_order', 'express_no', 'varchar(50) NULL DEFAULT NULL COMMENT "快递单号"');
CALL `add_column_if_not_exists`('tb_order', 'express_company', 'varchar(50) NULL DEFAULT NULL COMMENT "快递公司"');

-- 商品表字段补充
CALL `add_column_if_not_exists`('tb_pet_resource', 'description', 'text NULL COMMENT "商品描述"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'original_price', 'int NULL DEFAULT 0 COMMENT "原价（分）"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'unit', 'varchar(10) NULL DEFAULT "件" COMMENT "单位"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'weight', 'int NULL DEFAULT 0 COMMENT "重量（克）"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'sort', 'int NULL DEFAULT 0 COMMENT "排序"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'is_hot', 'tinyint NULL DEFAULT 0 COMMENT "是否热销"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'is_new', 'tinyint NULL DEFAULT 0 COMMENT "是否新品"');
CALL `add_column_if_not_exists`('tb_pet_resource', 'is_recommend', 'tinyint NULL DEFAULT 0 COMMENT "是否推荐"');

DROP PROCEDURE IF EXISTS `add_column_if_not_exists`;

-- =============================================
-- 二、创建新表
-- =============================================

DROP TABLE IF EXISTS `tb_payment`;
CREATE TABLE `tb_payment` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单id',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式 0-微信 1-支付宝 2-其他',
  `payment_amount` int NULL DEFAULT NULL COMMENT '支付金额（分）',
  `payment_status` tinyint NULL DEFAULT NULL COMMENT '0-待支付 1-成功 2-失败',
  `transaction_id` varchar(100) NULL DEFAULT NULL COMMENT '交易号',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`payment_id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='支付记录表';

DROP TABLE IF EXISTS `tb_order_status`;
CREATE TABLE `tb_order_status` (
  `status_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单id',
  `old_status` tinyint NULL DEFAULT NULL COMMENT '原状态',
  `new_status` tinyint NULL DEFAULT NULL COMMENT '新状态',
  `operate_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `operator` varchar(30) NULL DEFAULT NULL COMMENT '操作人',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`status_id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='订单状态流转表';

DROP TABLE IF EXISTS `tb_cart`;
CREATE TABLE `tb_cart` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `shop_id` bigint NULL DEFAULT NULL COMMENT '店铺id',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '商品id',
  `spec_id` bigint NULL DEFAULT NULL COMMENT '规格id',
  `spec_name` varchar(50) NULL DEFAULT NULL COMMENT '规格名称',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `selected` tinyint NULL DEFAULT 1 COMMENT '0-未选中 1-选中',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`cart_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='购物车表';

DROP TABLE IF EXISTS `tb_user_address`;
CREATE TABLE `tb_user_address` (
  `address_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `receiver_name` varchar(50) NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NULL DEFAULT NULL COMMENT '收货人电话',
  `province` varchar(50) NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) NULL DEFAULT NULL COMMENT '区县',
  `detail_address` varchar(200) NULL DEFAULT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`address_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='用户收货地址表';

DROP TABLE IF EXISTS `tb_product_spec`;
CREATE TABLE `tb_product_spec` (
  `spec_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '商品id',
  `spec_name` varchar(50) NULL DEFAULT NULL COMMENT '规格名称',
  `spec_value` varchar(100) NULL DEFAULT NULL COMMENT '规格值',
  `price` int NULL DEFAULT 0 COMMENT '价格（分）',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `image` varchar(255) NULL DEFAULT NULL COMMENT '规格图片',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`spec_id`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='商品规格表';

DROP TABLE IF EXISTS `tb_product_review`;
CREATE TABLE `tb_product_review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单id',
  `order_item_id` bigint NULL DEFAULT NULL COMMENT '订单项id',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '商品id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `spec_name` varchar(50) NULL DEFAULT NULL COMMENT '规格名称',
  `rating` tinyint NULL DEFAULT 5 COMMENT '评分 1-5',
  `content` varchar(500) NULL DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) NULL DEFAULT NULL COMMENT '评价图片',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名',
  `reply_content` varchar(500) NULL DEFAULT NULL COMMENT '商家回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`review_id`),
  INDEX `idx_resource_id` (`resource_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='商品评价表';

DROP TABLE IF EXISTS `tb_product_favorite`;
CREATE TABLE `tb_product_favorite` (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `resource_id` bigint NULL DEFAULT NULL COMMENT '商品id',
  `create_time` datetime NULL DEFAULT NULL,
  `is_deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`favorite_id`),
  UNIQUE INDEX `uk_user_resource` (`user_id`, `resource_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='商品收藏表';

-- =============================================
-- 三、商品分类数据（完整6个分类）
-- =============================================

DELETE FROM `tb_resource_category` WHERE `category_id` IN (6, 7, 8, 11, 12, 13);
INSERT INTO `tb_resource_category` (`category_id`, `category_name`, `order`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `shop_id`, `remark`) VALUES
(6, '宠物零食', 1, 0, NOW(), NULL, NULL, NULL, 1, NULL),
(7, '宠物玩具', 2, 0, NOW(), NULL, NULL, NULL, 1, NULL),
(8, '宠物洗护', 3, 0, NOW(), NULL, NULL, NULL, 1, NULL),
(11, '宠物服装', 4, 0, NOW(), NULL, NULL, NULL, 1, NULL),
(12, '宠物药品', 5, 0, NOW(), NULL, NULL, NULL, 1, NULL),
(13, '宠物日用', 6, 0, NOW(), NULL, NULL, NULL, 1, NULL);

-- =============================================
-- 四、新表数据填充
-- =============================================

INSERT INTO `tb_payment` VALUES (1, 1, 0, 1112, 1, 'WX1234567890', '2025-12-06 14:36:35', '2025-12-06 14:35:35', 0);
INSERT INTO `tb_payment` VALUES (2, 3, 1, 1112, 1, 'ALI9876543210', '2025-12-06 14:42:37', '2025-12-06 14:41:37', 0);
INSERT INTO `tb_payment` VALUES (3, 4, 0, 232222, 1, 'WX1122334455', '2025-12-10 16:06:36', '2025-12-10 16:05:36', 0);
INSERT INTO `tb_payment` VALUES (4, 5, 1, 10000, 1, 'ALI5544332211', '2025-12-10 22:47:16', '2025-12-10 22:46:16', 0);
INSERT INTO `tb_payment` VALUES (5, 8, 0, 62224, 1, 'WX6677889900', '2025-12-16 11:46:54', '2025-12-16 11:45:54', 0);
INSERT INTO `tb_payment` VALUES (6, 10, 1, 555555, 1, 'ALI0099887766', '2025-12-16 12:24:10', '2025-12-16 12:23:10', 0);

INSERT INTO `tb_order_status` VALUES (1, 1, 0, 1, '2025-12-06 14:36:35', 'system', '支付成功', 0);
INSERT INTO `tb_order_status` VALUES (2, 1, 1, 2, '2025-12-06 14:40:00', 'admin', '商家接单', 0);
INSERT INTO `tb_order_status` VALUES (3, 1, 2, 3, '2025-12-06 15:00:00', 'admin', '商品发货', 0);
INSERT INTO `tb_order_status` VALUES (4, 1, 3, 4, '2025-12-07 10:00:00', 'system', '用户确认收货', 0);
INSERT INTO `tb_order_status` VALUES (5, 3, 0, 1, '2025-12-06 14:42:37', 'system', '支付成功', 0);
INSERT INTO `tb_order_status` VALUES (6, 3, 1, 2, '2025-12-06 15:30:00', 'admin', '商家接单', 0);

INSERT INTO `tb_cart` VALUES (1, 1, 1, 1, 2, 1, '2025-12-01 10:00:00', '2025-12-01 10:00:00', 0);
INSERT INTO `tb_cart` VALUES (2, 1, 1, 4, 1, 1, '2025-12-01 10:01:00', '2025-12-01 10:01:00', 0);
INSERT INTO `tb_cart` VALUES (3, 2, 3, 5, 3, 1, '2025-12-02 14:00:00', '2025-12-02 14:00:00', 0);
INSERT INTO `tb_cart` VALUES (4, 110, 1, 7, 2, 0, '2025-12-03 09:30:00', '2025-12-03 09:30:00', 0);
INSERT INTO `tb_cart` VALUES (5, 110, 4, 1, 5, 1, '2025-12-04 16:00:00', '2025-12-04 16:00:00', 0);
INSERT INTO `tb_cart` VALUES (6, 1, 1, 8, 1, 1, '2025-12-05 11:00:00', '2025-12-05 11:00:00', 0);

INSERT INTO `tb_user_address` VALUES (1, 1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号', 1, '2025-12-01 09:00:00', '2025-12-02 10:00:00', 0);
INSERT INTO `tb_user_address` VALUES (2, 1, '张三', '13800138000', '上海市', '上海市', '浦东新区', '世纪大道100号', 0, '2025-12-02 10:00:00', NULL, 0);
INSERT INTO `tb_user_address` VALUES (3, 2, '李四', '13900139000', '广州市', '广州市', '天河区', '天河路385号', 1, '2025-12-03 14:00:00', NULL, 0);
INSERT INTO `tb_user_address` VALUES (4, 110, '王五', '13700137000', '深圳市', '深圳市', '南山区', '科技园南区', 1, '2025-12-04 09:30:00', NULL, 0);
INSERT INTO `tb_user_address` VALUES (5, 110, '王五', '13700137000', '杭州市', '杭州市', '西湖区', '西湖大道1号', 0, '2025-12-05 16:00:00', NULL, 0);
INSERT INTO `tb_user_address` VALUES (6, 1, '张三', '13800138000', '成都市', '成都市', '锦江区', '春熙路1号', 0, '2025-12-06 11:00:00', NULL, 0);

-- =============================================
-- 五、新增商品数据（每个店铺至少3个商品）
-- =============================================

-- 店铺1 你好宠物店 新增商品
INSERT INTO `tb_pet_resource` (`resource_id`, `resource_name`, `images`, `category_id`, `category_name`, `status`, `specification`, `stock`, `sales`, `create_time`, `create_by`, `remark`, `is_deleted`, `price`, `shop_id`, `description`, `original_price`, `unit`, `weight`, `sort`, `is_hot`, `is_new`, `is_recommend`) VALUES
(10, '冻干鸡肉粒', NULL, 6, '宠物零食', 0, '100g/罐', 200, 0, NOW(), 'admin', '纯肉冻干高蛋白无添加磨牙解馋训练奖励', 0, 3990, 1, '纯肉冻干，高蛋白无添加，磨牙解馋，训练奖励', 4500, '罐', 100, 0, 1, 0, 1),
(11, '三文鱼猫条', NULL, 6, '宠物零食', 0, '15g*12支/包', 350, 0, NOW(), 'admin', '流质湿粮补水增肥美毛护毛适口性佳', 0, 2990, 1, '流质湿粮，补水增肥，美毛护毛，适口性佳', 3500, '包', 180, 0, 1, 1, 1),
(12, '狗狗牛肉粒', NULL, 6, '宠物零食', 0, '500g/袋', 150, 0, NOW(), 'admin', '牛肉制成磨牙洁齿补钙训练互动专用', 0, 2550, 1, '牛肉制成，磨牙洁齿，补钙，训练互动专用', 3000, '袋', 500, 0, 1, 0, 0),
(13, '秋冬加绒四脚衣', NULL, 11, '宠物服装', 0, 'M码(5-8斤)', 180, 0, NOW(), 'admin', '加厚保暖防风弹力舒适不掉毛', 0, 3990, 1, '加厚保暖，防风弹力舒适不掉毛', 4800, '件', 120, 0, 1, 1, 1),
(14, '夏季薄款背心', NULL, 11, '宠物服装', 0, 'S码(3-5斤)', 300, 0, NOW(), 'admin', '透气网眼防晒防掉毛清凉舒适', 0, 1990, 1, '透气网眼，防晒防掉毛清凉舒适', 2500, '件', 50, 0, 0, 1, 0);

-- 店铺3 人民宠物馆 新增商品
INSERT INTO `tb_pet_resource` (`resource_id`, `resource_name`, `images`, `category_id`, `category_name`, `status`, `specification`, `stock`, `sales`, `create_time`, `create_by`, `remark`, `is_deleted`, `price`, `shop_id`, `description`, `original_price`, `unit`, `weight`, `sort`, `is_hot`, `is_new`, `is_recommend`) VALUES
(15, '激光逗猫笔', NULL, 7, '宠物玩具', 0, 'USB充电款', 280, 0, NOW(), 'admin', '红外线逗猫互动娱乐消耗体力电池续航', 0, 1680, 3, '红外线逗猫，互动娱乐消耗体力电池续航', 2000, '个', 50, 0, 1, 1, 1),
(16, '狗狗磨牙鹿角', NULL, 7, '宠物玩具', 0, '中号10cm', 90, 0, NOW(), 'admin', '天然鹿角磨牙洁齿耐咬补钙矿物质', 0, 4500, 3, '天然鹿角，磨牙洁齿耐咬补钙矿物质', 5500, '个', 100, 0, 1, 0, 1),
(17, '猫爬架小型', NULL, 7, '宠物玩具', 0, '50cm高', 60, 0, NOW(), 'admin', '多层跳台剑麻柱磨爪休息小户型适用', 0, 8900, 3, '多层跳台剑麻柱磨爪休息小户型适用', 12000, '个', 2500, 0, 1, 1, 1),
(18, '猫抓板瓦楞纸', NULL, 7, '宠物玩具', 0, '30*20cm', 320, 0, NOW(), 'admin', '耐磨不掉屑磨爪护沙发送猫薄荷', 0, 1580, 3, '耐磨不掉屑磨爪护沙发送猫薄荷', 2000, '个', 200, 0, 1, 0, 0),
(19, '绳结拔河玩具', NULL, 7, '宠物玩具', 0, '25cm', 180, 0, NOW(), 'admin', '棉绳编织磨牙洁齿互动训练耐撕咬', 0, 1850, 3, '棉绳编织磨牙洁齿互动训练耐撕咬', 2200, '个', 150, 0, 0, 0, 0),
(20, '不锈钢宠物食盆', NULL, 13, '宠物日用', 0, '中号(15cm)', 350, 0, NOW(), 'admin', '防滑底防打翻易清洗耐咬', 0, 2580, 3, '防滑底防打翻易清洗耐咬', 3000, '个', 200, 0, 1, 0, 0),
(21, '猫咪豆腐猫砂', NULL, 13, '宠物日用', 0, '6L/袋', 400, 0, NOW(), 'admin', '吸水结团可冲厕所无尘除臭植物原料', 0, 2990, 3, '吸水结团可冲厕所无尘除臭植物原料', 3500, '袋', 3000, 0, 1, 1, 1),
(22, '宠物自动饮水机', NULL, 13, '宠物日用', 0, '1.8L', 120, 0, NOW(), 'admin', '循环过滤活水防干烧静音猫咪爱喝', 0, 6990, 3, '循环过滤活水防干烧静音猫咪爱喝', 8900, '个', 800, 0, 1, 1, 1);

-- 店铺4 小船宠物店 新增商品
INSERT INTO `tb_pet_resource` (`resource_id`, `resource_name`, `images`, `category_id`, `category_name`, `status`, `specification`, `stock`, `sales`, `create_time`, `create_by`, `remark`, `is_deleted`, `price`, `shop_id`, `description`, `original_price`, `unit`, `weight`, `sort`, `is_hot`, `is_new`, `is_recommend`) VALUES
(23, '狗狗专用沐浴露', NULL, 8, '宠物洗护', 0, '500ml/瓶', 250, 0, NOW(), 'admin', '抑菌除臭柔顺毛发温和无刺激', 0, 4990, 4, '抑菌除臭柔顺毛发温和无刺激', 6000, '瓶', 550, 0, 1, 0, 1),
(24, '猫咪专用香波', NULL, 8, '宠物洗护', 0, '300ml/瓶', 180, 0, NOW(), 'admin', '低敏配方护毛去油除体臭蓬松柔顺', 0, 5500, 4, '低敏配方护毛去油除体臭蓬松柔顺', 6500, '瓶', 350, 0, 1, 1, 1),
(25, '宠物免洗泡沫', NULL, 8, '宠物洗护', 0, '200ml/瓶', 220, 0, NOW(), 'admin', '孕期术后幼犬猫免水洗清洁去污', 0, 3850, 4, '孕期术后幼犬猫免水洗清洁去污', 4500, '瓶', 220, 0, 0, 1, 0),
(26, '宠物护毛素', NULL, 8, '宠物洗护', 0, '500ml/瓶', 150, 0, NOW(), 'admin', '柔顺防打结亮毛修护受损毛发', 0, 4200, 4, '柔顺防打结亮毛修护受损毛发', 5000, '瓶', 550, 0, 0, 0, 0),
(27, '宠物除臭喷雾', NULL, 8, '宠物洗护', 0, '300ml/瓶', 210, 0, NOW(), 'admin', '分解异味杀菌猫砂盆窝垫环境适用', 0, 3680, 4, '分解异味杀菌猫砂盆窝垫环境适用', 4200, '瓶', 320, 0, 1, 0, 0),
(28, '犬猫体内外驱虫药', NULL, 12, '宠物药品', 0, '3支/盒', 150, 0, NOW(), 'admin', '广谱驱虫跳蚤蜱虫螨虫体内线虫', 0, 8900, 4, '广谱驱虫跳蚤蜱虫螨虫体内线虫', 12000, '盒', 30, 0, 1, 1, 1),
(29, '猫咪猫鼻支赖氨酸', NULL, 12, '宠物药品', 0, '100g/罐', 200, 0, NOW(), 'admin', '预防猫鼻支增强免疫力缓解打喷嚏流泪', 0, 4200, 4, '预防猫鼻支增强免疫力缓解打喷嚏流泪', 5000, '罐', 100, 0, 1, 0, 0),
(30, '狗狗关节消炎片', NULL, 12, '宠物药品', 0, '60片/瓶', 120, 0, NOW(), 'admin', '缓解关节疼痛炎症修复软骨老年犬适用', 0, 5500, 4, '缓解关节疼痛炎症修复软骨老年犬适用', 6800, '瓶', 60, 0, 0, 0, 0),
(31, '狗狗美洛昔康混悬液', NULL, 12, '宠物药品', 0, '32ml/瓶', 80, 0, NOW(), 'admin', '止痛抗炎术后关节疼痛6周龄可用', 0, 6900, 4, '止痛抗炎术后关节疼痛6周龄可用', 8000, '瓶', 40, 0, 0, 0, 0),
(32, '宠物耳螨药滴耳液', NULL, 12, '宠物药品', 0, '50ml/瓶', 240, 0, NOW(), 'admin', '除耳螨清洁耳道止痒抑菌去异味', 0, 3290, 4, '除耳螨清洁耳道止痒抑菌去异味', 4000, '瓶', 60, 0, 1, 0, 0),
(33, '宠物航空箱', NULL, 13, '宠物日用', 0, 'M码', 70, 0, NOW(), 'admin', '外出托运便携透气抗压医院旅行', 0, 8900, 4, '外出托运便携透气抗压医院旅行', 12000, '个', 2000, 0, 1, 0, 1),
(34, '宠物冰垫夏季', NULL, 13, '宠物日用', 0, 'M码(40cm)', 260, 0, NOW(), 'admin', '降温凉垫耐咬防水解暑散热', 0, 2850, 4, '降温凉垫耐咬防水解暑散热', 3500, '个', 400, 0, 1, 1, 0);

-- =============================================
-- 六、更新现有商品补充新字段
-- =============================================

UPDATE `tb_pet_resource` SET `description` = '精选优质肉源制作，富含高蛋白和多种维生素。采用低温烘干工艺，保留食材营养，适口性好。', `original_price` = 1500, `unit` = '包', `weight` = 160, `is_hot` = 1, `is_recommend` = 1 WHERE `resource_id` = 1;
UPDATE `tb_pet_resource` SET `description` = '法国皇家品牌直供猫粮，专为猫咪定制营养配方。含优质动物蛋白，满足猫咪日常营养需求。', `original_price` = 12000, `unit` = '包', `weight` = 100, `is_hot` = 1, `is_recommend` = 1 WHERE `resource_id` = 4;
UPDATE `tb_pet_resource` SET `description` = '趣味互动玩具逗猫棒，激发猫咪捕猎天性。采用环保材质制作，安全无毒无异味。', `original_price` = 15000, `unit` = '个', `weight` = 100, `is_hot` = 1 WHERE `resource_id` = 5;
UPDATE `tb_pet_resource` SET `description` = '天然胡萝卜条零食，健康美味零负担。选用新鲜胡萝卜为原料，富含胡萝卜素和膳食纤维。', `original_price` = 2500, `unit` = '包', `weight` = 300, `is_new` = 1 WHERE `resource_id` = 7;
UPDATE `tb_pet_resource` SET `description` = '法国进口VEK品牌宠物沐浴露，温和配方不刺激皮肤，深层清洁同时保护宠物皮脂层。', `original_price` = 16800, `unit` = '瓶', `weight` = 400, `is_hot` = 1, `is_recommend` = 1 WHERE `resource_id` = 8;
UPDATE `tb_pet_resource` SET `description` = '疯狂动物城联名款保暖背心，德绒面料柔软亲肤保暖效果好，适合秋冬季节外出穿着。', `original_price` = 3980, `unit` = '件', `weight` = 150, `is_new` = 1 WHERE `resource_id` = 9;

-- =============================================
-- 六、商品规格数据
-- =============================================

-- 店铺1商品规格
INSERT INTO `tb_product_spec` (`spec_id`, `resource_id`, `spec_name`, `spec_value`, `price`, `stock`, `image`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(1, 1, '重量', '500g', 5560, 100, NULL, 0, 1, NOW(), NULL, 0),
(2, 1, '重量', '1kg', 9990, 80, NULL, 1, 1, NOW(), NULL, 0),
(3, 1, '重量', '2kg', 18990, 50, NULL, 2, 1, NOW(), NULL, 0),
(4, 4, '口味', '鸡肉味', 1112, 200, NULL, 0, 1, NOW(), NULL, 0),
(5, 4, '口味', '牛肉味', 1112, 180, NULL, 1, 1, NOW(), NULL, 0),
(6, 4, '口味', '三文鱼味', 1112, 150, NULL, 2, 1, NOW(), NULL, 0),
(7, 7, '规格', '小号(2-4kg)', 1990, 120, NULL, 0, 1, NOW(), NULL, 0),
(8, 7, '规格', '中号(4-7kg)', 2580, 100, NULL, 1, 1, NOW(), NULL, 0),
(9, 7, '规格', '大号(7-10kg)', 3280, 80, NULL, 2, 1, NOW(), NULL, 0),
(10, 8, '颜色', '粉色', 31112, 50, NULL, 0, 1, NOW(), NULL, 0),
(11, 8, '颜色', '蓝色', 31112, 60, NULL, 1, 1, NOW(), NULL, 0),
(12, 8, '颜色', '灰色', 31112, 55, NULL, 2, 1, NOW(), NULL, 0),
(13, 10, '规格', '100g/罐', 3990, 200, NULL, 0, 1, NOW(), NULL, 0),
(14, 10, '规格', '200g/罐', 6990, 150, NULL, 1, 1, NOW(), NULL, 0),
(15, 11, '规格', '15g*12支/包', 2990, 350, NULL, 0, 1, NOW(), NULL, 0),
(16, 11, '规格', '15g*24支/包', 5580, 200, NULL, 1, 1, NOW(), NULL, 0),
(17, 12, '规格', '250g/袋', 1580, 180, NULL, 0, 1, NOW(), NULL, 0),
(18, 12, '规格', '500g/袋', 2550, 150, NULL, 1, 1, NOW(), NULL, 0),
(19, 12, '规格', '1kg/袋', 4580, 100, NULL, 2, 1, NOW(), NULL, 0),
(20, 13, '尺码', 'S码(3-5斤)', 3990, 80, NULL, 0, 1, NOW(), NULL, 0),
(21, 13, '尺码', 'M码(5-8斤)', 3990, 100, NULL, 1, 1, NOW(), NULL, 0),
(22, 13, '尺码', 'L码(8-12斤)', 4580, 60, NULL, 2, 1, NOW(), NULL, 0),
(23, 13, '颜色', '红色', 3990, 50, NULL, 3, 1, NOW(), NULL, 0),
(24, 13, '颜色', '蓝色', 3990, 60, NULL, 4, 1, NOW(), NULL, 0),
(25, 14, '尺码', 'S码(3-5斤)', 1990, 150, NULL, 0, 1, NOW(), NULL, 0),
(26, 14, '尺码', 'M码(5-8斤)', 2280, 120, NULL, 1, 1, NOW(), NULL, 0),
(27, 14, '颜色', '粉色', 1990, 80, NULL, 2, 1, NOW(), NULL, 0),
(28, 14, '颜色', '白色', 1990, 90, NULL, 3, 1, NOW(), NULL, 0);

-- 店铺3商品规格
INSERT INTO `tb_product_spec` (`spec_id`, `resource_id`, `spec_name`, `spec_value`, `price`, `stock`, `image`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(29, 5, '规格', '小号(适合小型犬)', 23222, 50, NULL, 0, 1, NOW(), NULL, 0),
(30, 5, '规格', '中号(适合中型犬)', 28900, 40, NULL, 1, 1, NOW(), NULL, 0),
(31, 5, '规格', '大号(适合大型犬)', 35800, 30, NULL, 2, 1, NOW(), NULL, 0),
(32, 15, '颜色', '红色', 1680, 100, NULL, 0, 1, NOW(), NULL, 0),
(33, 15, '颜色', '蓝色', 1680, 100, NULL, 1, 1, NOW(), NULL, 0),
(34, 15, '颜色', '绿色', 1680, 80, NULL, 2, 1, NOW(), NULL, 0),
(35, 16, '规格', '小号8cm', 3500, 60, NULL, 0, 1, NOW(), NULL, 0),
(36, 16, '规格', '中号10cm', 4500, 50, NULL, 1, 1, NOW(), NULL, 0),
(37, 16, '规格', '大号12cm', 5800, 40, NULL, 2, 1, NOW(), NULL, 0),
(38, 17, '颜色', '原木色', 8900, 30, NULL, 0, 1, NOW(), NULL, 0),
(39, 17, '颜色', '灰色', 8900, 30, NULL, 1, 1, NOW(), NULL, 0),
(40, 18, '形状', '长方形', 1580, 150, NULL, 0, 1, NOW(), NULL, 0),
(41, 18, '形状', '圆形', 1680, 120, NULL, 1, 1, NOW(), NULL, 0),
(42, 18, '形状', '心形', 1780, 100, NULL, 2, 1, NOW(), NULL, 0),
(43, 19, '颜色', '彩色', 1850, 80, NULL, 0, 1, NOW(), NULL, 0),
(44, 19, '颜色', '蓝色', 1850, 60, NULL, 1, 1, NOW(), NULL, 0),
(45, 19, '颜色', '红色', 1850, 40, NULL, 2, 1, NOW(), NULL, 0),
(46, 20, '颜色', '银色', 2580, 120, NULL, 0, 1, NOW(), NULL, 0),
(47, 20, '颜色', '粉色', 2680, 100, NULL, 1, 1, NOW(), NULL, 0),
(48, 20, '颜色', '蓝色', 2680, 130, NULL, 2, 1, NOW(), NULL, 0),
(49, 21, '香味', '原味', 2990, 200, NULL, 0, 1, NOW(), NULL, 0),
(50, 21, '香味', '绿茶味', 3180, 150, NULL, 1, 1, NOW(), NULL, 0),
(51, 21, '香味', '薰衣草味', 3180, 150, NULL, 2, 1, NOW(), NULL, 0),
(52, 22, '颜色', '白色', 6990, 50, NULL, 0, 1, NOW(), NULL, 0),
(53, 22, '颜色', '蓝色', 6990, 40, NULL, 1, 1, NOW(), NULL, 0),
(54, 22, '颜色', '粉色', 6990, 30, NULL, 2, 1, NOW(), NULL, 0);

-- 店铺4商品规格
INSERT INTO `tb_product_spec` (`spec_id`, `resource_id`, `spec_name`, `spec_value`, `price`, `stock`, `image`, `sort`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(55, 23, '香味', '清新花香', 4990, 100, NULL, 0, 1, NOW(), NULL, 0),
(56, 23, '香味', '牛奶香', 4990, 80, NULL, 1, 1, NOW(), NULL, 0),
(57, 23, '香味', '无香型', 4990, 70, NULL, 2, 1, NOW(), NULL, 0),
(58, 24, '香味', '海洋清新', 5500, 80, NULL, 0, 1, NOW(), NULL, 0),
(59, 24, '香味', '牛奶蜂蜜', 5500, 60, NULL, 1, 1, NOW(), NULL, 0),
(60, 25, '香味', '绿茶', 3850, 100, NULL, 0, 1, NOW(), NULL, 0),
(61, 25, '香味', '薰衣草', 3850, 80, NULL, 1, 1, NOW(), NULL, 0),
(62, 26, '规格', '300ml', 3200, 100, NULL, 0, 1, NOW(), NULL, 0),
(63, 26, '规格', '500ml', 4200, 80, NULL, 1, 1, NOW(), NULL, 0),
(64, 27, '香味', '柠檬', 3680, 80, NULL, 0, 1, NOW(), NULL, 0),
(65, 27, '香味', '绿茶', 3680, 70, NULL, 1, 1, NOW(), NULL, 0),
(66, 27, '香味', '无香', 3680, 60, NULL, 2, 1, NOW(), NULL, 0),
(67, 28, '规格', '1支装', 3500, 100, NULL, 0, 1, NOW(), NULL, 0),
(68, 28, '规格', '3支/盒', 8900, 80, NULL, 1, 1, NOW(), NULL, 0),
(69, 28, '规格', '6支/盒', 15800, 50, NULL, 2, 1, NOW(), NULL, 0),
(70, 29, '规格', '50g/罐', 2580, 150, NULL, 0, 1, NOW(), NULL, 0),
(71, 29, '规格', '100g/罐', 4200, 100, NULL, 1, 1, NOW(), NULL, 0),
(72, 30, '规格', '30片/瓶', 3200, 80, NULL, 0, 1, NOW(), NULL, 0),
(73, 30, '规格', '60片/瓶', 5500, 60, NULL, 1, 1, NOW(), NULL, 0),
(74, 31, '规格', '16ml/瓶', 4500, 50, NULL, 0, 1, NOW(), NULL, 0),
(75, 31, '规格', '32ml/瓶', 6900, 40, NULL, 1, 1, NOW(), NULL, 0),
(76, 32, '规格', '50ml/瓶', 3290, 150, NULL, 0, 1, NOW(), NULL, 0),
(77, 32, '规格', '100ml/瓶', 5580, 100, NULL, 1, 1, NOW(), NULL, 0),
(78, 33, '尺码', 'S码', 6900, 30, NULL, 0, 1, NOW(), NULL, 0),
(79, 33, '尺码', 'M码', 8900, 25, NULL, 1, 1, NOW(), NULL, 0),
(80, 33, '尺码', 'L码', 10900, 20, NULL, 2, 1, NOW(), NULL, 0);

-- =============================================
-- 七、商品评价数据
-- =============================================

INSERT INTO `tb_product_review` (`review_id`, `order_id`, `order_item_id`, `resource_id`, `user_id`, `spec_name`, `rating`, `content`, `images`, `is_anonymous`, `reply_content`, `reply_time`, `like_count`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(1, 1, 1, 4, 1, '鸡肉味', 5, '狗狗特别喜欢吃这个口味，每次都抢着吃，质量很好，会回购的！', NULL, 0, '感谢亲的支持和认可，我们会继续努力提供优质产品！', '2025-12-08 10:00:00', 15, 1, '2025-12-07 15:30:00', NULL, 0),
(2, 1, 2, 1, 1, '1kg', 5, '猫粮质量很好，猫咪吃了毛色都变亮了，颗粒大小适中，适口性很好。', NULL, 0, '感谢您的好评，猫咪吃得健康是我们最大的心愿！', '2025-12-08 10:30:00', 23, 1, '2025-12-07 16:00:00', NULL, 0),
(3, 3, NULL, 5, 2, '中号(适合中型犬)', 4, '狗窝质量不错，狗狗很喜欢，就是物流稍微慢了一点，整体满意。', NULL, 0, '感谢您的反馈，我们会改进物流速度，期待您的再次光临！', '2025-12-09 14:00:00', 8, 1, '2025-12-08 18:00:00', NULL, 0),
(4, 4, NULL, 8, 110, '蓝色', 5, '猫爬架做工精细，猫咪很喜欢在上面玩耍，稳定性也很好，推荐购买！', NULL, 0, '感谢您的五星好评，祝您和猫咪玩得开心！', '2025-12-12 09:00:00', 31, 1, '2025-12-11 20:00:00', NULL, 0),
(5, 5, NULL, 1, 1, '2kg', 5, '第二次购买了，品质一如既往的好，价格也很实惠，性价比很高！', NULL, 0, '感谢老顾客的支持，我们会保持品质，期待下次光临！', '2025-12-18 11:00:00', 12, 1, '2025-12-17 14:00:00', NULL, 0),
(6, 8, NULL, 7, 1, '中号(4-7kg)', 4, '衣服质量不错，就是尺码稍微偏小一点，建议买大一码。', NULL, 0, '感谢您的建议，我们会优化尺码说明，祝您购物愉快！', '2025-12-18 15:00:00', 6, 1, '2025-12-17 19:00:00', NULL, 0),
(7, 10, NULL, 5, 110, '大号(适合大型犬)', 5, '狗窝很舒服，我家金毛很喜欢，做工精细，物超所值！', NULL, 1, '感谢您的认可，祝您家金毛睡得香！', '2025-12-18 16:00:00', 18, 1, '2025-12-17 20:00:00', NULL, 0),
(8, 1, 1, 4, 1, '牛肉味', 4, '这个口味也不错，狗狗吃得津津有味，就是包装可以再改进一下。', NULL, 0, NULL, NULL, 5, 1, '2025-12-07 17:00:00', NULL, 0),
(9, 3, NULL, 15, 2, '红色', 5, '逗猫笔很好玩，猫咪追着跑，电量也很耐用，性价比超高！', NULL, 0, '感谢好评，祝您和猫咪玩得开心！', '2025-12-09 15:00:00', 9, 1, '2025-12-08 19:00:00', NULL, 0),
(10, 4, NULL, 21, 110, '原味', 5, '猫砂吸水效果很好，结团快，几乎没有粉尘，猫咪用着很舒服。', NULL, 0, '感谢您的认可，我们会继续提供优质产品！', '2025-12-12 10:00:00', 22, 1, '2025-12-11 21:00:00', NULL, 0),
(11, 5, NULL, 10, 1, '100g/罐', 5, '冻干鸡肉粒质量很好，猫咪特别爱吃，当零食奖励很棒！', NULL, 0, '感谢支持，祝您的猫咪健康成长！', '2025-12-18 12:00:00', 14, 1, '2025-12-17 15:00:00', NULL, 0),
(12, 8, NULL, 13, 1, 'M码(5-8斤)', 5, '衣服面料很柔软，保暖效果也好，我家泰迪穿上很可爱！', NULL, 0, '感谢好评，祝您家泰迪暖暖过冬！', '2025-12-18 16:00:00', 7, 1, '2025-12-17 20:30:00', NULL, 0),
(13, 10, NULL, 23, 110, '清新花香', 4, '沐浴露味道很好闻，洗完毛很柔顺，就是泡沫可以再丰富一点。', NULL, 0, '感谢您的反馈，我们会不断改进产品！', '2025-12-18 17:00:00', 4, 1, '2025-12-17 21:00:00', NULL, 0),
(14, 4, NULL, 22, 110, '白色', 5, '饮水机很安静，猫咪喜欢喝流动的水，容量也够大，不用天天加水了。', NULL, 0, '感谢您的详细评价，祝您和猫咪生活愉快！', '2025-12-12 11:00:00', 28, 1, '2025-12-11 22:00:00', NULL, 0),
(15, 1, 1, 11, 1, '15g*12支/包', 5, '猫条适口性超级好，每次喂食猫咪都很兴奋，会继续回购！', NULL, 0, '感谢支持，期待下次光临！', '2025-12-08 11:00:00', 19, 1, '2025-12-07 18:00:00', NULL, 0);

-- =============================================
-- 六、商品评论互动表（作为 tb_product_review 的子评论/回复）
-- =====================================================
-- 职责说明：
--   tb_product_review = 主评价表（用户对商品的首次评价）
--   tb_product_comment  = 互动评论表（对主评价的回复、追评等）
-- 关系：comment.review_id → review.review_id

DROP TABLE IF EXISTS `tb_product_comment`;
CREATE TABLE `tb_product_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `review_id` bigint NOT NULL COMMENT '关联的主评价ID（tb_product_review.review_id）',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID（0表示直接回复评价，其他表示回复其他评论）',
  `reply_to_id` bigint NULL DEFAULT NULL COMMENT '回复的目标评论ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID（用户角色时）',
  `shop_id` bigint NULL DEFAULT NULL COMMENT '店铺ID（商家角色时）',
  `user_type` tinyint NOT NULL DEFAULT 1 COMMENT '评论者角色：1-用户 2-商家',
  `comment_type` tinyint NOT NULL DEFAULT 3 COMMENT '评论类型：2-追评 3-回复',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `images` varchar(2000) NULL DEFAULT NULL COMMENT '评论图片（逗号分隔）',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-隐藏 1-显示 2-待审核',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`comment_id`),
  INDEX `idx_review_id` (`review_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_shop_id` (`shop_id`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='商品评论互动表（子评论/回复）';

-- 评论点赞记录表
DROP TABLE IF EXISTS `tb_comment_like`;
CREATE TABLE `tb_comment_like` (
  `like_id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '点赞时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否取消',
  PRIMARY KEY (`like_id`),
  UNIQUE INDEX `uk_comment_user` (`comment_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='评论点赞记录表';

-- 测试数据：商品评论互动（关联到 tb_product_review.review_id）
INSERT INTO `tb_product_comment` (`comment_id`, `review_id`, `parent_id`, `reply_to_id`, `user_id`, `shop_id`, `user_type`, `comment_type`, `content`, `images`, `like_count`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
(1, 1, 0, NULL, 2, NULL, 1, 3, '同款！我家狗狗也超爱吃这个鸡肉味的~', NULL, 8, 1, '2025-12-08 09:00:00', NULL, 0),
(2, 1, 1, 1, NULL, 1, 2, 3, '感谢认可！这款鸡肉味是我们店的热销款哦~', NULL, 3, 1, '2025-12-08 10:30:00', NULL, 0),
(3, 1, 0, NULL, 110, NULL, 1, 3, '请问这个适合小型犬吗？我家泰迪可以吃吗？', NULL, 5, 1, '2025-12-08 11:00:00', NULL, 0),
(4, 1, 3, 3, NULL, 1, 2, 3, '亲，这款非常适合小型犬哦，颗粒大小适中，泰迪完全可以吃~', NULL, 2, 1, '2025-12-08 12:00:00', NULL, 0),
(5, 2, 0, NULL, 2, NULL, 1, 3, '我家猫咪也吃这个，确实毛色变亮了！', NULL, 12, 1, '2025-12-08 10:00:00', NULL, 0),
(6, 2, 5, 5, NULL, 1, 2, 3, '感谢分享！这款猫粮添加了美毛成分，对猫咪毛发很有帮助~', NULL, 4, 1, '2025-12-08 11:30:00', NULL, 0),
(7, 2, 0, NULL, 110, NULL, 1, 3, '请问幼猫可以吃吗？我家小猫3个月大', NULL, 6, 1, '2025-12-08 14:00:00', NULL, 0),
(8, 2, 7, 7, NULL, 1, 2, 3, '可以的哦，这款适合全阶段猫咪，幼猫也能吃~', NULL, 3, 1, '2025-12-08 15:00:00', NULL, 0),
(9, 3, 0, NULL, 1, NULL, 1, 3, '物流确实有点慢，我等了一周才到', NULL, 4, 1, '2025-12-09 10:00:00', NULL, 0),
(10, 3, 9, 9, NULL, 2, 2, 3, '非常抱歉给您带来不便，我们会优化物流渠道，下次一定更快！', NULL, 2, 1, '2025-12-09 15:00:00', NULL, 0),
(11, 4, 0, NULL, 1, NULL, 1, 3, '这个猫爬架真的稳！我家胖猫跳来跳去都没问题', NULL, 15, 1, '2025-12-12 10:00:00', NULL, 0),
(12, 4, 11, 11, NULL, 1, 2, 3, '感谢好评！这款采用加粗立柱，承重力超强~', NULL, 5, 1, '2025-12-12 11:00:00', NULL, 0),
(13, 4, 0, NULL, 2, NULL, 1, 3, '请问好安装吗？一个人能装好吗？', NULL, 8, 1, '2025-12-12 14:00:00', NULL, 0),
(14, 4, 13, 13, NULL, 1, 2, 3, '亲，这款安装很简单，有详细说明书，一个人完全可以搞定~', NULL, 3, 1, '2025-12-12 15:00:00', NULL, 0),
(15, 5, 0, NULL, 2, NULL, 1, 3, '老顾客+1，这个牌子确实靠谱！', NULL, 10, 1, '2025-12-18 12:00:00', NULL, 0),
(16, 5, 15, 15, NULL, 1, 2, 3, '感谢老顾客支持！会员有专属优惠哦~', NULL, 4, 1, '2025-12-18 13:00:00', NULL, 0),
(17, 6, 0, NULL, 110, NULL, 1, 3, '尺码确实偏小，建议买大一码', NULL, 6, 1, '2025-12-18 16:00:00', NULL, 0),
(18, 6, 17, 17, NULL, 1, 2, 3, '感谢反馈！我们会更新尺码建议，方便大家选购~', NULL, 2, 1, '2025-12-18 17:00:00', NULL, 0),
(19, 7, 0, NULL, 1, NULL, 1, 3, '金毛用大号确实舒服，我家也是金毛~', NULL, 8, 1, '2025-12-18 17:30:00', NULL, 0),
(20, 7, 19, 19, NULL, 1, 2, 3, '感谢分享！大号适合大型犬，舒适度满分~', NULL, 3, 1, '2025-12-18 18:00:00', NULL, 0),
(21, 10, 0, NULL, 1, NULL, 1, 3, '猫砂确实好用，结团快，几乎没粉尘！', NULL, 18, 1, '2025-12-12 11:00:00', NULL, 0),
(22, 10, 21, 21, NULL, 3, 2, 3, '感谢认可！我们的豆腐猫砂天然环保，可冲厕所~', NULL, 6, 1, '2025-12-12 12:00:00', NULL, 0),
(23, 10, 0, NULL, 2, NULL, 1, 3, '请问这个可以直接冲马桶吗？', NULL, 5, 1, '2025-12-12 14:00:00', NULL, 0),
(24, 10, 23, 23, NULL, 3, 2, 3, '可以的哦！豆腐猫砂遇水即化，直接冲马桶很方便~', NULL, 2, 1, '2025-12-12 15:00:00', NULL, 0),
(25, 11, 0, NULL, 2, NULL, 1, 3, '冻干质量确实好，我家猫咪超爱吃！', NULL, 12, 1, '2025-12-18 13:00:00', NULL, 0),
(26, 11, 25, 25, NULL, 1, 2, 3, '感谢支持！纯肉制作无添加，放心喂食~', NULL, 4, 1, '2025-12-18 14:00:00', NULL, 0),
(27, 12, 0, NULL, 110, NULL, 1, 3, '衣服做工很好，我家泰迪穿上超可爱！', NULL, 10, 1, '2025-12-18 21:00:00', NULL, 0),
(28, 12, 27, 27, NULL, 1, 2, 3, '感谢好评！这款四脚衣加绒保暖，冬天必备~', NULL, 3, 1, '2025-12-18 22:00:00', NULL, 0),
(29, 14, 0, NULL, 1, NULL, 1, 3, '饮水机真的很安静，猫咪喜欢喝流动的水！', NULL, 20, 1, '2025-12-12 09:00:00', NULL, 0),
(30, 14, 29, 29, NULL, 3, 2, 3, '感谢详细评价！静音水泵+三重过滤，让宠物喝上健康活水~', NULL, 8, 1, '2025-12-12 10:00:00', NULL, 0),
(31, 14, 0, NULL, 2, NULL, 1, 3, '请问多久换一次水比较好？', NULL, 6, 1, '2025-12-12 11:00:00', NULL, 0),
(32, 14, 31, 31, NULL, 3, 2, 3, '建议3-5天换一次水，滤芯一个月换一次效果最佳~', NULL, 3, 1, '2025-12-12 12:00:00', NULL, 0),
(33, 15, 0, NULL, 110, NULL, 1, 3, '猫条适口性超好，我家猫咪每次都很兴奋！', NULL, 15, 1, '2025-12-08 12:00:00', NULL, 0),
(34, 15, 33, 33, NULL, 1, 2, 3, '感谢支持！新鲜三文鱼制作，营养丰富~', NULL, 5, 1, '2025-12-08 13:00:00', NULL, 0);

-- 测试数据：评论点赞记录
INSERT INTO `tb_comment_like` (`like_id`, `comment_id`, `user_id`, `create_time`, `is_deleted`) VALUES
(1, 1, 2, '2025-12-08 10:00:00', 0),
(2, 1, 110, '2025-12-08 11:00:00', 0),
(3, 2, 1, '2025-12-08 11:00:00', 0),
(4, 5, 1, '2025-12-08 11:00:00', 0),
(5, 5, 110, '2025-12-08 12:00:00', 0),
(6, 6, 2, '2025-12-08 12:00:00', 0),
(7, 11, 1, '2025-12-12 11:00:00', 0),
(8, 11, 2, '2025-12-12 12:00:00', 0),
(9, 21, 1, '2025-12-12 12:00:00', 0),
(10, 21, 2, '2025-12-12 13:00:00', 0),
(11, 22, 110, '2025-12-12 13:00:00', 0),
(12, 29, 1, '2025-12-12 10:00:00', 0),
(13, 29, 2, '2025-12-12 11:00:00', 0),
(14, 30, 110, '2025-12-12 11:00:00', 0),
(15, 33, 1, '2025-12-08 13:00:00', 0),
(16, 33, 2, '2025-12-08 14:00:00', 0);

SET FOREIGN_KEY_CHECKS = 1;
