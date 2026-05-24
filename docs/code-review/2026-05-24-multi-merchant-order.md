# 多商家订单拆分 - 代码审查报告

**日期**: 2026-05-24
**分支**: develop
**功能**: 多商家订单拆分 + 批量支付

## 变更概览

将原来的单订单模型改为多商家订单拆分模型：用户结算多商家商品时，后端按商家自动拆分为独立订单，共享 `batch_no` 批次号用于批量支付。

## 改动文件

### 后端 (Java)

| 文件 | 改动类型 | 说明 |
|------|---------|------|
| `ecommerce-pojo/.../entity/Order.java` | 修改 | 新增 `batchNo` 字段 |
| `ecommerce-pojo/.../vo/OrderVO.java` | 修改 | 新增 `batchNo` 字段，确保前端可获取 |
| `ecommerce-pojo/.../dto/OrderDTO.java` | 修改 | 移除 `addressId`，增加 `couponIds` 列表 |
| `ecommerce-core/.../impl/OrderServiceImpl.java` | 重构 | createOrder 拆分为商家分组、优惠券分配、批量创建等私有方法 |
| `ecommerce-core/.../impl/CartServiceImpl.java` | 修改 | 修正 MerchantMapper import 路径 |

### 前端 (Vue)

| 文件 | 改动类型 | 说明 |
|------|---------|------|
| `user-frontend/src/components/CartSidebar.vue` | 修改 | 购物车按商家分组显示 |
| `user-frontend/src/views/OrderConfirm.vue` | 修改 | 确认订单页按商家分组，提交时拆分 |
| `user-frontend/src/views/Payment.vue` | 修改 | 支持 batchNo 路由参数，批量加载订单 |
| `user-frontend/src/router/index.js` | 修改 | 支付路由改为 `/payment/:batchNo` |

## 关键设计决策

### 1. 订单拆分策略
- **方案**: 后端按 `merchantId` 分组，每个商家创建独立订单
- **无父子订单**: 同一批次的订单是平级关系，通过 `batch_no` 关联
- **理由**: 简化模型，每个商家独立管理自己的订单状态和售后

### 2. 优惠券分配
- **策略**: 按商家小计占比按比例分配优惠券面值
- **公式**: `商家优惠 = (商家小计 / 总金额) * 优惠券面值`
- **精度**: 使用 `BigDecimal` 保留2位小数，尾差归最后一个商家

### 3. 批量支付
- **路由**: `/payment/:batchNo` 通过批次号加载所有关联订单
- **API**: `POST /order/pay` 接收 `batchNo`，批量更新订单状态
- **总金额**: 前端汇总所有订单的 `payAmount`

## 发现的问题及修复

### Bug 1: MerchantMapper Bean 冲突
- **现象**: `ConflictingBeanDefinitionException` 启动失败
- **原因**: `com.ecommerce.mapper.MerchantMapper` 和 `com.ecommerce.mapper.Shop.MerchantMapper` 同名
- **修复**: 删除重复的 Mapper，统一使用 `Shop.MerchantMapper`，更新所有 import

### Bug 2: OrderVO 缺少 batchNo 字段
- **现象**: 前端提交订单后无法跳转支付页（`batchNo` 为 undefined）
- **原因**: Order 实体有 `batchNo` 但 OrderVO 未包含，转换时丢失
- **修复**: OrderVO 新增 `batchNo` 字段，转换方法中填充

### Bug 3: CartSidebar 读取 userId 键名不一致
- **现象**: 购物车显示空
- **原因**: CartSidebar 读 `localStorage.getItem('userId')`，但登录只设置了 `loginUser`
- **修复**: 登录流程同时设置 `userId` 到 localStorage

## E2E 验证结果

| 步骤 | 状态 | 说明 |
|------|------|------|
| 购物车商家分组 | ✅ | 4 个商家正确分组显示 |
| 订单确认页分组 | ✅ | 按商家显示小计和运费 |
| 提交订单拆分 | ✅ | 3 个独立订单创建成功 |
| batchNo 返回 | ✅ | OrderVO 正确返回 batchNo |
| 支付页加载 | ✅ | 通过 batchNo 加载 3 个订单 |
| 确认支付 | ✅ | 批量更新状态为待发货 |
| 订单列表 | ✅ | 3 个订单状态均为"待发货" |

## 未覆盖项

- Personal.vue 购物车 tab 未做商家分组（使用独立实现，非 CartSidebar 组件）
- 优惠券按比例分配的边界测试（如分配后某商家优惠为 0）
- 并发下单的库存扣减安全性

## 总结

多商家订单拆分功能整体实现良好，核心流程（购物车分组 → 订单拆分 → 批量支付）验证通过。主要问题集中在 import 冲突和 VO 字段遗漏，均已修复。
