# API 接口文档

所有接口返回统一格式：`{ code: 1, msg: "success", data: {...} }`

## Swagger 文档

项目集成了 Springdoc OpenAPI，启动后端后访问以下地址查看自动生成的接口文档：

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

Swagger UI 支持在线调试，可以直接在页面上测试 API 接口。

---

## 用户模块 `/user`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/login` | 用户登录（返回 JWT token） |
| POST | `/user/register` | 用户注册 |
| GET | `/user/info?userId=` | 获取用户信息 |
| GET | `/user/current` | 获取当前登录用户（需 Bearer token） |
| POST | `/user/logout` | 退出登录 |
| PUT | `/user/update` | 更新用户资料 |

## 商品模块 `/product`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/product/list` | 商品列表 |
| GET | `/product/{id}` | 商品详情 |
| GET | `/product/category/{categoryId}` | 按分类查询 |
| GET | `/product/hot-sales` | 热销榜单 TOP10 |

## 分类模块 `/category`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/category/list` | 分类列表 |

## 购物车模块 `/cart`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/cart/add` | 加入购物车 |
| GET | `/cart/list?userId=` | 购物车列表 |
| PUT | `/cart/update` | 更新数量 |
| DELETE | `/cart/delete?id=` | 删除单项 |
| DELETE | `/cart/batch-delete` | 批量删除 |
| PUT | `/cart/check` | 勾选/取消勾选 |
| PUT | `/cart/check-all` | 全选/取消全选 |
| GET | `/cart/count?userId=` | 购物车数量 |

## 订单模块 `/order`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/order/create` | 创建订单 |
| GET | `/order/list?userId=&status=` | 订单列表 |
| GET | `/order/detail?orderId=&userId=` | 订单详情 |
| PUT | `/order/cancel?orderId=&userId=` | 取消订单 |
| POST | `/order/pay` | 模拟支付 |
| POST | `/order/coupons/available` | 查询可用优惠券 |
| PUT | `/order/confirm?orderId=&userId=` | 确认收货 |

## 优惠券模块 `/coupon` & `/userCoupon`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/coupon/list` | 优惠券列表 |
| POST | `/userCoupon/receive` | 领取优惠券 |
| GET | `/userCoupon/list?userId=` | 我的优惠券 |

## 收藏模块 `/favorite`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/favorite` | 收藏商品 |
| DELETE | `/favorite?userId=&productId=` | 取消收藏 |
| GET | `/favorite/list?userId=` | 收藏列表 |
| GET | `/favorite/check?userId=&productId=` | 检查是否收藏 |

## 地址模块 `/address`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/address/list?userId=` | 地址列表 |
| GET | `/address/default?userId=` | 默认地址 |
| GET | `/address/{id}` | 地址详情 |
| POST | `/address` | 新增地址 |
| PUT | `/address` | 更新地址 |
| PUT | `/address/default/{id}?userId=` | 设为默认 |
| DELETE | `/address/{id}` | 删除地址 |

## 评论模块 `/comment`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/comment` | 发表评论 |
| DELETE | `/comment?id=&userId=` | 删除评论 |
| GET | `/comment/list?productId=` | 商品评论列表 |
| GET | `/comment/user?userId=` | 用户评论列表 |
| GET | `/comment/stats?productId=` | 商品评论统计（平均分、评论数） |

**发表评论请求体：**
```json
{
  "userId": 1,
  "productId": 2,
  "orderId": 5,
  "rating": 5,
  "content": "商品质量很好，非常满意！",
  "images": "https://example.com/img1.jpg,https://example.com/img2.jpg"
}
```

**评论统计响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [4.8, 120]
}
```
`data[0]` 为平均评分，`data[1]` 为评论总数。

## 售后模块 `/after-sale`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/after-sale` | 提交售后申请 |
| GET | `/after-sale/list?userId=` | 用户售后列表 |
| GET | `/after-sale/detail?id=` | 售后单详情 |
| PUT | `/after-sale/cancel?id=&userId=` | 取消售后 |

