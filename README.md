# Traework - AI 电商平台

一个基于前后端分离 + AI 智能客服的全栈电商平台，采用 **Vue 3 + Spring Boot + Python FastAPI** 三层架构，集成 LangChain + RAG 实现智能客服问答。

---

## 项目架构

```
┌─────────────────┐     ┌─────────────────────┐     ┌──────────────────────┐
│    Frontend      │     │      Backend         │     │      Agent           │
│   Vue 3 + Vite   │────▶│  Spring Boot 2.7     │────▶│  FastAPI + LangChain │
│   Element Plus   │     │  MyBatis-Plus        │     │  ChromaDB (RAG)      │
│   Port: 5173     │     │  Port: 8080          │     │  Port: 8000          │
└─────────────────┘     └─────────────────────┘     └──────────────────────┘
                              │                              │
                              ▼                              ▼
                        ┌──────────┐                  ┌──────────┐
                        │  MySQL   │                  │ ChromaDB │
                        │  db_aps  │                  │ 向量存储  │
                        └──────────┘                  └──────────┘
```

**数据流：** 用户请求 → Vue 前端 → Spring Boot 后端 → (需要 AI 时) → Python Agent → LLM + 工具调用 → 返回结果

---

## 技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **前端** | Vue 3 | 3.4+ | Composition API / Options API |
| | Vite | 5.0+ | 构建工具 |
| | Vue Router | 4.2+ | 路由管理 |
| | Element Plus | 2.5+ | UI 组件库 |
| | Axios | 1.6+ | HTTP 客户端 |
| **后端** | Java | 1.8 | 编程语言 |
| | Spring Boot | 2.7.15 | 应用框架 |
| | MyBatis-Plus | 3.5.3.2 | ORM 框架 |
| | Maven | - | 多模块构建工具 |
| | Spring WebFlux | 2.7.15 | WebClient 调用 Agent 服务 |
| **Agent** | Python | 3.8+ | 编程语言 |
| | FastAPI | 0.115+ | Web 框架 |
| | LangChain | 0.3.7+ | AI Agent 框架 |
| | ChromaDB | - | 向量数据库 (RAG) |
| | ZhipuAI Embedding | embedding-3 | 文本向量化 |
| | MiniMax-M2.5 | - | 大语言模型 |
| **数据库** | MySQL | 8.0+ | 主数据库 (12 张表) |

---

## 目录结构

```
Traework/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── api/                 # API 接口模块 (9 个)
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 公共组件 (CartSidebar)
│   │   ├── data/                # 数据文件 (省市区)
│   │   ├── router/              # 路由配置
│   │   ├── styles/              # 样式 (CSS 变量/设计令牌)
│   │   ├── utils/               # 工具 (Axios 封装)
│   │   └── views/               # 页面组件 (8 个)
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── backend/                     # 后端项目 (Maven 多模块)
│   ├── pom.xml                  # 父 POM
│   ├── ecommerce-pojo/          # 数据模型层 (Entity/DTO/VO)
│   ├── ecommerce-common/        # 公共层 (异常处理)
│   └── ecommerce-core/          # 核心业务层
│       └── src/main/java/com/ecommerce/
│           ├── Controller/      # 控制器 (11 个)
│           ├── service/         # 服务接口 (9 个)
│           │   └── impl/        # 服务实现
│           ├── mapper/          # 数据访问层 (11 个)
│           ├── entity/          # 实体类
│           ├── config/          # 配置类
│           └── exception/       # 自定义异常
│
├── agent/                       # AI 客服服务
│   ├── app/
│   │   ├── serviceClient.py     # Agent 核心
│   │   ├── callbacks.py         # 调试回调
│   │   └── connect.py           # FastAPI 路由
│   ├── config/                  # 配置模块
│   │   ├── settings.py          # 统一配置管理
│   │   └── database.py          # 数据库连接池
│   ├── rag/                     # RAG 模块
│   │   ├── vector_store.py      # 向量存储服务
│   │   └── rag_service.py       # RAG 检索服务
│   ├── tools/                   # 工具模块
│   │   ├── order_tools.py       # 订单工具 (2 个)
│   │   ├── user_tools.py        # 用户工具 (4 个)
│   │   └── product_tools.py     # 商品工具 (2 个)
│   ├── prompts/                 # 提示词目录
│   │   └── system_prompt.txt    # 系统提示词
│   ├── data/product.csv         # 商品种子数据 (47 条)
│   ├── chroma_data/             # ChromaDB 持久化
│   ├── requirements.txt         # Python 依赖
│   └── .env                     # 环境变量
│
└── db_init.sql                  # 数据库初始化脚本
```

---

## 快速开始

### 环境要求

- **JDK** 1.8+
- **Node.js** 16+
- **Python** 3.8+
- **MySQL** 8.0+

### 1. 初始化数据库

```bash
mysql -u root -p < db_init.sql
```

这会创建 `db_aps` 数据库及全部 12 张表。

### 2. 启动后端

```bash
cd backend

# 修改数据库配置 (如有需要)
# ecommerce-core/src/main/resources/application.yml

# 编译并运行
mvn clean package -DskipTests
cd ecommerce-core
mvn spring-boot:run
```

后端启动后监听 `http://localhost:8080`。

### 3. 启动 Agent 服务

```bash
cd agent

# 创建虚拟环境
python -m venv venv
source venv/bin/activate   # Windows: venv\Scripts\activate

# 安装依赖
pip install -r requirements.txt

# 配置环境变量
cp .env.example .env
# 编辑 .env 填入 MySQL 连接信息

# 启动服务
uvicorn app.connect:app --host 127.0.0.1 --port 8000 --reload
```

