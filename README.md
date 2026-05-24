# FlowShop - AI 电商平台

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

**商家端：** Vue 3 商家管理后台 → Spring Boot 商家 API → MySQL（数据总览、商品管理、订单管理、售后管理）

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
| | Springdoc OpenAPI | 1.6.15 | API 文档自动生成 (Swagger UI) |
| | Redis | - | 缓存（热销榜单、商品名映射、用户信息等） |
| **Agent** | Python | 3.8+ | 编程语言 |
| | FastAPI | 0.115+ | Web 框架 |
| | LangChain | 0.3.7+ | AI Agent 框架 |
| | ChromaDB | - | 向量数据库 (RAG) |
| | ZhipuAI Embedding | embedding-3 | 文本向量化 |
| | MiniMax-M2.5 | - | 大语言模型 |
| **数据库** | MySQL | 8.0+ | 主数据库 (13 张表) |

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

### 2. 启动后端

```bash
cd backend
$env:JAVA_HOME="E:\jdk1.8"
$env:PATH="E:\jdk1.8\bin;" + $env:PATH
mvn clean install -DskipTests
cd ecommerce-core
mvn spring-boot:run
```

后端启动后监听 `http://localhost:8080`。

### 3. 启动 Agent 服务

```bash
cd agent
python -m venv venv
source venv/bin/activate   # Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env       # 编辑 .env 填入 MySQL 连接信息
uvicorn app.connect:app --host 127.0.0.1 --port 8000 --reload
```

### 4. 启动用户端前端

```bash
cd user-frontend
npm install
npm run dev    # http://localhost:5173
```

### 5. 启动商家端前端

```bash
cd shop-frontend
npm install
npm run dev    # http://localhost:5174
```

---

---

## 项目页面

### 用户端（端口 5173）

| 页面 | 路由 | 功能 |
|------|------|------|
| 首页 | `/` | 分类导航、轮播图、热卖 TOP10、秒杀倒计时、商品瀑布流 |
| 商品详情 | `/product?id=` | 商品图片、规格选择、加入购物车/立即购买、收藏、评论 |
| 个人中心 | `/personal?tab=` | 订单管理、收藏夹、优惠券、地址管理、个人资料 |
| 订单确认 | `/order-confirm` | 地址选择、按商家分组商品清单、优惠券选择、备注、提交订单 |
| 支付页面 | `/payment/:batchNo` | 按批次号批量加载多个商家订单、支付方式选择、确认支付 |
| 订单详情 | `/order/detail?id=` | 订单状态、收货地址、商品列表、金额明细、操作按钮 |
| 优惠券中心 | `/coupon-seckill` | 分类标签、优惠券列表、领取按钮 |
| 智能客服 | `/customer-service` | 联系人列表、消息对话框、实时问答 |

### 商家端（端口 5174）

| 页面 | 路由 | 功能 |
|------|------|------|
| 数据总览 | `/` | ECharts 图表（销售趋势、订单分布、热销排行） |
| 商品管理 | `/product` | 商品列表、搜索筛选、上下架、库存管理 |
| 订单管理 | `/order` | 订单列表、状态筛选、详情查看、发货操作 |
| 售后管理 | `/after-sale` | 售后列表、状态筛选、同意/拒绝处理 |

---

## 文档

| 文档 | 说明 |
|------|------|
| [API 接口文档](API.md) | 所有 REST API 接口、请求/响应示例、数据库设计 |
| [开发指南](DEV_GUIDE.md) | 配置说明、代码规范、异常体系、Redis 缓存策略、单元测试 |
