# 售后模块设计文档

## 概述

为 Traework 电商平台新增售后模块，支持用户对已支付订单申请退款。采用按商品维度的售后方案（方案 B），用户可以选择订单中的具体商品项申请售后，支持部分退款。

本项目为模拟项目，不涉及真实支付、物流、地图 API。售后流程为：用户提交售后申请 → 商家审批（通过/驳回）→ 完成。

## 数据库设计

### 新增 after_sale 表（售后主表）

```sql
CREATE TABLE after_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '关联订单',
    user_id BIGINT NOT NULL COMMENT '用户',
    reason VARCHAR(200) NOT NULL COMMENT '售后原因',
    description VARCHAR(500) COMMENT '问题描述',
    images VARCHAR(500) COMMENT '凭证图片URL（逗号分隔）',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    status TINYINT DEFAULT 0 COMMENT '0待处理 1已通过 2已驳回 3已完成',
    admin_remark VARCHAR(200) COMMENT '商家处理备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) COMMENT '售后主表';
```

### 新增 after_sale_item 表（售后商品明细）

```sql
CREATE TABLE after_sale_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    after_sale_id BIGINT NOT NULL COMMENT '关联售后主表',
    order_item_id BIGINT NOT NULL COMMENT '关联订单商品项'
) COMMENT '售后商品明细表';
```

此表为关联表，仅存储售后单与订单商品项的对应关系。商品数量和价格从 order_item 表获取，不重复存储。

### orders 表新增字段

```sql
ALTER TABLE orders ADD COLUMN after_sale_status TINYINT DEFAULT 0 COMMENT '0无售后 1售后中';
```

用于在订单列表中快速筛选售后中的订单，避免每次查询都关联 after_sale 表。

## 售后状态流转

```
用户提交 → 待处理(0) ──商家审批──▶ 已通过(1) → 已完成(3)
                             │
                             └──已驳回(2)

用户可取消：待处理(0) → 用户主动取消
```

- **待处理(0)**：用户刚提交，等待商家处理
- **已通过(1)**：商家同意退款
- **已驳回(2)**：商家拒绝退款（附带驳回原因）
- **已完成(3)**：退款流程结束

## 订单状态流转（更新）

```
待付款(0) ──支付──▶ 待发货(1) ──发货──▶ 待收货(2) ──确认收货──▶ 已完成(3)
    │                                        │
    └──── 取消 ◀─────────────────────────────┘
                                              ▼
                                        已取消(4)
```

新增「确认收货」操作：用户手动将待收货(2)变为已完成(3)。这是售后申请的前提条件之一。

## 售后申请条件

仅以下状态的订单可申请售后：
- 待收货(2)：已发货但未确认收货
- 已完成(3)：已确认收货

其他状态（待付款、待发货、已取消）不可申请售后。同一订单如果已有售后中的记录（after_sale_status=1），不可重复申请。

## 后端 API 设计

### 售后接口（5 个）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/after-sale` | 提交售后申请 |
| GET | `/after-sale/list?userId=` | 用户售后记录列表 |
| GET | `/after-sale/detail?id=` | 售后单详情（含商品明细、商家备注） |
| PUT | `/after-sale/cancel?id=` | 用户取消售后（仅待处理状态） |
| GET | `/after-sale/stats?userId=` | 用户售后统计（各状态数量） |

### 订单接口补充（1 个）

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/order/confirm` | 确认收货，status 2→3 |

### 商家接口（预留，本次不实现）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/after-sale/admin/list` | 商家查看售后列表 |
| PUT | `/after-sale/admin/process` | 商家处理售后（通过/驳回） |

### 提交售后申请请求体

```json
{
  "userId": 1,
  "orderId": 10,
  "reason": "质量问题",
  "description": "收到的商品有破损",
  "images": "https://example.com/img1.jpg,https://example.com/img2.jpg",
  "orderItemIds": [101, 102]
}
```

