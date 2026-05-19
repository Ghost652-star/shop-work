# API 接口文档

## 统一响应格式

所有接口返回：

```json
{
  "code": 1,
  "msg": "success",
  "data": { ... }
}
```

- `code` = 1 表示成功，0 表示失败
- 失败时 `data` 为 null，`msg` 包含错误信息

---

## Swagger 文档

启动后端后访问：
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 用户模块 `/user`

### POST `/user/login` — 用户登录

**请求体：**
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "user": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "zhangsan",
      "phone": "138****1234",
      "avatar": "https://picsum.photos/100/100?random=1",
      "gender": 0
    },
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### POST `/user/register` — 用户注册

**请求体：**
```json
{
  "username": "zhangsan",
  "password": "123456",
  "phone": "13800138000"
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "zhangsanaaa",
    "phone": "13800138000",
    "avatar": "https://picsum.photos/100/100?random=567",
    "gender": 0
  }
}
```

### GET `/user/info?userId=1` — 获取用户信息

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "zhangsanaaa",
    "phone": "138****8000",
    "avatar": "https://picsum.photos/100/100?random=567",
    "gender": 0
  }
}
```

### GET `/user/current` — 获取当前登录用户

需 Header: `Authorization: Bearer <token>`

**响应：** 同 `/user/info`

### POST `/user/logout` — 退出登录

**响应：**
```json
{ "code": 1, "msg": "success", "data": null }
```

### PUT `/user/update` — 更新用户资料

**请求体：**
```json
{
  "id": 1,
  "nickname": "新昵称",
  "phone": "13900139000",
  "email": "zhangsan@example.com",
  "avatar": "https://example.com/avatar.jpg",
  "gender": 1
}
```

**响应：** 返回更新后的 UserVO

---

## 商品模块 `/product`

### GET `/product/list` — 商品列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "无线蓝牙耳机",
      "description": "高品质降噪耳机",
      "price": 199.00,
      "sales": 520,
      "mainImage": "https://example.com/product1.jpg",
      "categoryId": 1
    }
  ]
}
```

### GET `/product/{id}` — 商品详情

**响应：** 单个 ProductVO（同上结构）

### GET `/product/category/{categoryId}` — 按分类查询

**响应：** `List<ProductVO>`

### GET `/product/hot-sales` — 热销榜单 TOP10

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "rank": 1,
      "productId": 3,
      "name": "无线蓝牙耳机",
      "sales": 520
    },
    {
      "rank": 2,
      "productId": 7,
      "name": "机械键盘",
      "sales": 380
    }
  ]
}
```

---

## 分类模块 `/category`

### GET `/category/list` — 分类列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "id": 1, "name": "电子产品" },
    { "id": 2, "name": "服装" }
  ]
}
```

---

## 购物车模块 `/cart`

### POST `/cart/add` — 加入购物车

**请求体：**
```json
{
  "userId": 1,
  "productId": 3,
  "quantity": 1
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "productId": 3,
    "productName": "无线蓝牙耳机",
    "productImage": "https://example.com/product1.jpg",
    "price": 199.00,
    "quantity": 1,
    "isChecked": 1,
    "createTime": "2026-05-19T10:00:00",
    "updateTime": "2026-05-19T10:00:00",
    "subtotal": 199.00
  }
}
```

### GET `/cart/list?userId=1` — 购物车列表

**响应：** `List<CartVO>`（结构同上）

### PUT `/cart/update` — 更新数量

**请求体：**
```json
{
  "id": 1,
  "quantity": 3
}
```

**响应：** 返回更新后的 CartVO

### DELETE `/cart/delete?id=1` — 删除单项

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### DELETE `/cart/batch-delete` — 批量删除

**请求体：**
```json
{
  "ids": [1, 2, 3]
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/cart/check` — 勾选/取消勾选

**请求体：**
```json
{
  "id": 1,
  "isChecked": 0
}
```

**响应：** 返回更新后的 CartVO

### PUT `/cart/check-all` — 全选/取消全选

**请求体：**
```json
{
  "userId": 1,
  "isChecked": 1
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/cart/count?userId=1` — 购物车数量

**响应：**
```json
{ "code": 1, "msg": "success", "data": 5 }
```

