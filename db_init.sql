-- ============================================================
-- 电商平台数据库初始化脚本
-- 数据库: db_aps
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_0900_ai_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS `db_aps`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `db_aps`;

-- -----------------------------------------------------------
-- 1. 分类表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '分类 ID',
  `name`        VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
  `sort`        INT          DEFAULT 0                COMMENT '排序值',
  `status`      TINYINT      DEFAULT 1                COMMENT '状态：0-禁用，1-正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分类表';

-- -----------------------------------------------------------
-- 2. 商品表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品 ID',
  `merchant_id` BIGINT        DEFAULT 1                COMMENT '商家 ID（默认 1）',
  `name`        VARCHAR(200)  NOT NULL                 COMMENT '商品名称',
  `description` VARCHAR(1000) NULL                     COMMENT '商品描述',
  `price`       DECIMAL(10,2) NOT NULL                 COMMENT '价格',
  `stock`       INT           DEFAULT 0                COMMENT '库存',
  `sales`       INT           DEFAULT 0                COMMENT '销量',
  `category_id` INT           NOT NULL                 COMMENT '分类 ID',
  `main_image`  VARCHAR(500)  NULL                     COMMENT '主图 URL',
  `status`      TINYINT       DEFAULT 1                COMMENT '状态：0-下架，1-上架',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status`      (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

-- -----------------------------------------------------------
-- 3. 用户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL                 COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL                 COMMENT '密码（加密存储）',
  `nickname`    VARCHAR(50)  NULL                     COMMENT '昵称',
  `phone`       VARCHAR(20)  NULL                     COMMENT '手机号',
  `email`       VARCHAR(100) NULL                     COMMENT '邮箱',
  `avatar`      VARCHAR(255) NULL                     COMMENT '头像路径',
  `gender`      TINYINT(1)   DEFAULT 0                COMMENT '性别：0-未知，1-男，2-女',
  `status`      TINYINT(1)   DEFAULT 1                COMMENT '状态：1-正常，0-禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_phone`    (`phone`),
  UNIQUE KEY `idx_email`    (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 4. 优惠券表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id`              INT          NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `description`     VARCHAR(500) NULL                     COMMENT '优惠券简介',
  `category_id`     INT          NULL                     COMMENT '分类ID，关联category表',
  `min_spend`       DECIMAL(10,2) NOT NULL                COMMENT '最低消费金额',
  `discount_amount` DECIMAL(10,2) NOT NULL                COMMENT '可扣减金额',
  `start_time`      DATETIME     NOT NULL                 COMMENT '开始抢购时间',
  `end_time`        DATETIME     NOT NULL                 COMMENT '结束抢购时间',
  `valid_period`    INT          NOT NULL                 COMMENT '有效期（天数）',
  `stock`           INT          NOT NULL                 COMMENT '库存数量',
  `image`           VARCHAR(255) NULL                     COMMENT '优惠券图片路径',
  `status`          TINYINT(1)   DEFAULT 1                COMMENT '状态：1-启用，0-禁用',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_start_end_time` (`start_time`, `end_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券表';

-- -----------------------------------------------------------
-- 5. 地址表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id`             INT         NOT NULL AUTO_INCREMENT COMMENT '地址 ID',
  `user_id`        BIGINT      NOT NULL                 COMMENT '用户 ID',
  `name`           VARCHAR(50) NOT NULL                 COMMENT '收货人姓名',
  `phone`          VARCHAR(20) NOT NULL                 COMMENT '收货人电话',
  `province`       VARCHAR(50) NOT NULL                 COMMENT '省份',
  `city`           VARCHAR(50) NOT NULL                 COMMENT '城市',
  `district`       VARCHAR(50) NOT NULL                 COMMENT '区/县',
  `detail_address` VARCHAR(255) NOT NULL                COMMENT '详细地址',
  `is_default`     TINYINT     DEFAULT 0                COMMENT '是否默认：0-否，1-是',
  `create_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地址表';

-- -----------------------------------------------------------
-- 6. 购物车表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '购物车 ID',
  `user_id`       BIGINT        NOT NULL                 COMMENT '用户 ID',
  `product_id`    BIGINT        NOT NULL                 COMMENT '商品 ID',
  `product_name`  VARCHAR(200)  NULL                     COMMENT '商品名称（冗余字段）',
  `product_image` VARCHAR(500)  NULL                     COMMENT '商品图片（冗余字段）',
  `price`         DECIMAL(10,2) NOT NULL                 COMMENT '单价',
  `quantity`      INT           DEFAULT 1                COMMENT '购买数量',
  `is_checked`    TINYINT       DEFAULT 1                COMMENT '是否选中：0-未选中，1-选中',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '加入购物车时间',
  `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`) COMMENT '同一用户同一商品只有一条记录',
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';

-- -----------------------------------------------------------
-- 7. 订单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id`                      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `order_no`                VARCHAR(50)   NOT NULL                 COMMENT '订单号',
  `user_id`                 BIGINT        NOT NULL                 COMMENT '用户 ID',
  `status`                  TINYINT       DEFAULT 0                COMMENT '状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `total_amount`            DECIMAL(10,2) NOT NULL                 COMMENT '总金额（优惠券抵扣前）',
  `freight_amount`          DECIMAL(10,2) DEFAULT 0.00             COMMENT '运费',
  `coupon_amount`           DECIMAL(10,2) DEFAULT 0.00             COMMENT '优惠券总抵扣金额',
  `pay_amount`              DECIMAL(10,2) NOT NULL                 COMMENT '实付金额（总金额+运费-优惠券总抵扣）',
  `payment_type`            VARCHAR(20)   NULL                     COMMENT '支付方式',
  `payment_time`            DATETIME      NULL                     COMMENT '支付时间',
  `receiver_name`           VARCHAR(50)   NOT NULL                 COMMENT '收货人',
  `receiver_phone`          VARCHAR(20)   NOT NULL                 COMMENT '收货电话',
  `receiver_province`       VARCHAR(50)   NOT NULL                 COMMENT '收货省份',
  `receiver_city`           VARCHAR(50)   NOT NULL                 COMMENT '收货城市',
  `receiver_district`       VARCHAR(50)   NOT NULL                 COMMENT '收货区/县',
  `receiver_detail_address` VARCHAR(255)  NOT NULL                 COMMENT '收货详细地址',
  `remark`                  VARCHAR(500)  NULL                     COMMENT '订单备注',
  `after_sale_status`       TINYINT       DEFAULT 0                COMMENT '售后状态：0-无售后，1-售后中',
  `create_time`             DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status`  (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- -----------------------------------------------------------
-- 8. 订单商品表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单商品 ID',
  `order_id`      BIGINT        NOT NULL                 COMMENT '订单 ID',
  `product_id`    BIGINT        NOT NULL                 COMMENT '商品 ID',
  `product_name`  VARCHAR(200)  NOT NULL                 COMMENT '商品名称',
  `product_image` VARCHAR(500)  NULL                     COMMENT '商品图片',
  `category_id`   INT           NOT NULL                 COMMENT '商品分类 ID（用于优惠券匹配）',
  `price`         DECIMAL(10,2) NOT NULL                 COMMENT '单价',
  `quantity`      INT           NOT NULL                 COMMENT '数量',
  `total_price`   DECIMAL(10,2) NOT NULL                 COMMENT '小计',
  PRIMARY KEY (`id`),
  KEY `idx_order_id`   (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品表';

-- -----------------------------------------------------------
-- 9. 订单优惠券明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `order_coupon`;
CREATE TABLE `order_coupon` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `order_id`        BIGINT        NOT NULL                 COMMENT '订单 ID',
  `coupon_id`       BIGINT        NOT NULL                 COMMENT '优惠券 ID',
  `category_id`     INT           NULL                     COMMENT '分类 ID（冗余字段）',
  `discount_amount` DECIMAL(10,2) NOT NULL                 COMMENT '该优惠券的抵扣金额',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单优惠券明细表';

-- -----------------------------------------------------------
-- 10. 收藏表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏 ID',
  `user_id`     BIGINT   NOT NULL                 COMMENT '用户 ID',
  `product_id`  BIGINT   NOT NULL                 COMMENT '商品 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`) COMMENT '用户对同一商品只能收藏一次',
  KEY `idx_user_id`    (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';

-- -----------------------------------------------------------
-- 11. 用户优惠券表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id`     BIGINT   NOT NULL                 COMMENT '用户 ID',
  `coupon_id`   BIGINT   NOT NULL                 COMMENT '优惠券 ID',
  `status`      TINYINT  DEFAULT 0                COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `order_id`    BIGINT   NULL                     COMMENT '使用该优惠券的订单 ID',
  `get_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time`    DATETIME NULL                     COMMENT '使用时间',
  `expire_time` DATETIME NOT NULL                 COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_coupon` (`user_id`, `coupon_id`) COMMENT '每人限领一张',
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户领取优惠券表';

-- -----------------------------------------------------------
-- 12. 商品评论表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
  `user_id`     BIGINT        NOT NULL                 COMMENT '用户 ID',
  `product_id`  BIGINT        NOT NULL                 COMMENT '商品 ID',
  `rating`      TINYINT       NOT NULL                 COMMENT '评分：1-5 星',
  `content`     VARCHAR(1000) NULL                     COMMENT '评论内容',
  `images`      VARCHAR(2000) NULL                     COMMENT '评论图片（多张图片用逗号分隔）',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_product_time` (`product_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';

-- -----------------------------------------------------------
-- 13. 售后主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '售后 ID',
  `order_id`        BIGINT        NOT NULL                 COMMENT '关联订单',
  `user_id`         BIGINT        NOT NULL                 COMMENT '用户',
  `reason`          VARCHAR(200)  NOT NULL                 COMMENT '售后原因',
  `description`     VARCHAR(500)  NULL                     COMMENT '问题描述',
  `images`          VARCHAR(500)  NULL                     COMMENT '凭证图片URL（逗号分隔）',
  `refund_amount`   DECIMAL(10,2) NOT NULL                 COMMENT '退款金额',
  `status`          TINYINT       DEFAULT 0                COMMENT '状态：0-待处理，1-已通过，2-已驳回，3-已完成',
  `admin_remark`    VARCHAR(200)  NULL                     COMMENT '商家处理备注',
  `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id`  (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='售后主表';

-- -----------------------------------------------------------
-- 14. 售后商品明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `after_sale_item`;
CREATE TABLE `after_sale_item` (
  `id`               BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细 ID',
  `after_sale_id`    BIGINT NOT NULL                 COMMENT '关联售后主表',
  `order_item_id`    BIGINT NOT NULL                 COMMENT '关联订单商品项',
  PRIMARY KEY (`id`),
  KEY `idx_after_sale_id` (`after_sale_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='售后商品明细表';

-- -----------------------------------------------------------
-- 15. 商家表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商家 ID',
  `name`         VARCHAR(100) NOT NULL                 COMMENT '商家名称',
  `phone`        VARCHAR(20)  NULL                     COMMENT '联系电话',
  `description`  TEXT         NULL                     COMMENT '商家描述',
  `logo`         VARCHAR(500) NULL                     COMMENT 'Logo URL',
  `status`       TINYINT      DEFAULT 1                COMMENT '0=禁用 1=启用',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表';

-- -----------------------------------------------------------
-- 16. 默认商家数据
-- -----------------------------------------------------------
INSERT INTO `merchant` (`name`, `phone`, `description`, `logo`, `status`) VALUES
('FlowShop 官方旗舰店', '400-888-8888', 'FlowShop 官方自营店铺', 'https://via.placeholder.com/100', 1);

kkkkkkkkkk