# 开发指南

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

## 代码规范

- 后端遵循 **Controller → Service → Mapper** 三层架构
- Service 层按角色分包：`service/User/`（用户端，只读）、`service/Shop/`（商家端，可修改）
- 接口使用 **DTO 接收参数、VO 返回数据**，禁止使用 `Map<String, Object>`
- 前端 API 封装在 `src/utils/request.js`，基于 Axios 拦截器统一处理响应
- 设计令牌定义在 `src/styles/variables.css`，主色调为 `#E53935`（红色系）
- 组件库使用 Element Plus，其余 UI 自定义实现
- 用户认证使用 JWT（jjwt 0.9.1），token 存储在 localStorage 的 `loginUser` 对象中，通过 Axios 拦截器自动携带 `Authorization: Bearer <token>`
- Spring MVC 拦截器（`AuthInterceptor`）验证 token，白名单配置在 `WebMvcConfig`
- 商家端使用 SLF4J 日志，查询类 `debug`，状态变更 `info`，异常 `warn`

---

## 业务异常体系

异常按业务域分类，统一继承 `BaseException`，由 `GlobalExceptionHandler` 统一拦截并返回 `{ code, msg, data }` 格式。

```
RuntimeException
  └── BaseException (code + message)
        ├── UserException        — 用户登录/注册/信息相关
        ├── ProductException     — 商品不存在/下架/库存不足
        ├── OrderException       — 订单创建/支付/取消/确认收货
        ├── CartException        — 购物车增删改查
        ├── CouponException      — 优惠券领取/使用/过期
        ├── AddressException     — 收货地址增删改查
        ├── AfterSaleException   — 售后申请/取消/处理
        ├── CommentException     — 评论发表/删除
        └── FavoriteException    — 收藏/取消收藏
```

**使用规范：** Service 层根据业务场景抛出对应的异常类型，禁止直接使用 `BaseException`。

---

## Redis 缓存策略

| 缓存位置 | Key | 类型 | TTL | 说明 |
|----------|-----|------|-----|------|
| `UserProductServiceImpl.listProducts()` | `products:all` | String | 5 分钟 | 商品列表 |
| `UserProductServiceImpl.getProductById()` | `product:{id}` | String | 5 分钟 | 商品详情 |
| `UserCategoryServiceImpl.listCategories()` | `categories:all` | String | 30 分钟 | 分类列表 |
| `CouponServiceImpl.listCoupons()` | `coupons:active` | String | 10 分钟 | 优惠券列表 |
| `UserServiceImpl.getUserById()` | `user:{id}` | String | 3 分钟 | 用户信息 |
| `SalesRankInitRunner` | `product:sales_rank` | ZSet | 无 | 热销排行（启动时预热） |
| `SalesRankCacheTask` | `product:name:map` | Hash | 无 | 商品 ID→名称映射（cache-aside 按需回填） |
| `SalesRankCacheTask` | `cache:hot_sales:top10` | String | 35 秒 | 热销榜单快照缓存 |
| `CommentServiceImpl.getCommentListByProductId()` | `comment:list:{productId}` | String | 10 分钟 | 商品评论列表 |
| `CommentServiceImpl.getCommentStats()` | `comment:stats:{productId}` | String | 10 分钟 | 评论统计（平均分+数量） |

**缓存清除策略：**
- 商品名映射 Hash（`product:name:map`）：修改/删除商品时删除对应字段，新增无需操作（cache-aside 自动回填）
- 评论相关缓存（`comment:list:{productId}` + `comment:stats:{productId}`）：新增/删除评论时同步清除
- 其他缓存由商家端在修改/删除数据时主动调用清除方法

缓存 Key 常量定义在 `ecommerce-common` 模块的 `RedisKeys` 类中。

---

## 单元测试

```bash
cd backend/ecommerce-core
$env:JAVA_HOME="E:\jdk1.8"
$env:PATH="E:\jdk1.8\bin;" + $env:PATH
mvn test "-Dtest=com.ecommerce.GlobalExceptionHandlerTest"
```