---

## 订单模块 `/order`

### POST `/order/create` — 创建订单

**请求体：**
```json
{
  "userId": 1,
  "addressId": 1,
  "couponIds": [1],
  "remark": "请尽快发货",
  "cartItemIds": [1, 2],
  "items": [
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD20260519100000123",
    "userId": 1,
    "status": 0,
    "statusText": "待付款",
    "totalAmount": 199.00,
    "freightAmount": 5.00,
    "couponAmount": 20.00,
    "payAmount": 184.00,
    "paymentType": null,
    "paymentTime": null,
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "广东省深圳市南山区xxx",
    "remark": "请尽快发货",
    "afterSaleStatus": 0,
    "createTime": "2026-05-19T10:00:00",
    "items": [
      {
        "id": 1,
        "productId": 3,
        "productName": "无线蓝牙耳机",
        "productImage": "https://example.com/product1.jpg",
        "categoryId": 1,
        "price": 199.00,
        "quantity": 1,
        "totalPrice": 199.00
      }
    ],
    "coupons": [
      {
        "id": 1,
        "couponId": 1,
        "description": "满100减20",
        "categoryId": null,
        "discountAmount": 20.00
      }
    ]
  }
}
```

### GET `/order/list?userId=1&status=0` — 订单列表

`status` 可选：0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消

**响应：** `List<OrderVO>`（结构同上）

### GET `/order/detail?orderId=1&userId=1` — 订单详情

**响应：** 单个 OrderVO

### PUT `/order/cancel?orderId=1&userId=1` — 取消订单

**响应：**
```json
{ "code": 1, "msg": "success", "data": true }
```

### POST `/order/pay` — 模拟支付

**请求体：**
```json
{
  "orderId": 1,
  "userId": 1,
  "paymentType": "alipay"
}
```

**响应：**
```json
{ "code": 1, "msg": "success", "data": true }
```

### POST `/order/coupons/available` — 查询可用优惠券

**请求体：**
```json
{
  "userId": 1,
  "items": [
    {
      "productId": 3,
      "categoryId": 1,
      "price": 199.00,
      "quantity": 1
    }
  ]
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "available": [
      {
        "userCouponId": 1,
        "couponId": 1,
        "description": "满100减20",
        "minSpend": 100.00,
        "discountAmount": 20.00,
        "categoryId": null,
        "expireTime": "2026-06-19T00:00:00",
        "actualDiscount": 20.00
      }
    ],
    "unavailable": [
      {
        "userCouponId": 2,
        "description": "满500减100",
        "reason": "金额不满足最低消费"
      }
    ],
    "totalAmount": 199.00,
    "maxDiscount": 20.00
  }
}
```

### PUT `/order/confirm?orderId=1&userId=1` — 确认收货

**响应：**
```json
{ "code": 1, "msg": "success", "data": true }
```

---

## 优惠券模块 `/coupon` & `/userCoupon`