---

## 智能客服模块 `/shop/customer-service`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shop/customer-service/process` | 发送消息给 AI 客服 |

Agent 服务基于 **LangChain ReAct Agent** 模式，LLM 自主决策调用工具回答用户问题。

### 工具列表

| 工具 | 功能 | 数据源 |
|------|------|--------|
| `get_orderStatus` | 查询订单状态 | MySQL |
| `get_userOrders` | 查询用户所有订单 | MySQL |
| `get_user_coupons` | 查询用户优惠券 | MySQL |
| `get_user_addresses` | 查询用户收货地址 | MySQL |
| `get_user_favorites` | 查询用户收藏 | MySQL |
| `get_user_cart` | 查询用户购物车 | MySQL |
| `search_products` | 语义搜索推荐商品 | ChromaDB (RAG) |
| `get_product_detail` | 查询商品详情 | MySQL |

### RAG 流程

```
用户提问 → ZhipuAI Embedding 向量化 → ChromaDB 语义检索 (top-K=5, threshold=0.4)
    → 返回匹配商品 → MySQL 查询商品详情 → LLM 生成回答
```

---

## 商家端 API

### 商家信息 `/shop/merchant`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/merchant/info` | 获取商家信息 |

### 数据总览 `/shop/dashboard`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/dashboard/sales-trend` | 近 7 天销售趋势 |
| GET | `/shop/dashboard/order-status` | 订单状态分布统计 |
| GET | `/shop/dashboard/top-products` | 热销商品 TOP10 |

### 商品管理 `/shop/product`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/product/list` | 商品列表（支持分页、搜索、筛选） |
| GET | `/shop/product/detail?productId=` | 商品详情 |
| POST | `/shop/product/add` | 新增商品 |
| PUT | `/shop/product/update` | 修改商品信息 |
| DELETE | `/shop/product/delete?productId=` | 删除商品 |
| PUT | `/shop/product/status` | 上下架商品 |
| PUT | `/shop/product/stock` | 修改库存 |

### 订单管理 `/shop/order`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/order/list` | 订单列表（支持分页、状态筛选） |
| GET | `/shop/order/detail?orderId=` | 订单详情 |
| PUT | `/shop/order/ship` | 订单发货 |

### 售后管理 `/shop/after-sale`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shop/after-sale/list` | 售后列表（支持分页、状态筛选） |
| PUT | `/shop/after-sale/handle` | 处理售后（同意/拒绝 + 备注） |

---

## 数据库设计

数据库名 `db_aps`，共 13 张表：

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `user` | 用户表 | username, password, nickname, phone, email, avatar, gender |
| `category` | 商品分类表 | name, sort, status |
| `product` | 商品表 | name, price, stock, sales, categoryId, mainImage |
| `cart` | 购物车表 | userId, productId, quantity, price, isChecked |
| `orders` | 订单表 | orderNo, userId, status, totalAmount, payAmount, 收货信息 |
| `order_item` | 订单商品表 | orderId, productId, price, quantity, totalPrice |
| `coupon` | 优惠券表 | categoryId, minSpend, discountAmount, stock, 时间窗口 |
| `user_coupon` | 用户优惠券表 | userId, couponId, status, expireTime |
| `order_coupon` | 订单优惠券明细 | orderId, couponId, discountAmount |
| `address` | 收货地址表 | userId, name, phone, 省市区, detailAddress, isDefault |
| `favorite` | 收藏表 | userId, productId |
| `comment` | 商品评论表 | userId, productId, orderId, rating, content |
| `merchant` | 商家表 | name, phone, description, logo, status |

**订单状态流转：**
```
待付款(0) ──支付──▶ 待发货(1) ──发货──▶ 待收货(2) ──确认收货──▶ 已完成(3)
    │                                        │
    └──── 取消 ◀─────────────────────────────┘
                                              ▼
                                        已取消(4)
```