Agent 服务启动后监听 `http://127.0.0.1:8000`。

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问 `http://localhost:5173`。

---

## 数据库设计

数据库名 `db_aps`，共 12 张表：

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

**订单状态流转：**
```
待付款(0) ──支付──▶ 待发货(1) ──发货──▶ 待收货(2) ──确认收货──▶ 已完成(3)
    │                                        │
    └──── 取消 ◀─────────────────────────────┘
                                              ▼
                                        已取消(4)
```

---

## API 接口

所有接口返回统一格式：`{ code: 1, msg: "success", data: {...} }`

### 用户模块 `/user`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/login` | 用户登录 |
| POST | `/user/register` | 用户注册 |
| GET | `/user/info?userId=` | 获取用户信息 |
| POST | `/user/logout` | 退出登录 |
| PUT | `/user/update` | 更新用户资料 |

### 商品模块 `/product`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/product/list` | 商品列表 |
| GET | `/product/{id}` | 商品详情 |
| GET | `/product/category/{categoryId}` | 按分类查询 |

### 分类模块 `/category`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/category/list` | 分类列表 |

### 购物车模块 `/cart`

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

### 订单模块 `/order`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/order/create` | 创建订单 |
| GET | `/order/list?userId=&status=` | 订单列表 |
| GET | `/order/detail?orderId=&userId=` | 订单详情 |
| PUT | `/order/cancel?orderId=&userId=` | 取消订单 |
| POST | `/order/pay` | 模拟支付 |
| POST | `/order/coupons/available` | 查询可用优惠券 |

### 优惠券模块 `/coupon` & `/userCoupon`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/coupon/list` | 优惠券列表 |
| POST | `/userCoupon/receive` | 领取优惠券 |
| GET | `/userCoupon/list?userId=` | 我的优惠券 |

### 收藏模块 `/favorite`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/favorite` | 收藏商品 |
| DELETE | `/favorite?userId=&productId=` | 取消收藏 |
| GET | `/favorite/list?userId=` | 收藏列表 |
| GET | `/favorite/check?userId=&productId=` | 检查是否收藏 |

### 地址模块 `/address`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/address/list?userId=` | 地址列表 |
| GET | `/address/default?userId=` | 默认地址 |
| GET | `/address/{id}` | 地址详情 |
| POST | `/address` | 新增地址 |
| PUT | `/address` | 更新地址 |
| PUT | `/address/default/{id}?userId=` | 设为默认 |
| DELETE | `/address/{id}` | 删除地址 |

### 评论模块 `/comment`

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

### 售后模块 `/after-sale`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/after-sale` | 提交售后申请 |
| GET | `/after-sale/list?userId=` | 用户售后列表 |
| GET | `/after-sale/detail?id=` | 售后单详情 |
| PUT | `/after-sale/cancel?id=&userId=` | 取消售后 |

### 订单模块（补充）

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/order/confirm?orderId=&userId=` | 确认收货 |

### 智能客服模块 `/shop/customer-service`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shop/customer-service/process` | 发送消息给 AI 客服 |

---

## AI 智能客服

Agent 服务基于 **LangChain ReAct Agent** 模式，LLM 自主决策调用哪些工具来回答用户问题。

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

### Agent 接口

```bash
# 发送消息
curl -X POST http://127.0.0.1:8000/process \
  -H "Content-Type: application/json" \
  -d '{"message": "帮我查一下我的订单状态", "user_id": "1"}'
```

---

## 前端页面

| 页面 | 路由 | 功能 |
|------|------|------|
| 首页 | `/` | 分类导航、轮播图、热卖 TOP10、秒杀倒计时、商品瀑布流、登录/注册弹窗 |
| 商品详情 | `/product?id=` | 商品图片、规格选择、数量控制、加入购物车/立即购买、收藏、评论 |
| 个人中心 | `/personal?tab=` | 订单管理、收藏夹、优惠券、地址管理、个人资料 |
| 订单确认 | `/order-confirm` | 地址选择、商品清单、优惠券选择、备注、提交订单 |
| 支付页面 | `/payment/:id` | 支付方式选择、模拟二维码、倒计时、支付结果 |
| 订单详情 | `/order/detail?id=` | 订单状态、收货地址、商品列表、金额明细、操作按钮 |
| 优惠券中心 | `/coupon-seckill` | 分类标签、优惠券列表、领取按钮 |
| 智能客服 | `/customer-service` | 联系人列表、消息对话框、实时问答 |

---

## 配置说明

### 后端配置 (`application.yml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_aps
    username: root
    password: your_password
```

### Agent 配置 (`.env`)

```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=db_aps
LLM_API_KEY=your_llm_api_key
ZHIPUAI_API_KEY=your_zhipuai_api_key
```

Agent 配置文件位于 `config/settings.py`，包含：
- **LLM 配置** — 模型名称、API Key
- **Embedding 配置** — 模型名称、向量库参数
- **数据库配置** — 连接池参数

### 前端代理 (`vite.config.js`)

```js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

---

## 开发说明

- 后端遵循 **Controller → Service → Mapper** 三层架构
- 前端 API 封装在 `src/utils/request.js`，基于 Axios 拦截器统一处理响应
- 设计令牌定义在 `src/styles/variables.css`，主色调为 `#E53935`（红色系）
- 组件库使用 Element Plus，其余 UI 自定义实现
- 用户认证通过 localStorage 存储 `userId`，无 JWT/Session 机制