- `orderItemIds`：用户选择的售后商品项 ID 列表
- `refund_amount`：后端自动计算，= 所选 order_item 的 totalPrice 之和

### 售后列表响应

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD20260508001",
      "orderId": 10,
      "reason": "质量问题",
      "refundAmount": 99.00,
      "status": 0,
      "statusText": "待处理",
      "createTime": "2026-05-08 10:30:00",
      "items": [
        {
          "orderItemId": 101,
          "productName": "商品A",
          "productImage": "https://...",
          "price": 49.50,
          "quantity": 1,
          "totalPrice": 49.50
        }
      ]
    }
  ]
}
```

## 前端设计

### 新增页面

| 页面 | 路由 | 说明 |
|------|------|------|
| AfterSaleApply.vue | `/after-sale/apply?orderId=` | 售后申请表单页 |
| AfterSaleDetail.vue | `/after-sale/detail?id=` | 售后单详情页 |

### 修改页面

**Personal.vue（个人中心）：**
- 侧边栏新增「售后处理」导航项
- 订单列表新增「售后中」标签页，筛选 after_sale_status=1 的订单
- 订单卡片：待收货/已完成状态显示「申请售后」按钮；售后中的订单显示「查看售后」按钮
- 待收货状态的订单显示「确认收货」按钮

**OrderDetail.vue（订单详情）：**
- 待收货状态：显示「确认收货」按钮
- 待收货/已完成状态：显示「申请售后」按钮
- 售后中状态：显示「查看售后」按钮

### 售后申请页（AfterSaleApply.vue）

- 顶部：关联订单信息（订单号）
- 商品选择区：展示该订单所有商品项，每项有勾选框，用户选择要退的商品
- 售后原因：下拉选择（质量问题/不想要了/发错货/其他）+ 自定义输入
- 问题描述：文本输入框（选填）
- 凭证图片：上传区域，最多 3 张（选填）
- 底部：退款金额（自动计算所选商品 totalPrice 之和）+ 提交按钮

### 售后详情页（AfterSaleDetail.vue）

- 售后状态标签（待处理/已通过/已驳回/已完成）
- 关联订单信息
- 售后原因、描述、凭证图片
- 退款金额
- 商家处理备注（如有）
- 操作按钮：待处理状态可「取消售后」

### 个人中心「售后处理」列表

- 展示当前用户所有售后单
- 每条显示：售后状态标签、退款金额、商品缩略信息、申请时间
- 点击跳转 AfterSaleDetail

## 后端模块结构

遵循项目现有的 Entity/DTO/VO/Mapper/Service/Controller 三层架构。

### 新增文件

```
backend/ecommerce-pojo/src/main/java/com/ecommerce/
├── entity/AfterSale.java
├── entity/AfterSaleItem.java
├── dto/AfterSaleDTO.java
└── vo/AfterSaleVO.java
    vo/AfterSaleItemVO.java

backend/ecommerce-core/src/main/java/com/ecommerce/
├── mapper/AfterSaleMapper.java
├── mapper/AfterSaleItemMapper.java
├── service/AfterSaleService.java
├── service/impl/AfterSaleServiceImpl.java
└── Controller/User/AfterSaleController.java
```

### 修改文件

- `OrderController.java` — 新增 confirmOrder 方法
- `OrderService.java` — 新增 confirmOrder 接口
- `OrderServiceImpl.java` — 实现确认收货逻辑
- `db_init.sql` — 新增 after_sale、after_sale_item 建表语句，orders 表加字段

## 实现范围

本次实现：
- 数据库建表（after_sale、after_sale_item、orders 加字段）
- 后端售后接口（创建、列表、详情、取消）
- 后端确认收货接口
- 前端售后申请页、售后详情页
- 前端个人中心售后列表、订单列表按钮
- 前端订单详情页按钮

本次不实现：
- 商家端管理页面（仅预留 API 接口定义）
- 图片上传功能（前端保留上传 UI，图片以 URL 字符串存储）
- 真实退款逻辑（模拟审批通过即完成）