### GET `/coupon/list` — 优惠券列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "description": "满100减20",
      "categoryId": null,
      "categoryName": null,
      "minSpend": 100.00,
      "discountAmount": 20.00,
      "startTime": "2026-05-01T00:00:00",
      "endTime": "2026-06-01T00:00:00",
      "validPeriod": 30,
      "stock": 100,
      "image": "https://example.com/coupon.png",
      "status": 1,
      "countdown": "12:30:45"
    }
  ]
}
```

### POST `/userCoupon/receive` — 领取优惠券

**请求体：**
```json
{
  "userId": 1,
  "couponId": 1
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "couponId": 1,
    "description": "满100减20",
    "minSpend": 100.00,
    "discountAmount": 20.00,
    "status": 0,
    "orderId": null,
    "getTime": "2026-05-19T10:00:00",
    "useTime": null,
    "expireTime": "2026-06-18T10:00:00"
  }
}
```

### GET `/userCoupon/list?userId=1` — 我的优惠券

**响应：** `List<UserCouponVO>`（结构同上）

---

## 收藏模块 `/favorite`

### POST `/favorite` — 收藏商品

**请求体：**
```json
{
  "userId": 1,
  "productId": 3
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### DELETE `/favorite?userId=1&productId=3` — 取消收藏

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/favorite/list?userId=1` — 收藏列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "productId": 3,
      "createTime": "2026-05-19 10:00:00"
    }
  ]
}
```

### GET `/favorite/check?userId=1&productId=3` — 检查是否收藏

**响应：**
```json
{ "code": 1, "msg": "success", "data": true }
```

---

## 地址模块 `/address`

### GET `/address/list?userId=1` — 地址列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "name": "张三",
      "phone": "13800138000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detailAddress": "科技园路1号",
      "isDefault": 1,
      "createTime": "2026-05-19T10:00:00",
      "updateTime": "2026-05-19T10:00:00"
    }
  ]
}
```

### GET `/address/default?userId=1` — 默认地址

**响应：** 单个 AddressVO

### GET `/address/{id}` — 地址详情

**响应：** 单个 AddressVO

### POST `/address` — 新增地址

**请求体：**
```json
{
  "userId": 1,
  "name": "张三",
  "phone": "13800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园路1号",
  "isDefault": 0
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/address` — 更新地址

**请求体：** 同 POST，需包含 `id`

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/address/default/{id}?userId=1` — 设为默认

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### DELETE `/address/{id}` — 删除地址

**响应：** `{ "code": 1, "msg": "success", "data": null }`

---

## 评论模块 `/comment`

### POST `/comment` — 发表评论

**请求体：**
```json
{
  "userId": 1,
  "productId": 3,
  "orderId": 1,
  "rating": 5,
  "content": "商品质量很好，非常满意！",
  "images": "https://example.com/img1.jpg,https://example.com/img2.jpg"
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### DELETE `/comment?id=1&userId=1` — 删除评论

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/comment/list?productId=3` — 商品评论列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "username": "zhangsan",
      "productId": 3,
      "orderId": 1,
      "rating": 5,
      "content": "商品质量很好，非常满意！",
      "images": "https://example.com/img1.jpg,https://example.com/img2.jpg",
      "createTime": "2026-05-19 10:00:00"
    }
  ]
}
```

### GET `/comment/user?userId=1` — 用户评论列表

**响应：** `List<CommentVO>`（同上）

### GET `/comment/stats?productId=3` — 商品评论统计

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [4.8, 120]
}
```

`data[0]` = 平均评分，`data[1]` = 评论总数

---

## 售后模块 `/after-sale`

### POST `/after-sale` — 提交售后申请

**请求体：**
```json
{
  "userId": 1,
  "orderId": 1,
  "reason": "质量问题",
  "description": "耳机有杂音",
  "images": "https://example.com/defect1.jpg",
  "orderItemIds": [1]
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/after-sale/list?userId=1` — 用户售后列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD20260519100000123",
      "orderId": 1,
      "reason": "质量问题",
      "description": "耳机有杂音",
      "images": "https://example.com/defect1.jpg",
      "refundAmount": 199.00,
      "status": 0,
      "statusText": "待处理",
      "adminRemark": null,
      "createTime": "2026-05-19 10:00:00",
      "items": [
        {
          "orderItemId": 1,
          "productId": 3,
          "productName": "无线蓝牙耳机",
          "productImage": "https://example.com/product1.jpg",
          "price": 199.00,
          "quantity": 1,
          "totalPrice": 199.00
        }
      ]
    }
  ]
}
```

### GET `/after-sale/detail?id=1` — 售后单详情

**响应：** 单个 AfterSaleVO

### PUT `/after-sale/cancel?id=1&userId=1` — 取消售后

**响应：** `{ "code": 1, "msg": "success", "data": null }`

---

## 智能客服模块 `/shop/customer-service`

### POST `/shop/customer-service/process` — 发送消息给 AI 客服

**请求体：**
```json
{
  "message": "帮我查一下我的订单状态",
  "user_id": "1"
}
```

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "reply": "您有一笔待付款订单...",
    "tool_calls": [...]
  }
}
```

### Agent 工具列表

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

---

## 商家端 API

### GET `/shop/merchant/info` — 获取商家信息

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "FlowShop旗舰店",
    "phone": "13800138000",
    "description": "官方旗舰店",
    "logo": "https://example.com/logo.png",
    "status": 1,
    "createTime": "2026-05-01T00:00:00",
    "updateTime": "2026-05-19T00:00:00"
  }
}
```

### GET `/shop/dashboard/sales-trend` — 近7天销售趋势

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "dates": ["05-13", "05-14", "05-15", "05-16", "05-17", "05-18", "05-19"],
    "amounts": [1200.00, 890.00, 2100.00, 1500.00, 3200.00, 2800.00, 1900.00]
  }
}
```

### GET `/shop/dashboard/order-status` — 订单状态分布

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "name": "待付款", "value": 5 },
    { "name": "待发货", "value": 12 },
    { "name": "待收货", "value": 8 },
    { "name": "已完成", "value": 45 },
    { "name": "已取消", "value": 3 }
  ]
}
```

### GET `/shop/dashboard/top-products` — 热销商品 TOP10

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    { "name": "无线蓝牙耳机", "sales": 520 },
    { "name": "机械键盘", "sales": 380 }
  ]
}
```

### GET `/shop/product/list?page=1&size=10&name=&status=&categoryId=` — 商品列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "无线蓝牙耳机",
        "price": 199.00,
        "stock": 200,
        "sales": 520,
        "categoryName": "电子产品",
        "mainImage": "https://example.com/product1.jpg",
        "status": 1
      }
    ],
    "total": 47,
    "page": 1,
    "size": 10
  }
}
```

### GET `/shop/product/detail?productId=1` — 商品详情

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "无线蓝牙耳机",
    "description": "高品质降噪耳机",
    "price": 199.00,
    "stock": 200,
    "sales": 520,
    "categoryId": 1,
    "categoryName": "电子产品",
    "mainImage": "https://example.com/product1.jpg",
    "status": 1
  }
}
```

### POST `/shop/product/add` — 新增商品

**请求体：**
```json
{
  "name": "新商品",
  "description": "商品描述",
  "price": 99.00,
  "stock": 100,
  "categoryId": 1,
  "mainImage": "https://example.com/new.jpg"
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/shop/product/update` — 修改商品

**请求体：** 同 POST，需包含 `id`

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### DELETE `/shop/product/delete?productId=1` — 删除商品

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/shop/product/status` — 上下架

**请求体：**
```json
{
  "productId": 1,
  "status": 1
}
```

`status`: 0=下架, 1=上架

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### PUT `/shop/product/stock` — 修改库存

**请求体：**
```json
{
  "productId": 1,
  "stock": 300
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/shop/order/list?page=1&size=10&status=` — 订单列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20260519100000123",
        "userName": "zhangsan",
        "totalAmount": 199.00,
        "payAmount": 184.00,
        "status": 0,
        "statusText": "待付款",
        "createTime": "2026-05-19T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

### GET `/shop/order/detail?orderId=1` — 订单详情

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD20260519100000123",
    "userName": "zhangsan",
    "totalAmount": 199.00,
    "payAmount": 184.00,
    "status": 0,
    "statusText": "待付款",
    "createTime": "2026-05-19T10:00:00",
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "广东省深圳市南山区xxx",
    "remark": "请尽快发货",
    "items": [
      {
        "id": 1,
        "productId": 3,
        "productName": "无线蓝牙耳机",
        "productImage": "https://example.com/product1.jpg",
        "categoryId": 1,
        "price": 199.00,
        "quantity": 1,
        "totalPrice": 199.00
      }
    ]
  }
}
```

### PUT `/shop/order/ship` — 订单发货

**请求体：**
```json
{
  "orderId": 1
}
```

**响应：** `{ "code": 1, "msg": "success", "data": null }`

### GET `/shop/after-sale/list?page=1&size=10&status=` — 售后列表

**响应：**
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20260519100000123",
        "userName": "zhangsan",
        "reason": "质量问题",
        "refundAmount": 199.00,
        "status": 0,
        "statusText": "待处理",
        "createTime": "2026-05-19T10:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

### PUT `/shop/after-sale/handle` — 处理售后

**请求体：**
```json
{
  "afterSaleId": 1,
  "status": 1,
  "adminRemark": "同意退款，已处理"
}
```

`status`: 1=通过, 2=驳回

**响应：** `{ "code": 1, "msg": "success", "data": null }`

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
